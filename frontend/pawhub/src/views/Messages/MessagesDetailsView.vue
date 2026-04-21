<template>
  <div class="message-details-page">
    <!-- 顶部 -->
    <div class="header">
      <button class="back-btn" @click="goBack">
        <span>‹</span>
      </button>
      <div class="user-info">
        <img :src="conversation.avatar" class="avatar" />
        <div>
          <div class="name">{{ conversation.username }}</div>
          <div class="status" :class="conversationStatusClass">{{ conversationStatusText }}</div>
        </div>
      </div>
      <div class="more"></div>
    </div>

    <!-- 消息区域 -->
    <div class="message-area" ref="messageArea">
      <div 
        v-for="(msg, idx) in messages" 
        :key="msg.id || idx"
        :class="['message-bubble', msg.isSent ? 'sent' : 'received', msg.type === 'image' ? 'image-message' : 'text-message']"
      >
        <img v-if="!msg.isSent" :src="conversation.avatar" class="bubble-avatar" />
        <div class="bubble-body">
          <div v-if="msg.type === 'text'" class="bubble-content">
            <div v-if="msg.text" class="bubble-text">{{ msg.text }}</div>
          </div>
          <img v-else-if="msg.type === 'image'" :src="msg.imageUrl" class="standalone-image" />
          <div class="bubble-meta">
            <span class="bubble-time">{{ msg.time }}</span>
            <span v-if="msg.isSent" class="bubble-read-state" :class="msg.readStateClass">{{ msg.readStateText }}</span>
          </div>
        </div>
        <img v-if="msg.isSent" :src="currentUser.avatar" class="bubble-avatar" />
      </div>
    </div>

    <!-- 输入框 -->
    <div class="input-area">
      <button class="image-btn" :disabled="sending || uploadingImage || !conversation.targetUserId" @click="triggerImagePicker">
        <span class="image-plus">+</span>
        <span v-if="pendingImageFiles.length" class="image-count">{{ pendingImageFiles.length }}</span>
      </button>
      <input
        ref="imageInput"
        class="hidden-image-input"
        type="file"
        accept="image/*"
        @change="handleImageSelect"
      />
      <input 
        v-model="inputText" 
        type="text" 
        placeholder="输入消息..."
        :disabled="sending || uploadingImage || !conversation.targetUserId"
        @keyup.enter="sendMessage"
      />
      <button class="send-btn" :disabled="sending || uploadingImage || !conversation.targetUserId" @click="sendMessage">{{ sending ? '发送中...' : '发送' }}</button>
    </div>

    <div v-if="pendingImageFiles.length" class="pending-image-preview">
      <div v-for="item in pendingImageFiles" :key="item.id" class="pending-image-item">
        <img :src="item.previewUrl" alt="待发送图片" />
        <button class="remove-pending-image-btn" @click="removePendingImage(item.id)">×</button>
      </div>
    </div>
  </div>
</template>

<script>
import { createPrivateMessage, getConversationMessages, markConversationAsRead } from "@/api/messages"
import { uploadFile } from "@/api/upload"

