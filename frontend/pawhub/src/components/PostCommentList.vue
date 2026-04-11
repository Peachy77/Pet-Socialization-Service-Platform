<template>
  <div class="comments-block">
    <div class="comments-title">全部评论 <span>({{ comments.length }})</span></div>

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

        <div class="comment-actions">
          <button class="action-btn" @click="onLike(comment)">
            <span :class="['like-icon', { liked: comment.liked }]">
              {{ comment.liked ? '♥' : '♡' }}
            </span>
            <span>{{ comment.likes }}</span>
          </button>
          <button class="action-btn" @click="toggleReply(comment.id)">
            回复 {{ comment.replyCount }}
          </button>
        </div>

        <div v-if="activeReplyId === comment.id" class="reply-box">
          <input
            v-model.trim="replyDraft"
            type="text"
            class="reply-input"
            placeholder="回复这条评论..."
            @keyup.enter="submitReply(comment)"
          />
          <button class="reply-send" @click="submitReply(comment)">发送</button>
        </div>

        <div v-if="comment.lastReply" class="reply-preview">
          你回复了：{{ comment.lastReply }}
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
    }
  },

  data() {
    return {
      activeReplyId: null,
      replyDraft: ""
    }
  },

  methods: {
    onLike(comment) {
      this.$emit("like-comment", comment.id)
    },

    toggleReply(commentId) {
      if (this.activeReplyId === commentId) {
        this.activeReplyId = null
        this.replyDraft = ""
        return
      }

      this.activeReplyId = commentId
      this.replyDraft = ""
    },

    submitReply(comment) {
      if (!this.replyDraft) {
        return
      }

      this.$emit("reply-comment", {
        id: comment.id,
        text: this.replyDraft
      })

      this.replyDraft = ""
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

.like-icon {
  color: #6b6291;
  font-size: 13px;
  line-height: 1;
}

.like-icon.liked {
  color: #ff4d5a;
}

.reply-box {
  display: flex;
  gap: 8px;
  margin-top: 10px;
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

.reply-preview {
  margin-top: 8px;
  font-size: 13px;
  color: #5f5880;
  background: #f5f2fc;
  border-radius: 8px;
  padding: 6px 10px;
}
</style>
