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
        <div class="section-title">服务项目</div>
        <div class="project-list" v-if="service.servicesOffered && service.servicesOffered.length">
          <div v-for="item in service.servicesOffered" :key="item.name" class="project-item">
            <div class="project-name">{{ item.name }}</div>
            <div class="project-price">{{ item.price }}</div>
          </div>
        </div>
      </div>

      <PostCommentList
        :comments="commentList"
        :current-user-avatar="currentUser.avatar"
        :current-user-id="currentUser.userId"
        :current-user-name="currentUser.username"
        @like-comment="handleLikeComment"
        @like-reply="handleLikeReply"
        @reply-comment="handleReplyComment"
        @delete-comment="handleDeleteComment"
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
import {
  getServiceDetail,
  getServiceReviews,
  getReviewReplies,
  createServiceReview,
  addFavorite,
  removeFavorite
} from "@/api/services"
import { likeServiceReview, unlikeServiceReview, deleteServiceReview, replyServiceReview } from "@/api/services"
import { uploadFile } from "@/api/upload"

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
      commentImageFile: null,
      serviceId: null,
      service: {
        serviceId: null,
        name: "",
        address: "",
        rating: 0,
        images: [],
        tags: [],
        description: "",
        servicesOffered: [], 
        isFavorited: false,
        intro: "",
        price: ""
      },
      commentList: [],
      currentUser: { userId: null, username: "", avatar: "" }
      // extraCommentCount: 0,
      // currentUser: {
      //   username: "宠物爱好者",
      //   avatar: "https://images.unsplash.com/photo-1601758123927-1967a3f7f3b4"
      // },
      // fallbackService: {
      //   id: 1,
      //   type: "美容",
      //   name: "爱宠美容工作室",
      //   images: [
      //     "https://images.unsplash.com/photo-1516734212186-a967f81ad0d7",
      //     "https://images.unsplash.com/photo-1583337130417-3346a1be7dee",
      //     "https://images.unsplash.com/photo-1507149833265-60c372daea22"
      //   ],
      //   address: "朝阳区建国路88号",
      //   rating: 4.8,
      //   distance: 1.2,
      //   tags: ["洗澡", "美容", "造型"],
      //   price: "¥88起",
      //   intro:
      //     "这是一家专注宠物美容与护理的门店，环境干净整洁，店员对宠物很耐心，适合洗护、造型和日常护理。",
      //   projects: [
      //     { name: "洗澡", price: "¥38起" },
      //     { name: "剪毛造型", price: "¥88起" },
      //     { name: "驱虫护理", price: "¥58起" },
      //     { name: "毛发护理", price: "¥68起" }
      //   ]
      // },
      // commentList: [
      //   {
      //     id: 1,
      //     name: "猫咪铲屎官",
      //     time: "1小时前",
      //     avatar: "https://i.pravatar.cc/100?img=5",
      //     content: "环境看起来很不错，我家猫也想去试试。",
      //     likes: 9,
      //     replyCount: 1,
      //     replies: [],
      //     lastReply: "",
      //     liked: false
      //   },
      //   {
      //     id: 2,
      //     name: "小鱼",
      //     time: "刚刚",
      //     avatar: "https://i.pravatar.cc/100?img=12",
      //     content: "价格很清楚，打算周末去看看。",
      //     likes: 4,
      //     replyCount: 2,
      //     replies: [],
      //     lastReply: "",
      //     liked: false
      //   }
      // ]
    }
  },

	async created() {
		this.loadCurrentUser()
    this.serviceId = this.$route.params.id || this.$route.query.id
     console.log("获取到的 serviceId:", this.serviceId)
    if (this.serviceId) {
      await this.loadServiceDetail()
      await this.loadComments()
    }
	},

  // computed: {
  //   service() {
  //     const query = this.$route.query || {}

  //     return {
  //       ...this.fallbackService,
  //       id: query.id ? Number(query.id) || this.fallbackService.id : this.fallbackService.id,
  //       type: query.type || this.fallbackService.type,
  //       name: query.name || this.fallbackService.name,
  //       images: this.normalizeArray(query.images || query.image, this.fallbackService.images),
  //       address: query.address || this.fallbackService.address,
  //       rating: this.normalizeNumber(query.rating, this.fallbackService.rating),
  //       tags: this.normalizeArray(query.tags, this.fallbackService.tags),
  //       price: query.price || this.fallbackService.price,
  //       intro: query.intro || this.fallbackService.intro,
  //       projects: query.projects ? this.normalizeProjects(query.projects) : this.fallbackService.projects
  //     }
  //   }
  // },

  methods: {
    handleGalleryScroll(event) {
      const container = event.target
      const width = container.clientWidth
      if (!width) {
        this.activeImageIndex = Math.round(container.scrollLeft / width)
      }
    },

    // toggleFavorite() {
    //   this.favorite = !this.favorite
    // },

    loadCurrentUser() {
      const user = JSON.parse(localStorage.getItem("user") || "{}")
      this.currentUser = user
    },
     async loadServiceDetail() {
      try {
        const res = await getServiceDetail(this.serviceId)
        // console.log("后端返回的 services_offered:", res.data.services_offered)
        // console.log("services_offered 类型:", typeof res.data.services_offered)
        if (res.code === 1) {
          const data = res.data
          this.service = {
            serviceId: data.service_id,
            name: data.name,
            address: data.address,
            rating: data.rating,
            images: Array.isArray(data.images) ? data.images : [],
            tags: data.tags || [],
            description: data.description,
            servicesOffered: data.services_offered || [],  // 后端返回的套餐列表
            isFavorited: data.is_favorited || false,
            intro: data.description,
            price: data.price
          }
          this.favorite = this.service.isFavorited
        }
      } catch (error) {
        console.error("加载商户详情失败", error)
      }
    },

      async loadComments() {
      try {
        const res = await getServiceReviews(this.serviceId, { page: 1, pageSize: 20 })
          console.log("=== 后端返回的原始数据 ===", res)
      console.log("第一条评论:", res.data.list?.[0])
      console.log("所有字段:", Object.keys(res.data.list?.[0] || {}))
        if (res.code === 1) {
          const list = (res.data.list || []).map(c => this.normalizeCommentItem(c))
          // 为每条根评论加载回复
      for (const comment of list) {
        const repliesRes = await getReviewReplies(this.serviceId, comment.id)
        if (repliesRes.code === 1) {
          comment.replies = (repliesRes.data || []).map(reply => this.normalizeReplyItem(reply, comment.name))
          comment.replyCount = comment.replies.length
        }
      }
          this.commentList = list
        }
      } catch (error) {
        console.error("加载评论失败", error)
      }
    },
    
    // 标准化评论数据
  normalizeCommentItem(comment) {
    // 输出原始评论数据中与点赞相关的字段，便于排查后端字段名或值类型
    try {
      console.log("normalizeCommentItem raw:", {
        id: comment.reviewId ?? comment.review_id ?? comment.id,
        likedField: comment.liked,
        is_liked: comment.is_liked,
        likeCount: comment.likeCount ?? comment.likes ?? comment.like_count,
        raw: comment
      })
    } catch (e) {
      console.log('normalizeCommentItem log error', e)
    }

    const commentImages = this.parseJson(comment.images || [])
    const currentUserId = String(this.currentUser.userId || localStorage.getItem("userId") || "")
    const commentUserId = String(comment.userId ?? comment.user_id ?? "")
    const currentUserName = String(this.currentUser.username || "").trim()
    const commentName = String(comment.username || comment.name || "").trim()
    const replies = Array.isArray(comment.replies)
      ? comment.replies.map(reply => this.normalizeReplyItem(reply, comment.username || comment.name || "匿名用户"))
      : []

    return {
      id: comment.reviewId ?? comment.review_id ?? comment.id,
      name: comment.username || comment.name || "匿名用户",
      time: this.formatTime(comment.createTime || comment.create_time),
      avatar: this.toDisplayImageUrl(comment.avatar || "") || "",
      content: comment.content || "",
      image: this.toDisplayImageUrl(commentImages[0] || "") || "",
      // 后端可能返回不同命名：like_count / likeCount / likes
      likes: Number(comment.likeCount ?? comment.likes ?? comment.like_count ?? 0),
      // 后端可能返回 liked / is_liked / has_liked / like_status 等
      liked: this.toBooleanLikeFlag(
        comment.liked ?? comment.is_liked ?? comment.has_liked ?? comment.like_status ?? comment.is_like ?? comment.liked_flag
      ),
      userId: commentUserId,
      isMine: Boolean(
        (currentUserId && commentUserId && currentUserId === commentUserId) ||
        (currentUserName && commentName && currentUserName === commentName)
      ),
      replyCount: Number(comment.replyCount ?? 0),
      replies: replies
    }
  },

  // 标准化回复数据
  normalizeReplyItem(reply, fallbackTargetName) {
    // 输出原始回复数据中与点赞相关的字段，便于排查
    try {
      console.log("normalizeReplyItem raw:", {
        id: reply.reviewId ?? reply.review_id ?? reply.id,
        likedField: reply.liked,
        is_liked: reply.is_liked,
        likeCount: reply.likeCount ?? reply.likes ?? reply.like_count,
        raw: reply
      })
    } catch (e) {
      console.log('normalizeReplyItem log error', e)
    }

    const replyImages = this.parseJson(reply.images || [])
    const text = String(reply.content || reply.text || "").trim()
    const currentUserId = String(this.currentUser.userId || localStorage.getItem("userId") || "")
    const replyUserId = String(reply.userId ?? reply.user_id ?? "")
    const currentUserName = String(this.currentUser.username || "").trim()
    const replyName = String(reply.username || reply.name || "").trim()

    return {
      id: reply.reviewId ?? reply.review_id ?? reply.id,
      comment_id: reply.reviewId ?? reply.review_id ?? reply.id,
      content: reply.content || text,
      text: text,
      displayText: text,
      image: this.toDisplayImageUrl(reply.image || replyImages[0] || "") || "",
      targetName: reply.targetName || fallbackTargetName || "",
      avatar: this.toDisplayImageUrl(reply.avatar || "") || "",
      likes: Number(reply.likeCount ?? reply.likes ?? reply.like_count ?? 0),
      liked: this.toBooleanLikeFlag(
        reply.liked ?? reply.is_liked ?? reply.has_liked ?? reply.like_status ?? reply.is_like ?? reply.liked_flag
      ),
      userId: replyUserId,
      name: replyName,
      isMine: Boolean(
        (currentUserId && replyUserId && currentUserId === replyUserId) ||
        (currentUserName && replyName && currentUserName === replyName)
      )
      }
  },

      async toggleFavorite() {
      if (!this.currentUser.userId) {
        this.$router.push("/login")
        return
      }
      try {
        if (this.favorite) {
          await removeFavorite({ userId: this.currentUser.userId, serviceId: this.serviceId })
        } else {
          await addFavorite({ userId: this.currentUser.userId, serviceId: this.serviceId })
        }
        this.favorite = !this.favorite
      } catch (error) {
        console.error("收藏操作失败", error)
      }
    },

    async submitComment() {
      const text = this.commentDraft.trim()
      if (!text && !this.commentImage) return
      if (!this.currentUser.userId) {
        this.$router.push("/login")
        return
      }

      try {

      let uploadedImageUrl = ""

      if (this.commentImageFile) {
        const uploadRes = await uploadFile(this.commentImageFile)
        uploadedImageUrl = this.resolveUploadedUrl(uploadRes) || ""
        if (!uploadedImageUrl) {
          this.$message.error("评论图片上传失败")
          return
        }
      }

        const res = await createServiceReview(this.serviceId, {
          userId: this.currentUser.userId,
          rating: 5,
          content: text,
          images: uploadedImageUrl ? [uploadedImageUrl] : []
        })
        if (res.code === 1) {
          this.commentDraft = ""
          this.clearCommentImage()
          await this.loadComments()  // 刷新评论列表
          this.$message.success("评论成功")
        }else{
          this.$message.error(res.msg || "评论失败")
        }
      } catch (error) {
        console.error("提交评论失败", error)
        this.$message.error("发表评论失败")
      }
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
        this.commentImageFile = file
        this.commentImage = URL.createObjectURL(file)
      

      const reader = new FileReader()
      reader.onload = () => {
        this.commentImage = reader.result
      }
      reader.readAsDataURL(file)
    },

    clearCommentImage() {
      if (this.commentImage && this.commentImage.startsWith("blob:")) {
      URL.revokeObjectURL(this.commentImage)
    }
      this.commentImage = ""
      this.commentImageFile = null
      if (this.$refs.commentImageInput) {
        this.$refs.commentImageInput.value = ""
  }
    },

        async likeComment(reviewId) {
      try {
        await likeServiceReview(this.serviceId, reviewId)
        await this.loadComments()
      } catch (error) {
        console.error("点赞失败", error)
      }
    },

    async unlikeComment(reviewId) {
      try {
        await unlikeServiceReview(this.serviceId, reviewId)
        await this.loadComments()
      } catch (error) {
        console.error("取消点赞失败", error)
      }
    },

    async handleLikeComment(commentId) {
  if (!this.serviceId) return
  
  const target = this.commentList.find(c => c.id === commentId)
  if (!target) return

  const wasLiked = target.liked  // true=已点赞, false=未点赞
  const oldLikes = target.likes

  // 乐观更新
  target.liked = !wasLiked
  target.likes = wasLiked ? oldLikes - 1 : oldLikes + 1

  try {
    if (wasLiked) {
      // 已点赞 → 取消点赞
      await unlikeServiceReview(this.serviceId, commentId)
    } else {
      // 未点赞 → 点赞
      await likeServiceReview(this.serviceId, commentId)
    }
  } catch (error) {
    // 失败时回滚
    target.liked = wasLiked
    target.likes = oldLikes
    console.error("点赞操作失败", error)
    this.$message.error("操作失败")
  }
},

async handleLikeReply(payload) {
  if (!this.serviceId || !payload?.id) return

  let targetReply = null
  for (const comment of this.commentList) {
    const replies = Array.isArray(comment.replies) ? comment.replies : []
    targetReply = replies.find(r => r.id === payload.id)
    if (targetReply) break
  }

  if (!targetReply) return

  const wasLiked = targetReply.liked
  const oldLikes = targetReply.likes

  targetReply.liked = !wasLiked
  targetReply.likes = wasLiked ? oldLikes - 1 : oldLikes + 1

  try {
    if (wasLiked) {
      await unlikeServiceReview(this.serviceId, payload.id)
    } else {
      await likeServiceReview(this.serviceId, payload.id)
    }
  } catch (error) {
    targetReply.liked = wasLiked
    targetReply.likes = oldLikes
    console.error("点赞失败", error)
    this.$message.error("操作失败")
  }
},


    // async handleLikeComment(commentId) {
    //   if (!this.serviceId) return

    //   const target = this.commentList.find(c => c.id === commentId)
    //   if (!target) return

    //   const previousLiked = !target.liked
    //   const previousLikes = Number(target.likes || 0)

    //   try {
    //     if (previousLiked) {
    //       await likeServiceReview(this.serviceId, commentId)
    //       target.liked = false
    //       target.likes = Math.max(0, previousLikes - 1)
    //     } else {
    //       await unlikeServiceReview(this.serviceId, commentId)
    //       target.liked = true
    //       target.likes = previousLikes + 1
    //     }
    //   } catch (error) {
    //     console.error("操作失败", error)
    //     // 恢复之前的状态
    //     target.liked = previousLiked
    //     target.likes = previousLikes
    //     this.$message.error("点赞失败")
    //   }
    // },

    // 点赞回复
// async handleLikeReply(payload) {
//   if (!this.serviceId || !payload?.id) return

//   let targetReply = null
//   for (const comment of this.commentList) {
//     const replies = Array.isArray(comment.replies) ? comment.replies : []
//     targetReply = replies.find(r => r.id === payload.id)
//     if (targetReply) break
//   }

//   if (!targetReply) return

//   const previousLiked = !!targetReply.liked
//   const previousLikes = Number(targetReply.likes || 0)

//   try {
//     if (previousLiked) {
//       await unlikeServiceReview(this.serviceId, payload.id)
//       targetReply.liked = false
//       targetReply.likes = Math.max(0, previousLikes - 1)
//     } else {
//       await likeServiceReview(this.serviceId, payload.id)
//       targetReply.liked = true
//       targetReply.likes = previousLikes + 1
//     }
//   } catch (error) {
//     targetReply.liked = previousLiked
//     targetReply.likes = previousLikes
//     this.$message.error("点赞失败")
//   }
// },

    // 回复评论
async handleReplyComment(payload) {
  if (!this.serviceId || !payload?.id) return

  try {
    let uploadedImageUrl = ""

    if (payload.image) {
      const blob = await fetch(payload.image).then(response => response.blob())
      const file = new File([blob], `reply-${Date.now()}.png`, { type: blob.type || "image/png" })
      const uploadRes = await uploadFile(file)
      uploadedImageUrl = this.resolveUploadedUrl(uploadRes) || ""
      if (!uploadedImageUrl) {
        this.$message.error("回复图片上传失败")
        return
      }
    }

       // 使用专门的回复接口
    const replyData = { content: payload.text || "" }
    if (uploadedImageUrl) {
      // 如果后端支持回复带图片，需要扩展
      replyData.content = payload.text || "[图片]"
    }

    let res = null

    if (uploadedImageUrl) {
      // 若包含图片，优先把图片地址放入 replyData.images 并调用 replyServiceReview
      replyData.images = [uploadedImageUrl]
      try {
        res = await replyServiceReview(this.serviceId, payload.id, replyData)
      } catch (e) {
        console.warn('replyServiceReview 不支持 images，回退到 createServiceReview 发送带图回复', e)
        try {
          res = await createServiceReview(this.serviceId, {
            userId: this.currentUser.userId,
            content: payload.text || "[图片]",
            images: [uploadedImageUrl],
            parentReviewId: payload.id
          })
        } catch (err) {
          res = err
        }
      }
    } else {
      res = await replyServiceReview(this.serviceId, payload.id, replyData)
    }

    if (res && res.code === 1) {
      await this.loadComments()
      this.$message.success("回复成功")
    } else {
      this.$message.error((res && res.msg) || "回复失败")
    }
  } catch (error) {
    console.error("回复评论失败:", error)
    this.$message.error("回复失败")
  }
},

  // 删除评论
async handleDeleteComment(comment) {
  if (!this.serviceId || !comment?.id) return

  try {
    await deleteServiceReview(this.serviceId, comment.id)
    await this.loadComments()
    this.$message.success("删除成功")
  } catch (error) {
    console.error("删除评论失败:", error)
    this.$message.error("删除失败")
  }
},
// 辅助方法
parseJson(value) {
  if (!value) return []
  if (Array.isArray(value)) return value
  try { return JSON.parse(value) } catch { return [] }
},

toBooleanLikeFlag(value) {
  if (typeof value === "boolean") return value
  if (typeof value === "number") return value === 1
  if (typeof value === "string") {
    const text = value.trim().toLowerCase()
    if (["1", "true", "yes", "y", "liked"].includes(text)) return true
    if (["0", "false", "no", "n", "unliked", ""].includes(text)) return false
  }
  return false
},

formatTime(dateTime) {
  if (!dateTime) return '刚刚'
  const date = new Date(dateTime)
  const now = new Date()
  const diff = now - date
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  return `${Math.floor(diff / 86400000)}天前`
},

resolveUploadedUrl(response) {
  if (!response) return ""
  if (typeof response === "string") return response
  if (typeof response !== "object") return ""
  const candidates = [response.data, response.url, response.path, response.fileUrl, response.filePath]
  for (const candidate of candidates) {
    if (typeof candidate === "string" && candidate) return candidate
  }
  if (response.data && typeof response.data === "object") return this.resolveUploadedUrl(response.data)
  return ""
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

		formatNow() {
			const now = new Date()
			const pad = value => String(value).padStart(2, "0")
			return `${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}`
		},

    //   if (!value) {
    //     return Array.isArray(fallback) ? fallback : []
    //   }

    //   if (Array.isArray(value)) {
    //     return value
    //   }

    //   if (typeof value === "string") {
    //     try {
    //       const parsed = JSON.parse(value)

    //       if (Array.isArray(parsed)) {
    //         return parsed
    //       }
    //     } catch (error) {
    //       return value.split(",").map(item => item.trim()).filter(Boolean)
    //     }
    //   }

    //   return Array.isArray(fallback) ? fallback : []
    // },

    // normalizeProjects(value) {
    //   if (!value) {
    //     return this.fallbackService.projects
    //   }

    //   if (typeof value === "string") {
    //     try {
    //       const parsed = JSON.parse(value)
    //       if (Array.isArray(parsed)) {
    //         return parsed
    //       }
    //     } catch (error) {
    //       return this.fallbackService.projects
    //     }
    //   }

    //   return Array.isArray(value) ? value : this.fallbackService.projects
    // },

    // normalizeNumber(value, fallback) {
    //   const number = Number(value)
    //   return Number.isFinite(number) ? number : fallback
    // },

    handleBooking() {
      console.log("=== handleBooking 调试 ===")
      console.log("传递的商户ID:", this.serviceId)
      console.log("传递的商户名称:", this.service.name)
      this.$router.push({
        path:'/service/appointment',
        query: {
          id: this.serviceId,
          name: this.service.name,
          address: this.service.address,
          price: this.service.price,
          servicesOffered: JSON.stringify(this.service.servicesOffered || [])
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
