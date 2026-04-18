<template>
  <div class="service-detail-page">
    <div class="top-bar">
      <button class="back-btn" @click="goBack">‹</button>
      <div class="top-title">商户详情</div>
      <button class="top-fav" @click="toggleFavorite">
        <span :class="['fav-icon', { active: favorite }]">
          {{ favorite ? '★' : '☆' }}
        </span>
      </button>
    </div>

    <div class="content-shell">
      <div v-if="service.images.length" class="gallery">
        <div
          ref="galleryTrack"
          class="gallery-track"
          @scroll="handleGalleryScroll"
        >
          <div
            v-for="(image, index) in service.images"
            :key="index"
            class="gallery-item"
          >
            <img :src="image" alt="商户详情图片" />
          </div>
        </div>

        <div v-if="service.images.length > 1" class="gallery-dots">
          <span
            v-for="(_, index) in service.images"
            :key="`dot-${index}`"
            :class="['dot', { active: index === activeImageIndex }]"
          ></span>
        </div>
      </div>

      <div class="info-card">
        <div class="title-row">
          <div class="service-name">{{ service.name }}</div>
          <div class="price-tag">{{ service.price }}</div>
        </div>

        <div class="meta-row">
          <span class="meta-item">📍 {{ service.address }}</span>
          <span class="meta-item">⭐ {{ service.rating }}</span>
          <span class="meta-item">{{ service.distance }}km</span>
        </div>

        <div class="tag-list">
          <span v-for="tag in service.tags" :key="tag" class="tag">{{ tag }}</span>
        </div>

        <div class="booking-row">
          <button class="book-btn" @click="handleBooking">预约</button>
        </div>
      </div>

      <div class="section-card">
        <div class="section-title">商户介绍</div>
        <p class="section-text">{{ service.intro }}</p>
      </div>

      <div class="section-card">
        <div class="section-title">基本项目价格介绍</div>
        <div class="project-list">
          <div v-for="item in service.projects" :key="item.name" class="project-item">
            <div class="project-name">{{ item.name }}</div>
            <div class="project-price">{{ item.price }}</div>
          </div>
        </div>
      </div>

      <PostCommentList
        :comments="commentList"
        :current-user-avatar="currentUser.avatar"
        @like-comment="handleLikeComment"
        @reply-comment="handleReplyComment"
      />
    </div>

    <div class="comment-bar">
      <input
        ref="commentImageInput"
        type="file"
        accept="image/*"
        class="image-input"
        @change="handleCommentImageChange"
      />
      <button class="attach-btn" @click="triggerCommentImage">＋</button>
      <textarea
        v-model.trim="commentDraft"
        class="input-box"
        placeholder="写评论..."
        rows="1"
      />
      <button class="send-btn" @click="submitComment">➤</button>
    </div>

    <div v-if="commentImage" class="comment-image-preview">
      <img :src="commentImage" alt="待发布图片" />
      <button class="remove-image-btn" @click="clearCommentImage">×</button>
    </div>
  </div>
</template>

<script>
import PostCommentList from "@/components/PostCommentList.vue"

