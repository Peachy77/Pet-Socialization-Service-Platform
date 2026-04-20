<template>
  <div class="user-info-page">
    <div class="header">
      <button class="back-btn" @click="goBack">‹</button>
      <div class="title">用户主页</div>
      <span class="spacer"></span>
    </div>

    <div class="profile-card">
      <div class="cover"></div>

      <div class="profile-main">
        <div class="profile-row">
          <img class="avatar" :src="profile.avatar" />

          <div class="user-info">
            <div class="name">{{ profile.username }}</div>
            <div class="bio">{{ profile.bio }}</div>
          </div>

          <div v-if="!isOwnProfile" class="profile-actions">
            <button
              class="action-btn action-btn-follow"
              :class="{ following: isFollowing }"
              @click="toggleFollow"
            >
              {{ isFollowing ? '已关注' : '关注' }}
            </button>
            <button class="action-btn action-btn-message" @click="goMessage">
              私信
            </button>
          </div>
        </div>

        <div class="stats">
          <div class="stat">
            <div class="num">{{ profile.followCount }}</div>
            <div class="label">关注</div>
          </div>
          <div class="stat">
            <div class="num">{{ profile.fansCount }}</div>
            <div class="label">粉丝</div>
          </div>
          <div class="stat">
            <div class="num">{{ profile.likesCount }}</div>
            <div class="label">获赞</div>
          </div>
        </div>
      </div>
    </div>

    <div class="section-title">用户动态</div>

    <div class="post-list">
      <PostCard
        v-for="post in posts"
        :key="post.id"
        :post="post"
        @toggle-like="handleToggleLike"
      />
    </div>
  </div>
</template>

<script>
import PostCard from "@/components/PostCard.vue"
import { likePost, unlikePost } from "@/api/posts"
import { followUser, getUser, getUserPosts, unfollowUser } from "@/api/users"

