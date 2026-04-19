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
import { createPrivateMessage } from '@/api/messages';

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
      messages: []
      // messages: [
      //   {
      //     text: "你好，最近怎么样？",
      //     isSent: false,
      //     time: "10:30"
      //   },
      //   {
      //     text: "很好啊，你呢？",
      //     isSent: true,
      //     time: "10:31"
      //   },
      //   {
      //     text: "我也不错，改天约一起玩",
      //     isSent: false,
      //     time: "10:32"
      //   },
      //   {
      //     text: "好啊，周末可以吗？",
      //     isSent: true,
      //     time: "10:33"
      //   }
      // ]
    }
  },
  computed: {
    currentUserId() {
      // 从 Vuex 或 localStorage 获取当前用户ID
      return this.$store.state.user?.userId || JSON.parse(localStorage.getItem("user"))?.userId
    }
  },
  async mounted() {
    // // 从路由参数获取对话信息
    // const queryData = this.$route.query
    // this.conversation = {
    //   avatar: queryData.avatar || "https://randomuser.me/api/portraits/women/44.jpg",
    //   username: queryData.username || "用户",
    //   type: queryData.type || "like"
    // }
    // this.scrollToBottom()
     const queryData = this.$route.query
    const targetUserId = queryData.userId  // 对方用户ID
  
    if (!targetUserId) {
      console.error("缺少对方用户ID")
      return
    }
  
    // 获取对方用户信息
    await this.getUserInfo(targetUserId)
  
    // 获取聊天记录
    await this.loadMessages(targetUserId)
  
    // 标记为已读
    await this.markAsRead(targetUserId)
  
    this.scrollToBottom()
    },
  methods: {
     async getUserInfo(userId) {
    try {
      const response = await this.$api.getUser(userId)
      if (response.code === 0) {
        this.conversation = {
          avatar: response.data.avatar || "default.jpg",
          username: response.data.username,
        }
      }
    } catch (error) {
      console.error("获取用户信息失败:", error)
    }
  },
  
  // 加载聊天记录
  async loadMessages(targetUserId) {
    try {
      const response = await this.$api.getConversationMessages(targetUserId, {
        page: 1,
        pageSize: 50
      })
      if (response.code === 0) {
        // 转换后端数据格式
        this.messages = response.data.list.map(msg => ({
          text: msg.content,
          isSent: msg.sender_id === this.currentUserId,  // 根据当前用户判断
          time: this.formatTime(msg.create_time),
          messageId: msg.message_id,
          images: msg.images
        }))
      }
    } catch (error) {
      console.error("加载聊天记录失败:", error)
    }
  },
  
  // 标记会话为已读
  async markAsRead(targetUserId) {
    try {
      await this.$api.markConversationAsRead(targetUserId)
    } catch (error) {
      console.error("标记已读失败:", error)
    }
  },
  
  // 发送消息
  async sendMessage() {
    if (!this.inputText.trim()) return
    
          const content = this.inputText
      this.inputText = ""
      
      // 先显示消息（乐观更新）
      const tempMessage = {
        text: content,
        isSent: true,
        time: this.formatTime(new Date()),
        messageId: null
      }
      this.messages.push(tempMessage)
      this.$nextTick(() => this.scrollToBottom())
      
      try {
        const response = await createPrivateMessage({
          receiver_id: this.conversation.userId,
          content: content,
          images: []
        })
        
        if (response.code !== 0) {
          // 发送失败，移除临时消息并提示
          this.messages.pop()
          console.error("发送失败:", response.message)
        } else {
          // 更新临时消息的 messageId
          if (tempMessage.messageId === null && response.data) {
            tempMessage.messageId = response.data
          }
        }
      } catch (error) {
        this.messages.pop()
        console.error("发送消息失败:", error)
      }
    },
    formatTime(dateTime) {
      const date = new Date(dateTime)
      const hours = date.getHours().toString().padStart(2, '0')
      const minutes = date.getMinutes().toString().padStart(2, '0')
      return `${hours}:${minutes}`
    },
    
    goBack() {
      this.$router.back()
    },
    
    scrollToBottom() {
      if (this.$refs.messageArea) {
        this.$refs.messageArea.scrollTop = this.$refs.messageArea.scrollHeight
      }
    }
  },
  
  // // 格式化时间
  // formatTime(dateTime) {
  //   const date = new Date(dateTime)
  //   return `${date.getHours()}:${String(date.getMinutes()).padStart(2, "0")}`
  // },
    // goBack() {
    //   this.$router.back()
    // },
    // sendMessage() {
    //   if (!this.inputText.trim()) return
      
    //   const now = new Date()
    //   const time = `${now.getHours()}:${String(now.getMinutes()).padStart(2, "0")}`
      
    //   this.messages.push({
    //     text: this.inputText,
    //     isSent: true,
    //     time: time
    //   })
      
    //   this.inputText = ""
    //   this.$nextTick(() => {
    //     this.scrollToBottom()
    //   })
    // },


  // }
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