export default {
  name: "ServiceDetailView",

  components: {
    PostCommentList
  },

  data() {
    return {
      activeImageIndex: 0,
      favorite: false,
      commentDraft: "",
      commentImage: "",
      extraCommentCount: 0,
      currentUser: {
        username: "宠物爱好者",
        avatar: "https://images.unsplash.com/photo-1601758123927-1967a3f7f3b4"
      },
      fallbackService: {
        id: 1,
        type: "美容",
        name: "爱宠美容工作室",
        images: [
          "https://images.unsplash.com/photo-1516734212186-a967f81ad0d7",
          "https://images.unsplash.com/photo-1583337130417-3346a1be7dee",
          "https://images.unsplash.com/photo-1507149833265-60c372daea22"
        ],
        address: "朝阳区建国路88号",
        rating: 4.8,
        distance: 1.2,
        tags: ["洗澡", "美容", "造型"],
        price: "¥88起",
        intro:
          "这是一家专注宠物美容与护理的门店，环境干净整洁，店员对宠物很耐心，适合洗护、造型和日常护理。",
        projects: [
          { name: "洗澡", price: "¥38起" },
          { name: "剪毛造型", price: "¥88起" },
          { name: "驱虫护理", price: "¥58起" },
          { name: "毛发护理", price: "¥68起" }
        ]
      },
      commentList: [
        {
          id: 1,
          name: "猫咪铲屎官",
          time: "1小时前",
          avatar: "https://i.pravatar.cc/100?img=5",
          content: "环境看起来很不错，我家猫也想去试试。",
          likes: 9,
          replyCount: 1,
          replies: [],
          lastReply: "",
          liked: false
        },
        {
          id: 2,
          name: "小鱼",
          time: "刚刚",
          avatar: "https://i.pravatar.cc/100?img=12",
          content: "价格很清楚，打算周末去看看。",
          likes: 4,
          replyCount: 2,
          replies: [],
          lastReply: "",
          liked: false
        }
      ]
    }
  },

	created() {
		this.loadCurrentUser()
	},

  computed: {
    service() {
      const query = this.$route.query || {}

      return {
        ...this.fallbackService,
        id: query.id ? Number(query.id) || this.fallbackService.id : this.fallbackService.id,
        type: query.type || this.fallbackService.type,
        name: query.name || this.fallbackService.name,
        images: this.normalizeArray(query.images || query.image, this.fallbackService.images),
        address: query.address || this.fallbackService.address,
        rating: this.normalizeNumber(query.rating, this.fallbackService.rating),
        distance: this.normalizeNumber(query.distance, this.fallbackService.distance),
        tags: this.normalizeArray(query.tags, this.fallbackService.tags),
        price: query.price || this.fallbackService.price,
        intro: query.intro || this.fallbackService.intro,
        projects: query.projects ? this.normalizeProjects(query.projects) : this.fallbackService.projects
      }
    }
  },

  methods: {
    handleGalleryScroll(event) {
      const container = event.target
      const width = container.clientWidth

      if (!width) {
        return
      }

      this.activeImageIndex = Math.round(container.scrollLeft / width)
    },

    toggleFavorite() {
      this.favorite = !this.favorite
    },

    loadCurrentUser() {
      const stored = JSON.parse(localStorage.getItem("pawhub_user_profile") || "null")

      if (!stored) {
        return
      }

      this.currentUser = {
        username: stored.username || stored.name || this.currentUser.username,
        avatar: stored.avatar || this.currentUser.avatar
      }
    },

    submitComment() {
      const text = String(this.commentDraft || "").trim()

      if (!text && !this.commentImage) {
        return
      }

      this.commentList = [{
        id: Date.now(),
        name: this.currentUser.username,
        time: this.formatNow(),
        avatar: this.currentUser.avatar,
        content: text,
        image: this.commentImage,
        likes: 0,
        replyCount: 0,
        replies: [],
        lastReply: "",
        liked: false,
        isMine: true
      }, ...this.commentList]

      this.extraCommentCount += 1
      this.commentDraft = ""
      this.clearCommentImage()
    },

    triggerCommentImage() {
      if (this.$refs.commentImageInput) {
        this.$refs.commentImageInput.click()
      }
    },

    handleCommentImageChange(event) {
      const file = event.target.files && event.target.files[0]

      if (!file) {
        return
      }

      const reader = new FileReader()
      reader.onload = () => {
        this.commentImage = reader.result
      }
      reader.readAsDataURL(file)
    },

    clearCommentImage() {
      this.commentImage = ""
      if (this.$refs.commentImageInput) {
        this.$refs.commentImageInput.value = ""
      }
    },

    handleLikeComment(commentId) {
      this.commentList = this.commentList.map(comment => {
        if (comment.id !== commentId) {
          return comment
        }

        const nextLiked = !comment.liked
        const nextLikes = nextLiked
          ? (comment.likes || 0) + 1
          : Math.max((comment.likes || 0) - 1, 0)

        return {
          ...comment,
          liked: nextLiked,
          likes: nextLikes
        }
      })
    },

    handleReplyComment(payload) {
      this.commentList = this.commentList.map(comment => {
        if (comment.id !== payload.id) {
          return comment
        }

        const nextReplies = Array.isArray(comment.replies)
          ? [...comment.replies]
          : []

        nextReplies.push({
          text: payload.text,
          image: payload.image || "",
          targetName: payload.targetName || comment.name,
          avatar: this.currentUser.avatar,
          time: this.formatNow()
        })

        return {
          ...comment,
          replyCount: (comment.replyCount || 0) + 1,
          replies: nextReplies,
          lastReply: payload.text || (payload.image ? "[图片]" : "")
        }
      })
    },

		formatNow() {
			const now = new Date()
			const pad = value => String(value).padStart(2, "0")
			return `${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}`
		},

    normalizeArray(value, fallback) {
      if (!value) {
        return Array.isArray(fallback) ? fallback : []
      }

      if (Array.isArray(value)) {
        return value
      }

      if (typeof value === "string") {
        try {
          const parsed = JSON.parse(value)

          if (Array.isArray(parsed)) {
            return parsed
          }
        } catch (error) {
          return value.split(",").map(item => item.trim()).filter(Boolean)
        }
      }

      return Array.isArray(fallback) ? fallback : []
    },

    normalizeProjects(value) {
      if (!value) {
        return this.fallbackService.projects
      }

      if (typeof value === "string") {
        try {
          const parsed = JSON.parse(value)
          if (Array.isArray(parsed)) {
            return parsed
          }
        } catch (error) {
          return this.fallbackService.projects
        }
      }

      return Array.isArray(value) ? value : this.fallbackService.projects
    },

    normalizeNumber(value, fallback) {
      const number = Number(value)
      return Number.isFinite(number) ? number : fallback
    },

    handleBooking() {
      this.$router.push({
        name: "serviceAppointment",
        query: {
          id: this.service.id,
          name: this.service.name,
          address: this.service.address,
          price: this.service.price,
          projects: JSON.stringify(this.service.projects || [])
        }
      })
    },

    goBack() {
      if (window.history.length > 1) {
        this.$router.back()
        return
      }

      this.$router.push({ path: "/service" })
    }
  }
}
</script>

