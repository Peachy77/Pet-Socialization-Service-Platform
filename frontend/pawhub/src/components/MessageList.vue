<template>
  <div class="message-list">
    <!-- 点赞 -->
    <div class="message-item" @click="openChat('猫咪铲屎官', 'https://randomuser.me/api/portraits/women/44.jpg', 'like')">
      <div class="dot"></div>

      <img class="avatar" src="https://randomuser.me/api/portraits/women/44.jpg" />

      <div class="message-content">
        <div class="name">
          猫咪铲屎官 <span class="like">❤</span>
        </div>
        <div class="text">赞了你的动态</div>
        <div class="time">2小时前</div>
      </div>
    </div>


    <!-- 评论 -->
    <div class="message-item" @click="openChat('金毛家长', 'https://randomuser.me/api/portraits/men/32.jpg', 'comment')">
      <div class="dot"></div>

      <img class="avatar" src="https://randomuser.me/api/portraits/men/32.jpg" />

      <div class="message-content">
        <div class="name">
          金毛家长 <span class="comment">💬</span>
        </div>
        <div class="text">评论了：好可爱的狗狗！</div>
        <div class="time">5小时前</div>
      </div>
    </div>


    <!-- 系统消息 -->
    <div class="message-item system-message">
      <div class="system-icon">🔔</div>

      <div class="message-content">
        <div class="text">你的预约已确认，请准时到店</div>
        <div class="time">1天前</div>
      </div>
    </div>


    <!-- 点赞 -->
    <div class="message-item" @click="openChat('萌宠达人', 'https://randomuser.me/api/portraits/women/65.jpg', 'like')">
      <img class="avatar" src="https://randomuser.me/api/portraits/women/65.jpg" />

      <div class="message-content">
        <div class="name">
          萌宠达人 <span class="like">❤</span>
        </div>
        <div class="text">赞了你的动态</div>
        <div class="time">1天前</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "MessageList",
  data() {
    return {
      messages: []
    }
  },
  methods: {
    openChat(username, avatar, type) {
      this.$router.push({
        name: "messagesDetails",
        query: {
          username,
          avatar,
          type
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
  padding: 16px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background 0.2s;
}

.message-item:not(.system-message):hover {
  background: #f9f9fb;
}

.message-item.system-message {
  cursor: default;
}

/* 未读点 */
.dot {
  width: 8px;
  height: 8px;
  background: #8a84c8;
  border-radius: 50%;
  margin-right: 8px;
}

/* 头像 */
.avatar {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  margin-right: 12px;
}

/* 系统图标 */
.system-icon {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: #f0eef8;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  margin-right: 12px;
}

/* 内容 */
.message-content {
  text-align: left;
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

/* 图标 */
.like {
  color: red;
  margin-left: 4px;
}

.comment {
  margin-left: 4px;
}
</style>
