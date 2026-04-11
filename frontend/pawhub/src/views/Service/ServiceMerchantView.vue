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
        @like-comment="handleLikeComment"
        @reply-comment="handleReplyComment"
      />
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
          lastReply: "",
          liked: false
        }
      ]
    }
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

        return {
          ...comment,
          replyCount: (comment.replyCount || 0) + 1,
          lastReply: payload.text
        }
      })
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
  padding-bottom: 24px;
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