<style scoped>
.service-detail-page {
  min-height: 100vh;
  background: #f6f6fa;
  padding-bottom: 96px;
}

.top-bar {
  height: 64px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 14px;
  box-shadow: 0 1px 10px rgba(23, 24, 38, 0.06);
  position: sticky;
  top: 0;
  z-index: 10;
}

.back-btn,
.top-fav {
  border: none;
  background: transparent;
  padding: 0;
  cursor: pointer;
}

.back-btn {
  width: 34px;
  height: 34px;
  font-size: 30px;
  line-height: 34px;
  color: #222;
  text-align: left;
}

.top-title {
  font-size: 18px;
  font-weight: 600;
  color: #222;
}

.top-fav {
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex: none;
}

.fav-icon {
  font-size: 24px;
  color: #8a8494;
}

.fav-icon.active {
  color: #ff4d5a;
}

.content-shell {
  max-width: 760px;
  margin: 0 auto;
  padding: 14px 18px 0;
}

.gallery {
  background: #fff;
  padding: 0 18px 14px;
  border-radius: 18px 18px 0 0;
}

.gallery-track {
  display: flex;
  overflow-x: auto;
  scroll-snap-type: x mandatory;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
}

.gallery-track::-webkit-scrollbar {
  display: none;
}

.gallery-item {
  flex: 0 0 100%;
  scroll-snap-align: start;
  overflow: hidden;
  border-radius: 12px;
  background: #f2f2f2;
}

.gallery-item img {
  display: block;
  width: 100%;
  aspect-ratio: 16 / 10;
  object-fit: cover;
}

.gallery-dots {
  display: flex;
  justify-content: center;
  gap: 6px;
  margin-top: 10px;
}

.dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: #d4cfdd;
  transition: all 0.2s ease;
}

.dot.active {
  width: 16px;
  border-radius: 999px;
  background: #9f90c8;
}

.info-card,
.section-card {
  background: #fff;
  padding: 16px 18px;
}

.title-row {
  display: flex;
  justify-content: space-between;
  gap: 12px;
  align-items: flex-start;
}