export default {
  name: "UserInformationView",

  components: {
    PostCard
  },

  data() {
    return {
      isFollowing: false,
      followLoading: false,
      likingPostIds: [],
      profile: {
        id: "",
        username: "Pet Lover",
        avatar: "https://images.unsplash.com/photo-1601758123927-1967a3f7f3b4",
        bio: "Loves pets and life.",
        followCount: 0,
        fansCount: 0,
        likesCount: 0
      },
      posts: []
    }
  },

  computed: {
    isOwnProfile() {
      const currentUserId = String(localStorage.getItem("userId") || "")
      const profileId = String(this.profile?.id || "")
      return Boolean(currentUserId && profileId && currentUserId === profileId)
    }
  },

  async created() {
    this.loadProfile()
    if (!this.profile.id) return

    await this.loadUserInfo()
    await this.loadUserPosts()
  },

  methods: {
    async loadUserInfo() {
      try {
        const response = await getUser(this.profile.id)
        const userData = this.unwrapPayload(response)
        if (!userData) return

        this.profile = {
          ...this.profile,
          id: userData.id ?? userData.userId ?? userData.user_id ?? this.profile.id,
          username: userData.username || userData.name || this.profile.username,
          avatar: userData.avatar || userData.avatarUrl || this.profile.avatar,
          bio: userData.bio || userData.intro || this.profile.bio,
          followCount: Number(userData.followCount || userData.following_count || this.profile.followCount || 0),
          fansCount: Number(userData.fansCount || userData.follower_count || this.profile.fansCount || 0),
          likesCount: Number(userData.totalLikeCount || userData.likesCount || userData.likeCount || userData.likes || this.profile.likesCount || 0)
        }
        this.isFollowing = this.toBooleanLikeFlag(userData.isFollowing ?? userData.following ?? userData.is_following)
      } catch (error) {
        const msg = error?.response?.data?.message || error?.message || "Failed to load user info"
        this.$message?.error?.(msg)
      }
    },

    async loadUserPosts() {
      if (!this.profile.id) return

      try {
        const response = await getUserPosts(this.profile.id, { page: 1, pageSize: 20 })
        const payload = this.unwrapPayload(response)
        const list = this.extractList(payload)
        this.posts = list.map(post => this.mapUserPost(post)).filter(Boolean)
      } catch (error) {
        const msg = error?.response?.data?.message || error?.message || "Failed to load posts"
        this.$message?.error?.(msg)
        this.posts = []
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

    mapUserPost(post) {
      if (!post || typeof post !== "object") return null

      const images = this.toArray(post.images || post.imageList || post.image_urls || post.imageUrls)
      const tags = this.toArray(post.tags || post.tagList || post.tagNames)

      return this.normalizePost({
        id: post.id ?? post.postId ?? post.post_id,
        avatar: post.avatar || post.userAvatar || post.authorAvatar || this.profile.avatar,
        name: post.name || post.username || post.userName || post.authorName || this.profile.username,
        time: this.formatTime(post.create_time || post.time || post.createTime || post.createdAt || post.created_at),
        images,
        content: post.content || post.text || "",
        tags,
        likes: Number(post.likes ?? post.likeCount ?? post.like_count ?? 0),
        comments: Number(post.comments ?? post.commentCount ?? post.comment_count ?? 0),
        liked: this.resolveLiked(post)
      })
    },

    toArray(value) {
      if (Array.isArray(value)) return value

      if (typeof value === "string") {
        try {
          const parsed = JSON.parse(value)
          if (Array.isArray(parsed)) return parsed
        } catch (error) {
          return value
            .split(",")
            .map(item => item.trim())
            .filter(Boolean)
        }
      }

      return []
    },

    toBooleanLikeFlag(value) {
      if (typeof value === "boolean") return value
      if (typeof value === "number") return value === 1

      if (typeof value === "string") {
        const text = value.trim().toLowerCase()
        if (["1", "true", "yes", "y", "liked", "已点赞"].includes(text)) return true
        if (["0", "false", "no", "n", "unliked", "未点赞", ""].includes(text)) return false
      }

      return false
    },

    resolveLiked(post) {
      const directFlag = post.liked ?? post.isLiked ?? post.is_liked ?? post.hasLiked ?? post.likeStatus
      if (directFlag !== undefined && directFlag !== null) {
        return this.toBooleanLikeFlag(directFlag)
      }

      const currentUserId = String(localStorage.getItem("userId") || "")
      if (!currentUserId) return false

      const likedUserIds = this.toArray(post.likedUserIds || post.likeUserIds || post.likerIds)
      return likedUserIds.map(item => String(item)).includes(currentUserId)
    },

    normalizePost(post) {
      if (!post || typeof post !== "object") return null
      return {
        ...post,
        liked: this.resolveLiked(post)
      }
    },

    parseDateTime(value) {
      if (!value) return null
      if (value instanceof Date) return Number.isNaN(value.getTime()) ? null : value
      if (typeof value === "number") {
        const date = new Date(value)
        return Number.isNaN(date.getTime()) ? null : date
      }

      const text = String(value).trim()
      if (!text) return null

      const normalizedText = text.replace(" ", "T")
      const directDate = new Date(normalizedText)
      if (!Number.isNaN(directDate.getTime())) return directDate

      const match = text.match(/^(\d{4})-(\d{2})-(\d{2})(?:[T\s](\d{2}):(\d{2})(?::(\d{2}))?)?$/)
      if (!match) return null

      const [, year, month, day, hour = "0", minute = "0", second = "0"] = match
      const date = new Date(Number(year), Number(month) - 1, Number(day), Number(hour), Number(minute), Number(second))
      return Number.isNaN(date.getTime()) ? null : date
    },

    formatTime(dateTime) {
      const date = this.parseDateTime(dateTime)
      if (!date) return "刚刚"

      const now = new Date()
      const diff = now - date
      if (diff < 60000) return "刚刚"
      if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
      if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
      return `${Math.floor(diff / 86400000)}天前`
    },

    goBack() {
      this.$router.back()
    },

    loadProfile() {
      const query = this.$route.query || {}
      const rawUser = query.user

      let parsedUser = null
      if (rawUser) {
        try {
          parsedUser = JSON.parse(decodeURIComponent(rawUser))
        } catch (error) {
          parsedUser = null
        }
      }

      const user = parsedUser || query
      this.profile = {
        ...this.profile,
        id: user.id || user.user_id || this.profile.id,
        username: user.name || user.username || this.profile.username,
        avatar: user.avatar || this.profile.avatar,
        bio: user.bio || this.profile.bio,
        followCount: Number(user.followCount || user.following_count || this.profile.followCount || 0),
        fansCount: Number(user.fansCount || user.follower_count || this.profile.fansCount || 0),
        likesCount: Number(user.totalLikeCount || user.likesCount || user.likeCount || user.likes || this.profile.likesCount || 0)
      }
      this.isFollowing = this.toBooleanLikeFlag(user.isFollowing ?? user.following ?? user.is_following)
    },

    async toggleFollow() {
      const targetUserId = this.profile?.id
      if (!targetUserId || this.followLoading) return

      const previousFollowing = this.isFollowing
      this.isFollowing = !previousFollowing
      this.followLoading = true

      try {
        const response = previousFollowing
          ? await unfollowUser(targetUserId)
          : await followUser(targetUserId)
        this.unwrapPayload(response)
      } catch (error) {
        this.isFollowing = previousFollowing
        const msg = error?.response?.data?.message || error?.message || (previousFollowing ? "取消关注失败" : "关注失败")
        this.$message?.error?.(msg)
      } finally {
        this.followLoading = false
      }
    },

    goMessage() {
      const targetUserId = this.profile?.id
      if (!targetUserId) {
        this.$message?.error?.("无法获取私信对象")
        return
      }

      this.$router.push({
        name: "messagesDetails",
        query: {
          targetUserId,
          username: this.profile.username,
          avatar: this.profile.avatar,
          type: "private"
        }
      })
    },

    isLiking(postId) {
      return this.likingPostIds.includes(postId)
    },

    async handleToggleLike(post) {
      const postId = post?.id
      if (!postId || this.isLiking(postId)) return

      const target = this.posts.find(item => item.id === postId)
      if (!target) return

      const previousLiked = !!target.liked
      const previousLikes = Number(target.likes || 0)

      target.liked = !previousLiked
      target.likes = previousLiked
        ? Math.max(0, previousLikes - 1)
        : previousLikes + 1

      this.likingPostIds.push(postId)

      try {
        const response = previousLiked
          ? await unlikePost(postId)
          : await likePost(postId)
        this.unwrapPayload(response)
      } catch (error) {
        target.liked = previousLiked
        target.likes = previousLikes
        const msg = error?.response?.data?.message || error?.message || "Like failed"
        this.$message.error(msg)
      } finally {
        this.likingPostIds = this.likingPostIds.filter(id => id !== postId)
      }
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
        throw new Error(response?.message || response?.msg || "Request failed")
      }

      if (response && Object.prototype.hasOwnProperty.call(response, "data")) {
        return response.data
      }

      return response
    }
  }
}
</script>

<style scoped>
.user-info-page{
  min-height:100vh;
  background:#f5f5f7;
  padding-bottom:24px;
}

.header{
  display:flex;
  align-items:center;
  justify-content:space-between;
  padding:14px 16px;
  background:white;
  border-bottom:1px solid #eee;
}

.back-btn{
  width:36px;
  height:36px;
  border:none;
  background:#f5f6fb;
  border-radius:12px;
  font-size:26px;
  line-height:1;
  color:#222;
}

.title{
  font-size:18px;
  font-weight:700;
  color:#111827;
}

.spacer{
  width:36px;
}

.profile-card{
  background:white;
  overflow:hidden;
}

.cover{
  height:120px;
  background:linear-gradient(120deg, #b385c8 0%, #899dce 45%, #eafbfd 100%);
}

.profile-main{
  padding:0 16px 18px;
}

.profile-row{
  display:flex;
  align-items:flex-start;
  gap:12px;
  margin-top:-36px;
}

.avatar{
  width:76px;
  height:76px;
  border-radius:50%;
  border:4px solid white;
  object-fit:cover;
  background:white;
  flex-shrink:0;
}

.user-info{
  flex:1;
  min-width:0;
  padding-top:34px;
  text-align:left;
}

.name{
  font-size:18px;
  font-weight:700;
  line-height:1.3;
  color:#111827;
}

.bio{
  margin-top:4px;
  color:#6b7280;
  font-size:14px;
  line-height:1.5;
}

.profile-actions{
  display:flex;
  gap:8px;
  margin-left:auto;
  padding-top:34px;
  flex-shrink:0;
}

.action-btn{
  min-width:72px;
  height:34px;
  padding:0 14px;
  border-radius:18px;
  font-size:13px;
  border:1px solid transparent;
}

.action-btn-follow{
  background:#8a84c8;
  color:white;
}

.action-btn-follow.following{
  background:#ddd8f4;
  color:#5f5877;
}

.action-btn-message{
  background:white;
  color:#8a84c8;
  border-color:#c9c6ef;
}

.stats{
  display:flex;
  gap:34px;
  padding:18px 0 0;
}

.stat{
  text-align:center;
  cursor:default;
}

.stat .num,
.stat .label{
  cursor:inherit;
}

.num{
  font-size:17px;
  font-weight:700;
  color:#111827;
}

.label{
  margin-top:4px;
  font-size:12px;
  color:#6b7280;
}

.intro-card{
  background:white;
  margin-top:10px;
  padding:16px;
}

.intro-title,
.section-title{
  font-size:16px;
  font-weight:700;
  color:#111827;
}

.intro-text{
  margin-top:8px;
  color:#4b5563;
  font-size:14px;
  line-height:1.6;
}

.section-title{
  padding:16px 16px 10px;
}

.post-list{
  padding:0 16px;
}
</style>

