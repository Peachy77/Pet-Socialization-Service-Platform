<template>
  <div class="message-details-page">
    <!-- 顶部 -->
    <div class="header">
      <button class="back-btn" @click="goBack">
        <span>←</span>
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
        :key="idx"
        :class="['message-bubble', msg.isSent ? 'sent' : 'received']"
      >
        <img v-if="!msg.isSent" :src="conversation.avatar" class="bubble-avatar" />
        <div class="bubble-content">{{ msg.text }}</div>
        <div class="bubble-time">{{ msg.time }}</div>
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
export default {
  name: "MessagesDetailsView",
  data() {
    return {
      inputText: "",
      conversation: {
        avatar: "",
        username: "",
        type: ""
      },
      messages: [
        {
          text: "你好，最近怎么样？",
          isSent: false,
          time: "10:30"
        },
        {
          text: "很好啊，你呢？",
          isSent: true,
          time: "10:31"
        },
        {
          text: "我也不错，改天约一起玩",
          isSent: false,
          time: "10:32"
        },
        {
          text: "好啊，周末可以吗？",
          isSent: true,
          time: "10:33"
        }
      ]
    }
  },
  mounted() {
    // 从路由参数获取对话信息
    const queryData = this.$route.query
    this.conversation = {
      avatar: queryData.avatar || "https://randomuser.me/api/portraits/women/44.jpg",
      username: queryData.username || "用户",
      type: queryData.type || "like"
    }
    this.scrollToBottom()
  },
  methods: {
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