.service-name {
  font-size: 20px;
  font-weight: 700;
  color: #3d2f23;
  line-height: 1.4;
}

.price-tag {
  flex: none;
  padding: 6px 12px;
  border-radius: 999px;
  background: #f4f0ff;
  color: #8673d6;
  font-size: 13px;
  font-weight: 600;
}

.meta-row {
  display: flex;
  flex-wrap: wrap;
  gap: 10px 14px;
  margin-top: 12px;
  color: #5f566b;
  font-size: 14px;
}

.meta-item {
  background: #faf7ff;
  border-radius: 10px;
  padding: 7px 10px;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 14px;
}

.tag {
  display: inline-flex;
  align-items: center;
  padding: 6px 12px;
  border-radius: 999px;
  background: #f2eefb;
  color: #6b6291;
  font-size: 13px;
}

.booking-row {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}

.book-btn {
  height: 34px;
  padding: 0 16px;
  border: none;
  border-radius: 999px;
  background: #8673d6;
  color: #fff;
  font-size: 13px;
  cursor: pointer;
}

.section-card {
  margin-top: 12px;
}

.section-title {
  font-size: 17px;
  font-weight: 700;
  color: #27242d;
  margin-bottom: 10px;
}

.section-text {
  margin: 0;
  font-size: 15px;
  line-height: 1.9;
  color: #4d4040;
}

.project-list {
  display: grid;
  gap: 10px;
}

.project-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 14px;
  border-radius: 14px;
  background: linear-gradient(180deg, #f4f0fa 0%, #ffffff 100%);
  border: 1px solid #f1eaf6;
}

.project-name {
  font-size: 14px;
  color: #3f3232;
}

.project-price {
  font-size: 14px;
  font-weight: 600;
  color: #8673d6;
}

.comment-bar {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 20;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 18px calc(12px + env(safe-area-inset-bottom));
  background: rgba(246, 246, 250, 0.94);
  backdrop-filter: blur(14px);
}

.image-input {
  display: none;
}

.attach-btn {
  width: 46px;
  height: 46px;
  border: none;
  border-radius: 50%;
  background: #fff;
  color: #8d79d2;
  font-size: 28px;
  line-height: 1;
  box-shadow: 0 8px 20px rgba(43, 35, 55, 0.1);
  cursor: pointer;
}

.input-box {
  flex: 1;
  height: 56px;
  padding: 16px 20px;
  border-radius: 28px;
  background: #fff;
  color: #3f3232;
  font-size: 16px;
  box-shadow: 0 8px 24px rgba(43, 35, 55, 0.06);
  border: none;
  outline: none;
  resize: none;
  line-height: 1.4;
  font-family: inherit;
}

.input-box::placeholder {
  color: #b3acb8;
}

.send-btn {
  width: 52px;
  height: 52px;
  border: none;
  border-radius: 50%;
  background: #7f5dcd;
  color: #fff;
  font-size: 22px;
  box-shadow: 0 10px 24px rgba(200, 184, 238, 0.45);
  cursor: pointer;
}

.comment-image-preview {
  position: fixed;
  right: 18px;
  bottom: calc(74px + env(safe-area-inset-bottom));
  z-index: 21;
  background: #fff;
  border-radius: 12px;
  padding: 8px;
  box-shadow: 0 8px 24px rgba(43, 35, 55, 0.14);
}

.comment-image-preview img {
  width: 88px;
  height: 88px;
  object-fit: cover;
  border-radius: 8px;
  display: block;
}

.remove-image-btn {
  position: absolute;
  right: -8px;
  top: -8px;
  width: 22px;
  height: 22px;
  border: none;
  border-radius: 50%;
  background: #8d79d2;
  color: #fff;
  font-size: 14px;
  line-height: 1;
  cursor: pointer;
}

@media (max-width: 640px) {
  .content-shell {
    padding-left: 12px;
    padding-right: 12px;
  }

  .gallery,
  .info-card,
  .section-card {
    padding-left: 14px;
    padding-right: 14px;
  }

  .service-name {
    font-size: 18px;
  }
}
</style>
