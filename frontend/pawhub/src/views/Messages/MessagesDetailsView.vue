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
          <div class="status">在线</div>
        </div>
      </div>
      <div class="more"></div>
    </div>

    <!-- 消息区域 -->
    <div class="message-area" ref="messageArea">
      <div 
        v-for="(msg, idx) in messages" 
        :key="msg.id || idx"
        :class="['message-bubble', msg.isSent ? 'sent' : 'received']"
      >
        <img v-if="!msg.isSent" :src="conversation.avatar" class="bubble-avatar" />
        <div class="bubble-content">{{ msg.text }}</div>
        <div class="bubble-time">{{ msg.time }}</div>
        <img v-if="msg.isSent" :src="currentUser.avatar" class="bubble-avatar" />
      </div>
    </div>

    <!-- 输入框 -->
    <div class="input-area">
      <input 
        v-model="inputText" 
        type="text" 
        placeholder="输入消息..."
        @keyup.enter="sendMessage"
      />
      <button class="send-btn" @click="sendMessage">发送</button>
    </div>
  </div>
</template>

<script>
import { getConversationMessages } from "@/api/messages"

export default {
  name: "MessagesDetailsView",
  data() {
    return {
      inputText: "",
      loading: false,
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

    this.scrollToBottom()
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
        this.messages = list.map(item => this.mapMessage(item)).filter(Boolean)
      } catch (error) {
        const msg = error?.response?.data?.message || error?.message || "加载聊天记录失败"
        this.$message?.error?.(msg)
        this.messages = []
      } finally {
        this.loading = false
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

      return {
        id: message.id ?? message.messageId ?? message.message_id ?? `${senderId}-${message.createTime || message.create_time || Date.now()}`,
        text: message.content || message.text || message.message || "",
        isSent: Boolean(currentUserId && senderId && currentUserId === senderId),
        time: this.formatMessageTime(message.createTime || message.create_time || message.time || message.createdAt || message.created_at)
      }
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
        response?.success === true

      if (!isBusinessSuccess) {
        throw new Error(response?.message || response?.msg || "请求失败")
      }

      if (response && Object.prototype.hasOwnProperty.call(response, "data")) {
        return response.data
      }

      return response
    },

    goBack() {
      this.$router.back()
    },
    sendMessage() {
      if (!this.inputText.trim()) return
      
      const now = new Date()
      const time = `${now.getHours()}:${String(now.getMinutes()).padStart(2, "0")}`
      
      this.messages.push({
        text: this.inputText,
        isSent: true,
        time: time
      })
      
      this.inputText = ""
      this.$nextTick(() => {
        this.scrollToBottom()
      })
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
  align-items: flex-end;
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

.bubble-content {
  max-width: 60%;
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

.bubble-time {
  font-size: 12px;
  color: #999;
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
