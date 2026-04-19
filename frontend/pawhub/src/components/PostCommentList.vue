<template>
  <div class="comments-block">
    <div class="comments-title">全部评论 <span>({{ comments.length }})</span></div>

    <input
      ref="replyImageInput"
      type="file"
      accept="image/*"
      class="reply-image-input"
      @change="handleReplyImageChange"
    />

    <div
      v-for="comment in comments"
      :key="comment.id"
      class="comment-item"
    >
      <img :src="comment.avatar" class="comment-avatar" />

      <div class="comment-main">
        <div class="comment-head">
          <span class="comment-name">{{ comment.name }}</span>
          <span class="comment-time">{{ comment.time }}</span>
        </div>

        <div class="comment-text">{{ comment.content }}</div>

        <div v-if="comment.image" class="comment-image-wrap">
          <img :src="comment.image" class="comment-image" alt="评论图片" />
        </div>

        <div class="comment-actions">
          <button class="action-btn" :class="{ liked: comment.liked }" @click="onLike(comment)">
            <svg viewBox="0 0 24 24" class="heart-icon" aria-hidden="true">
              <path
                d="M12 21s-7.2-4.7-9.6-9C.6 8.7 2.1 5.2 5.6 5c2.1-.1 3.4 1 4.4 2.2C11 6 12.3 4.9 14.4 5c3.5.2 5 3.7 3.2 7-2.4 4.3-9.6 9-9.6 9z"
              />
            </svg>
            <span>{{ comment.likes }}</span>
          </button>
          <button class="action-btn" @click="toggleReply(comment.id)">
            回复 {{ comment.replyCount }}
          </button>
        </div>

        <div v-if="activeReplyId === comment.id" class="reply-box">
          <button type="button" class="reply-attach" @click="triggerReplyImage">＋</button>
          <textarea
            v-model.trim="replyDraft"
            class="reply-input"
            placeholder="回复这条评论...支持多行输入"
          ></textarea>
          <button class="reply-send" @click="submitReply(comment)">发送</button>
        </div>

        <div v-if="activeReplyId === comment.id && replyImage" class="reply-image-preview">
          <img :src="replyImage" alt="待发送回复图片" />
          <button class="reply-image-remove" @click="clearReplyImage">×</button>
        </div>

        <div v-if="replyItems(comment).length" class="reply-preview-list">
          <div
            v-for="(reply, index) in replyItems(comment)"
            :key="`${comment.id}-reply-${index}`"
            class="reply-preview"
          >
            <img
              :src="reply.avatar || currentUserAvatar || comment.avatar"
              class="reply-user-avatar"
              alt="我的头像"
            />
            <div class="reply-preview-content">
              <div v-if="reply.text">你回复了（{{ reply.targetName || comment.name }}）：{{ reply.text }}</div>
              <div v-else>你回复了（{{ reply.targetName || comment.name }}）：[图片]</div>
              <img v-if="reply.image" :src="reply.image" class="reply-preview-image" alt="回复图片" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "PostCommentList",

  props: {
    comments: {
      type: Array,
      default: () => []
    },
    currentUserAvatar: {
      type: String,
      default: ""
    }
  },

  data() {
    return {
      activeReplyId: null,
      replyDraft: "",
      replyImage: ""
    }
  },

  methods: {
    onLike(comment) {
      this.$emit("like-comment", comment.id)
    },

    replyItems(comment) {
      if (Array.isArray(comment.replies)) {
        return comment.replies.map(reply => ({
          ...reply,
          targetName: reply.targetName || comment.name
        }))
      }

      if (comment.lastReply) {
        return [{
          text: comment.lastReply,
          targetName: comment.name
        }]
      }

      return []
    },

    toggleReply(commentId) {
      if (this.activeReplyId === commentId) {
        this.activeReplyId = null
        this.replyDraft = ""
        this.clearReplyImage()
        return
      }

      this.activeReplyId = commentId
      this.replyDraft = ""
      this.clearReplyImage()
    },

    triggerReplyImage() {
      if (this.$refs.replyImageInput) {
        this.$refs.replyImageInput.click()
      }
    },

    handleReplyImageChange(event) {
      const file = event.target.files && event.target.files[0]

      if (!file) {
        return
      }

      const reader = new FileReader()
      reader.onload = () => {
        this.replyImage = reader.result
      }
      reader.readAsDataURL(file)
    },

    clearReplyImage() {
      this.replyImage = ""
      if (this.$refs.replyImageInput) {
        this.$refs.replyImageInput.value = ""
      }
    },

    submitReply(comment) {
      if (!this.replyDraft && !this.replyImage) {
        return
      }

      this.$emit("reply-comment", {
        id: comment.id,
        targetName: comment.name,
        text: this.replyDraft,
        image: this.replyImage
      })

      this.replyDraft = ""
      this.clearReplyImage()
      this.activeReplyId = null
    }
  }
}
</script>