export default {
  name: "MessagesDetailsView",
  data() {
    return {
      inputText: "",
      loading: false,
      sending: false,
      uploadingImage: false,
      pendingImageFiles: [],
      page: 1,
      pageSize: 20,
      conversation: {
        targetUserId: "",
        avatar: "",
        username: "",
        type: ""
      },
      currentUser: {
        id: "",
        username: "我",
        avatar: "https://images.unsplash.com/photo-1601758123927-1967a3f7f3b4"
      },
      messages: []
    }
  },
  computed: {
    conversationStatusText() {
      return this.getConversationStatus().text
    },

    conversationStatusClass() {
      return this.getConversationStatus().state
    }
  },
  async mounted() {
    this.loadCurrentUser()

    const queryData = this.$route.query || {}
    this.conversation = {
      targetUserId: String(queryData.targetUserId || queryData.userId || queryData.id || ""),
      avatar: queryData.avatar || "https://randomuser.me/api/portraits/women/44.jpg",
      username: queryData.username || "用户",
      type: queryData.type || "like"
    }

    await this.loadConversationMessages()
    await this.markCurrentConversationAsRead()

    this.scrollToBottom()
  },
  beforeDestroy() {
    this.cleanupPendingImagePreviews()
  },
  methods: {
    loadCurrentUser() {
      const stored = JSON.parse(localStorage.getItem("pawhub_user_profile") || "null")

      this.currentUser = {
        id: String(localStorage.getItem("userId") || stored?.id || stored?.userId || ""),
        username: stored?.username || stored?.name || "我",
        avatar: stored?.avatar || this.currentUser.avatar
      }
    },

    async loadConversationMessages() {
      if (!this.conversation.targetUserId) {
        this.messages = []
        return
      }

      this.loading = true

      try {
        const response = await getConversationMessages(this.conversation.targetUserId, {
          page: this.page,
          pageSize: this.pageSize
        })

        const payload = this.unwrapPayload(response)
        const list = this.extractList(payload)
        this.messages = list
          .map(item => this.mapMessage(item))
          .filter(Boolean)
          .flatMap(item => this.expandMessageUnits(item))
      } catch (error) {
        const msg = error?.response?.data?.message || error?.message || "加载聊天记录失败"
        this.$message?.error?.(msg)
        this.messages = []
      } finally {
        this.loading = false
      }
    },

    async markCurrentConversationAsRead() {
      if (!this.conversation.targetUserId) {
        return
      }

      try {
        await markConversationAsRead(this.conversation.targetUserId)
      } catch (error) {
        const msg = error?.response?.data?.message || error?.message || "标记已读失败"
        this.$message?.warning?.(msg)
      }
    },

    extractList(payload) {
      if (Array.isArray(payload)) return payload
      if (Array.isArray(payload?.list)) return payload.list
      if (Array.isArray(payload?.records)) return payload.records
      if (Array.isArray(payload?.items)) return payload.items
      if (Array.isArray(payload?.rows)) return payload.rows
      if (Array.isArray(payload?.content)) return payload.content
      if (Array.isArray(payload?.data)) return payload.data
      return []
    },

    mapMessage(message) {
      if (!message || typeof message !== "object") return null

      const senderId = String(message.senderId ?? message.sender_id ?? message.fromUserId ?? message.from_user_id ?? "")
      const currentUserId = String(this.currentUser.id || "")
      const imageList = message.images ?? message.imageUrls ?? message.image_urls ?? []
      const sentByCurrentUser = Boolean(currentUserId && senderId && currentUserId === senderId)
      const readState = this.resolveReadState(message)

      return {
        id: message.id ?? message.messageId ?? message.message_id ?? `${senderId}-${message.createTime || message.create_time || Date.now()}`,
        text: message.content || message.text || message.message || "",
        isSent: sentByCurrentUser,
        time: this.formatMessageTime(message.createTime || message.create_time || message.time || message.createdAt || message.created_at),
        images: Array.isArray(imageList) ? imageList.map(image => this.toDisplayImageUrl(image)).filter(Boolean) : [],
        rawTime: message.createTime || message.create_time || message.time || message.createdAt || message.created_at,
        readStateText: sentByCurrentUser ? (readState === true ? "已读" : "未读") : "",
        readStateClass: sentByCurrentUser ? (readState === true ? "is-read" : "is-unread") : ""
      }
    },

    expandMessageUnits(message) {
      const result = []
      const text = String(message?.text || "").trim()

      if (text) {
        result.push({
          ...message,
          id: `${message.id}-text`,
          type: "text"
        })
      }

      const images = Array.isArray(message?.images) ? message.images : []
      images.forEach((imageUrl, index) => {
        result.push({
          ...message,
          id: `${message.id}-image-${index}`,
          type: "image",
          imageUrl
        })
      })

      return result
    },

    resolveReadState(message) {
      const directValue = message.read ?? message.isRead ?? message.readStatus ?? message.is_read ?? message.status

      if (typeof directValue === "boolean") return directValue
      if (typeof directValue === "number") return directValue === 1
      if (typeof directValue === "string") {
        const normalized = directValue.trim().toLowerCase()
        if (["read", "1", "yes", "true", "already_read", "已读"].includes(normalized)) return true
        if (["unread", "0", "no", "false", "not_read", "未读"].includes(normalized)) return false
      }

      if (message.readAt || message.read_at || message.readTime || message.read_time) {
        return true
      }

      return null
    },

    getMessageTimestamp(message) {
      const value = message?.rawTime || message?.createTime || message?.create_time || message?.time || message?.createdAt || message?.created_at
      const date = value instanceof Date ? value : new Date(value)
      return Number.isNaN(date.getTime()) ? null : date.getTime()
    },

    getConversationStatus() {
      const latestOtherUserMessageTime = this.messages
        .filter(message => !message.isSent)
        .map(message => this.getMessageTimestamp(message))
        .filter(timestamp => timestamp !== null)
        .reduce((latest, timestamp) => Math.max(latest, timestamp), 0)

      if (!latestOtherUserMessageTime) {
        return { state: "offline", text: "离线" }
      }

      const elapsedMinutes = (Date.now() - latestOtherUserMessageTime) / 60000
      if (elapsedMinutes <= 5) {
        return { state: "online", text: "在线" }
      }

      return { state: "offline", text: "离线" }
    },

    parseDateTime(value) {
      if (!value) return null
      const date = value instanceof Date ? value : new Date(value)
      return Number.isNaN(date.getTime()) ? null : date
    },

    formatMessageTime(value) {
      const date = this.parseDateTime(value)
      if (!date) return ""
      const hour = String(date.getHours()).padStart(2, "0")
      const minute = String(date.getMinutes()).padStart(2, "0")
      return `${hour}:${minute}`
    },

    unwrapPayload(response) {
      const code = response?.code
      const normalizedCode = code === undefined || code === null ? null : String(code)
      const isBusinessSuccess =
        normalizedCode === null ||
        normalizedCode === "0" ||
        normalizedCode === "1" ||
        normalizedCode === "200" ||
        response?.success === true ||
        response?.success === 1

      if (!isBusinessSuccess) {
        throw new Error(response?.message || response?.msg || "请求失败")
      }

      if (response && Object.prototype.hasOwnProperty.call(response, "data")) {
        return response.data
      }

      return response
    },

    async refreshConversation() {
      await this.loadConversationMessages()
      await this.markCurrentConversationAsRead()
      this.$nextTick(() => {
        this.scrollToBottom()
      })
    },

    triggerImagePicker() {
      if (this.sending || this.uploadingImage || !this.conversation.targetUserId) return
      this.$refs.imageInput?.click()
    },

    getUploadedImageUrl(response) {
      return response?.data?.data || response?.data?.url || response?.url || response?.data || ""
    },

    normalizeUploadPath(path) {
      if (!path) return ""
      if (/^https?:\/\//.test(path)) return path
      if (path.startsWith("/")) return path
      if (path.startsWith("uploads/")) return `/${path}`
      if (path.startsWith("uploads\\")) return `/${path.replace(/\\/g, "/")}`
      return path
    },

    toDisplayImageUrl(path) {
      const normalized = this.normalizeUploadPath(path)
      if (!normalized) return ""
      if (/^https?:\/\//.test(normalized)) return normalized
      if (normalized.startsWith("/uploads")) return `http://localhost:8080${normalized}`
      return normalized
    },

    clearImageInput() {
      if (this.$refs.imageInput) {
        this.$refs.imageInput.value = ""
      }
    },

    removePendingImage(imageId) {
      const target = this.pendingImageFiles.find(item => item.id === imageId)
      if (target?.previewUrl && target.previewUrl.startsWith("blob:")) {
        URL.revokeObjectURL(target.previewUrl)
      }
      this.pendingImageFiles = this.pendingImageFiles.filter(item => item.id !== imageId)
    },

    cleanupPendingImagePreviews() {
      this.pendingImageFiles.forEach(item => {
        if (item?.previewUrl && item.previewUrl.startsWith("blob:")) {
          URL.revokeObjectURL(item.previewUrl)
        }
      })
      this.pendingImageFiles = []
    },

    async uploadPendingImages() {
      const files = Array.isArray(this.pendingImageFiles) ? this.pendingImageFiles : []
      const urls = []

      for (const item of files) {
        const uploadResponse = await uploadFile(item.file)
        const imageUrl = this.toDisplayImageUrl(this.getUploadedImageUrl(uploadResponse))
        if (!imageUrl || typeof imageUrl !== "string") {
          throw new Error("图片上传成功但未返回可用地址")
        }
        urls.push(imageUrl)
      }

      return urls
    },

    async handleImageSelect(event) {
      const file = event?.target?.files?.[0]
      if (!file || this.uploadingImage || !this.conversation.targetUserId) {
        this.clearImageInput()
        return
      }

      this.uploadingImage = true

      try {
        const previewUrl = URL.createObjectURL(file)
        this.pendingImageFiles.push({
          id: `${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
          file,
          previewUrl
        })
      } catch (error) {
        const msg = error?.response?.data?.message || error?.message || "图片选择失败"
        this.$message?.error?.(msg)
      } finally {
        this.uploadingImage = false
        this.clearImageInput()
      }
    },

    goBack() {
      this.$router.back()
    },
    async sendMessage() {
      const content = this.inputText.trim()
      const hasPendingImages = Array.isArray(this.pendingImageFiles) && this.pendingImageFiles.length > 0

      if ((!content && !hasPendingImages) || !this.conversation.targetUserId || this.sending || this.uploadingImage) return

      this.sending = true

      try {
        const images = hasPendingImages ? await this.uploadPendingImages() : []

        await createPrivateMessage({
          receiver_id: Number(this.conversation.targetUserId),
          content,
          images
        })

        this.inputText = ""
        this.cleanupPendingImagePreviews()
        await this.refreshConversation()
      } catch (error) {
        const msg = error?.response?.data?.message || error?.message || "发送失败"
        this.$message?.error?.(msg)
      } finally {
        this.sending = false
      }
    },
    scrollToBottom() {
      if (this.$refs.messageArea) {
        this.$refs.messageArea.scrollTop = this.$refs.messageArea.scrollHeight
      }
    }
  }
}
</script>

<style scoped>
.message-details-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f5f7;
}

/* 顶部 */
.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: white;
  padding: 12px 16px;
  border-bottom: 1px solid #eee;
}

.back-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-info {
  display: flex;
  align-items: center;
  flex: 1;
  margin-left: 8px;
}

.user-info .avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 12px;
}

.name {
  font-size: 16px;
  font-weight: 600;
}

.status {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.status.online {
  color: #2f9e44;
}

.status.offline {
  color: #999;
}

.more {
  width: 40px;
}

/* 消息区域 */
.message-area {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
}

/* 消息气泡 */
.message-bubble {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12px;
  gap: 8px;
}

.message-bubble.received {
  justify-content: flex-start;
}

.message-bubble.sent {
  justify-content: flex-end;
}

.bubble-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  flex-shrink: 0;
}

.bubble-body {
  display: flex;
  flex-direction: column;
  max-width: 70%;
}

.bubble-content {
  padding: 10px 12px;
  border-radius: 8px;
  word-break: break-word;
  font-size: 14px;
}

.message-bubble.received .bubble-content {
  background: white;
  color: #333;
}

.message-bubble.sent .bubble-content {
  background: #8673d6;
  color: white;
}

.bubble-text + .bubble-image {
  margin-top: 8px;
}

.bubble-image {
  display: block;
  max-width: 100%;
  margin-top: 8px;
  border-radius: 8px;
}

.standalone-image {
  display: block;
  width: auto;
  max-width: 50vw;
  max-height: 240px;
  height: auto;
  border-radius: 12px;
  object-fit: cover;
  box-shadow: 0 3px 12px rgba(0, 0, 0, 0.1);
}

.bubble-meta {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 8px;
  margin-top: 8px;
  font-size: 12px;
  line-height: 1;
}

.message-bubble.sent .bubble-meta {
  justify-content: flex-end;
}

.bubble-time {
  color: rgba(153, 153, 153, 0.95);
}

.bubble-read-state {
  padding: 0;
  border-radius: 0;
  font-weight: 500;
  color: #999;
}

.bubble-read-state.is-read {
  color: #999;
  background: transparent;
}

.bubble-read-state.is-unread {
  color: #999;
  background: transparent;
}

/* 输入框 */
.input-area {
  display: flex;
  align-items: center;
  gap: 8px;
  background: white;
  padding: 12px 16px;
  border-top: 1px solid #eee;
}

.image-btn {
  position: relative;
  width: 36px;
  height: 36px;
  border: 1px solid #d8dce6;
  border-radius: 50%;
  background: #fff;
  color: #666;
  padding: 0;
  font-size: 20px;
  line-height: 1;
  cursor: pointer;
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.image-btn:disabled {
  color: #bbb;
  border-color: #eceff4;
  background: #f7f7f7;
  cursor: not-allowed;
}

.image-plus {
  display: inline-block;
  transform: translateY(-1px);
}

.image-count {
  position: absolute;
  top: -5px;
  right: -5px;
  min-width: 16px;
  height: 16px;
  border-radius: 10px;
  background: #ff4d4f;
  color: #fff;
  font-size: 11px;
  line-height: 16px;
  text-align: center;
  padding: 0 4px;
  box-sizing: border-box;
}

.hidden-image-input {
  display: none;
}

.pending-image-preview {
  display: flex;
  gap: 8px;
  padding: 8px 16px 10px;
  background: #fff;
  border-top: 1px solid #f1f1f1;
  overflow-x: auto;
}

.pending-image-item {
  position: relative;
  width: 64px;
  height: 64px;
  flex-shrink: 0;
}

.pending-image-item img {
  width: 64px;
  height: 64px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid #e8e8e8;
}

.remove-pending-image-btn {
  position: absolute;
  top: -6px;
  right: -6px;
  width: 18px;
  height: 18px;
  border: none;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  font-size: 12px;
  line-height: 18px;
  text-align: center;
  cursor: pointer;
}

.input-area input {
  flex: 1;
  border: 1px solid #ddd;
  border-radius: 20px;
  padding: 10px 16px;
  font-size: 14px;
  outline: none;
}

.input-area input:focus {
  border-color: #8673d6;
}

.input-area input:disabled {
  background: #f6f6f6;
  cursor: not-allowed;
}

.send-btn {
  background: #8673d6;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 10px 20px;
  font-size: 14px;
  cursor: pointer;
  font-weight: 500;
}

.send-btn:active {
  background: #7962c4;
}

.send-btn:disabled {
  background: #b9b2de;
  cursor: not-allowed;
}

/* 滚动条美化 */
.message-area::-webkit-scrollbar {
  width: 6px;
}

.message-area::-webkit-scrollbar-track {
  background: transparent;
}

.message-area::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 3px;
}

.message-area::-webkit-scrollbar-thumb:hover {
  background: #999;
}
</style>
