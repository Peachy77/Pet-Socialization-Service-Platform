<template>
  <div class="message-list">
    <div v-if="loading" class="tips">加载中...</div>
    <div v-else-if="!messages.length" class="tips">暂无消息</div>
    <div v-else>
      <div
        v-for="msg in messages"
        :key="msg.otherUserId"
        class="message-item"
        @click="openChat(msg.otherUserId, msg.otherUserName, msg.otherUserAvatar, msg.type)"
      >
        <div v-if="msg.unreadCount > 0" class="dot"></div>
        <div v-else class="dot dot-empty"></div>

        <img class="avatar" :src="msg.otherUserAvatar" />

        <div class="message-content">
          <div class="name">
            {{ msg.otherUserName }}
          </div>
          <div class="text">{{ msg.lastMessage }}</div>
          <div class="time">{{ msg.formattedTime }}</div>
        </div>

        <div v-if="msg.unreadCount > 0" class="badge">{{ msg.unreadCount }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import { getMessages } from "@/api/messages"

export default {
  name: "MessageList",
  data() {
    return {
      messages: [],
      loading: false,
      page: 1,
      pageSize: 20,
      total: 0
    }
  },
  async created() {
    await this.loadMessages()
  },
  methods: {
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

    extractList(payload) {
      if (Array.isArray(payload)) return payload
      if (Array.isArray(payload?.list)) return payload.list
      if (Array.isArray(payload?.records)) return payload.records
      if (Array.isArray(payload?.items)) return payload.items
      if (Array.isArray(payload?.rows)) return payload.rows
      if (Array.isArray(payload?.content)) return payload.content
      if (Array.isArray(payload?.data)) return payload.data
      if (Array.isArray(payload?.conversations)) return payload.conversations
      return []
    },

    formatTime(timestamp) {
      if (!timestamp) return ""

      const messageTime = new Date(timestamp)
      const now = new Date()
      const diffMs = now - messageTime
      const diffMins = Math.floor(diffMs / 60000)
      const diffHours = Math.floor(diffMs / 3600000)
      const diffDays = Math.floor(diffMs / 86400000)

      if (diffMins < 1) return "刚刚"
      if (diffMins < 60) return `${diffMins}分钟前`
      if (diffHours < 24) return `${diffHours}小时前`
      if (diffDays < 7) return `${diffDays}天前`

      const pad = (v) => String(v).padStart(2, "0")
      const year = messageTime.getFullYear()
      const month = pad(messageTime.getMonth() + 1)
      const date = pad(messageTime.getDate())

      return `${year}-${month}-${date}`
    },

    normalizeMessage(item) {
      if (!item || typeof item !== "object") return null

      const lastMessageTime = item.lastMessageTime || item.last_message_time || item.updatedAt || item.updated_at

      return {
        otherUserId: item.otherUserId ?? item.other_user_id,
        otherUserName: item.otherUserName ?? item.other_user_name ?? item.username ?? "未知用户",
        otherUserAvatar: item.otherUserAvatar ?? item.other_user_avatar ?? item.avatar ?? "https://images.unsplash.com/photo-1601758123927-1967a3f7f3b4",
        lastMessage: item.lastMessage ?? item.last_message ?? "暂无消息",
        lastMessageTime: lastMessageTime,
        formattedTime: this.formatTime(lastMessageTime),
        unreadCount: Number(item.unreadCount ?? item.unread_count ?? 0),
        type: "private" // 后期可根据 message_type 判断
      }
    },

    async loadMessages() {
      this.loading = true

      try {
        const response = await getMessages({ page: this.page, pageSize: this.pageSize })
        const payload = this.unwrapPayload(response)

        this.messages = this.extractList(payload)
          .map(item => this.normalizeMessage(item))
          .filter(Boolean)

        this.total = payload?.total ?? 0
      } catch (error) {
        const msg = error?.response?.data?.message || error?.message || "加载消息失败"
        this.$message.error(msg)
        this.messages = []
      } finally {
        this.loading = false
      }
    },

    openChat(otherUserId, otherUserName, otherUserAvatar, type) {
      this.$router.push({
        name: "messagesDetails",
        query: {
          userId: otherUserId,
          username: otherUserName,
          avatar: otherUserAvatar,
          type: type
        }
      })
    }
  }
}
</script>

<style scoped>
/* 列表 */
.message-list {
  background: white;
}

/* 每条消息 */
.message-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background 0.2s;
}

.message-item:hover {
  background: #f9f9fb;
}

/* 未读点 */
.dot {
  width: 8px;
  height: 8px;
  background: #8a84c8;
  border-radius: 50%;
  margin-right: 8px;
  flex-shrink: 0;
}

.dot-empty {
  background: transparent;
}

/* 未读数badge */
.badge {
  background: #ff4444;
  color: white;
  border-radius: 50%;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  flex-shrink: 0;
  margin-left: auto;
}

/* 提示 */
.tips {
  padding: 20px;
  text-align: center;
  color: #999;
  font-size: 14px;
}

/* 头像 */
.avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  margin-right: 12px;
  flex-shrink: 0;
  object-fit: cover;
}

/* 内容 */
.message-content {
  text-align: left;
  flex: 1;
}

.name {
  font-size: 15px;
  font-weight: 500;
}

.text {
  font-size: 14px;
  color: #666;
  margin-top: 4px;
}

.time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
</style>