<style scoped>
.comments-block {
  background: #fff;
  padding: 14px 18px 16px;
}

.comments-title {
  font-size: 18px;
  font-weight: 600;
  color: #27242d;
  margin-bottom: 8px;
}

.comments-title span {
  font-weight: 500;
  color: #7e7887;
}

.comment-item {
  display: flex;
  gap: 12px;
  padding: 14px 0 10px;
  border-top: 1px solid #f0e9f3;
}

.comment-item:first-of-type {
  border-top: none;
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  flex: none;
}

.comment-main {
  flex: 1;
  min-width: 0;
  text-align: left;
}

.comment-head {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 4px;
}

.comment-name {
  font-size: 15px;
  color: #3b3030;
}

.comment-time {
  font-size: 12px;
  color: #a8a0ac;
}

.comment-text {
  font-size: 15px;
  line-height: 1.7;
  color: #4d4040;
  text-align: left;
}

.comment-actions {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  border: none;
  background: #f2eefb;
  color: #6b6291;
  border-radius: 999px;
  font-size: 12px;
  padding: 5px 10px;
  cursor: pointer;
}

.heart-icon {
  width: 14px;
  height: 14px;
}

.heart-icon path {
  fill: transparent;
  stroke: #6b6291;
  stroke-width: 1.8;
  transition: 0.2s;
}

.action-btn.liked {
  color: #ff4d5a;
}

.action-btn.liked .heart-icon path {
  fill: #ff4d5a;
  stroke: #ff4d5a;
}

.comment-image-wrap {
  margin-top: 10px;
}

.comment-image {
  width: min(240px, 100%);
  border-radius: 10px;
  display: block;
  object-fit: cover;
}

.reply-image-input {
  display: none;
}

.reply-box {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}

.reply-attach {
  width: 34px;
  height: 34px;
  border: none;
  border-radius: 8px;
  background: #f2eefb;
  color: #7b67c7;
  font-size: 24px;
  line-height: 1;
  cursor: pointer;
}

.reply-input {
  flex: 1;
  min-width: 0;
  height: 34px;
  border: 1px solid #e4ddf2;
  border-radius: 8px;
  padding: 0 10px;
  font-size: 13px;
  outline: none;
}

.reply-input:focus {
  border-color: #8673d6;
}

.reply-send {
  border: none;
  background: #8673d6;
  color: #fff;
  border-radius: 8px;
  padding: 0 10px;
  cursor: pointer;
  font-size: 12px;
}

.reply-image-preview {
  position: relative;
  margin-top: 8px;
  width: 88px;
}

.reply-image-preview img {
  width: 88px;
  height: 88px;
  border-radius: 8px;
  object-fit: cover;
  display: block;
}

.reply-image-remove {
  position: absolute;
  right: -8px;
  top: -8px;
  width: 20px;
  height: 20px;
  border: none;
  border-radius: 50%;
  background: #8673d6;
  color: #fff;
  font-size: 12px;
  line-height: 1;
  cursor: pointer;
}

.reply-preview {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  margin-top: 8px;
  font-size: 13px;
  color: #5f5880;
  background: #f5f2fc;
  border-radius: 8px;
  padding: 6px 10px;
}

.reply-user-avatar {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  object-fit: cover;
  flex: none;
}

.reply-preview-content {
  flex: 1;
  min-width: 0;
}

.reply-preview-image {
  margin-top: 6px;
  width: min(200px, 100%);
  border-radius: 8px;
  object-fit: cover;
  display: block;
}
</style>
