<template>
  <div class="mine-page">

    <!-- 顶部标题 -->
    <div class="header">
      <span>个人主页</span>
      <span class="setting" @click="goSetting">⚙</span>
    </div>

    <!-- 个人信息区域 -->
    <div class="profile">

      <!-- 背景 -->
      <div class="cover"></div>

      <!-- 头像 + 用户信息 -->
      <div class="profile-row">

        <img
          class="avatar"
          :src="profile.avatar"
        />

        <div class="user-info">
          <div class="name">{{ profile.username }}</div>
          <div class="bio">
            {{ profile.bio }}
          </div>
        </div>

      </div>

      <!-- 数据 -->
      <div class="stats">
        <div class="stat clickable" @click="goFocusList">
          <div class="num">{{ profile.followingCount }}</div>
          <div class="label">关注</div>
        </div>

        <div class="stat clickable" @click="goFansList">
          <div class="num">{{ profile.followerCount }}</div>
          <div class="label">粉丝</div>
        </div>

        <div class="stat">
          <div class="num">{{ profile.likesCount }}</div>
          <div class="label">获赞</div>
        </div>
      </div>

      <!-- 编辑按钮 -->
      <button class="edit-btn" @click="goEditProfile">
        编辑资料
      </button>

    </div>


    <!-- Tab -->
    <div class="tabs">
      <div
        class="tab"
        :class="{ active: activeTab === 'posts' }"
        @click="selectTab('posts')"
      >
        动态
      </div>
      <div
        class="tab"
        :class="{ active: activeTab === 'favorites' }"
        @click="selectTab('favorites')"
      >
        收藏
      </div>
      <div
        class="tab"
        :class="{ active: activeTab === 'orders' }"
        @click="selectTab('orders')"
      >
        订单
      </div>
    </div>


    <!-- 动态列表 -->
    <div v-if="activeTab === 'posts'" class="post-list">
      <PostCard
        v-for="post in posts"
        :key="post.id"
        :post="post"
        @toggle-like="handleToggleLike"
        @delete-post="handleDeletePost"
      />
    </div>

    <!-- 收藏列表 -->
    <div v-else-if="activeTab === 'favorites'" class="service-list">
      <ServiceCard
        v-for="service in favoriteServices"
        :key="service.id"
        :service="service"
      />
    </div>

    <!-- 订单列表 -->
    <div v-else class="order-list-wrap">
      <OrderList
        :orders="orders"
        @view="viewOrder"
        @delete-order="deleteOrder"
        @cancel="cancelOrder"
        @rebook="rebookOrder"
      />
    </div>


    <!-- 底部导航 -->
    <BottomNav />

  </div>
</template>

<script>

import BottomNav from "@/components/BottomNav.vue"
import PostCard from "@/components/PostCard.vue"
import ServiceCard from "@/components/ServiceCard.vue"
import OrderList from "@/components/OrderList.vue"
import { getCurrentUser, getMyPosts, getMyFavorites, getMyOrders, getUser } from "@/api/users"
import { cancelOrder as deleteOrderApi, updateOrderStatus } from "@/api/orders"
import { likePost, unlikePost, deletePost } from "@/api/posts"
import defaultCatAvatar from "@/assets/cat.png"

export default {
  name: "MineView",

  data(){
    return{
      profile:{
        email:"",
        username:"未登录用户",
        avatar:"https://images.unsplash.com/photo-1601758123927-1967a3f7f3b4",
        bio:"这个人很神秘，什么介绍也没有",
        followerCount:0,
        followingCount:0,
        likesCount:0
      },
      activeTab:"posts",
      posts:[],
      favoriteServices:[],
      orders:[],
      likingPostIds:[]
    }
  },

  components: {
    BottomNav,
    PostCard,
    ServiceCard,
    OrderList
  },

  async created(){
    await this.loadMineData()
  },

  methods:{
    getFallbackAvatar(){
      return defaultCatAvatar
    },

    resolveAvatar(avatar){
      if (!avatar) {
        return this.getFallbackAvatar()
      }

      const avatarText = String(avatar).toLowerCase()
      if (avatarText.includes("default.jpg")) {
        return this.getFallbackAvatar()
      }

      return avatar
    },

    normalizeBio(bio, intro){
      const raw = bio ?? intro
      if (raw === null || raw === undefined) {
        return "这个人很神秘，什么介绍也没有~"
      }

      const text = String(raw).trim()
      return text || "这个人很神秘，什么介绍也没有~"
    },

    unwrapPayload(response){
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

    extractList(payload){
      if (Array.isArray(payload)) return payload
      if (Array.isArray(payload?.list)) return payload.list
      if (Array.isArray(payload?.records)) return payload.records
      if (Array.isArray(payload?.items)) return payload.items
      if (Array.isArray(payload?.rows)) return payload.rows
      if (Array.isArray(payload?.content)) return payload.content
      if (Array.isArray(payload?.data)) return payload.data
      return []
    },

    toArray(value){
      if (Array.isArray(value)) return value

      if (typeof value === "string") {
        try {
          const parsed = JSON.parse(value)
          if (Array.isArray(parsed)) return parsed
        } catch (error) {
          // 非 JSON 字符串时按逗号拆分
        }

        return value
          .split(",")
          .map(item => item.trim())
          .filter(Boolean)
      }

      return []
    },

    normalizePrice(value){
      if (value === undefined || value === null || value === "") {
        return "¥0"
      }

      const priceText = String(value)
      return priceText.startsWith("¥") ? priceText : `¥${priceText}`
    },

    normalizeOrderStatus(status){
      const text = String(status || "").toLowerCase()

      if (["pending", "wait", "waiting", "0", "待服务", "待处理"].includes(text)) {
        return "pending"
      }

      if (["completed", "done", "finish", "finished", "1", "已完成"].includes(text)) {
        return "completed"
      }

      if (["cancelled", "canceled", "cancel", "2", "已取消"].includes(text)) {
        return "cancelled"
      }

      return "pending"
    },

    mapProfile(user){
      if (!user || typeof user !== "object") return null

      return {
        email: user.email || user.account || "",
        username: user.username || user.name || "未命名用户",
        avatar: this.resolveAvatar(user.avatar || user.avatarUrl),
        bio: this.normalizeBio(user.bio, user.intro),
        followerCount: Number(user.followerCount ?? user.follower_count ?? 0),
        followingCount: Number(user.followingCount ?? user.following_count ?? 0),
        likesCount: Number(user.totalLikeCount ?? user.likesCount ?? user.likeCount ?? user.likes ?? 0)
      }
    },

    readCachedProfile(){
      const cached = JSON.parse(localStorage.getItem("pawhub_user_profile") || "null")
      if (!cached || typeof cached !== "object") return null
      return this.mapProfile(cached)
    },

    toBooleanLikeFlag(value){
      if (typeof value === "boolean") return value
      if (typeof value === "number") return value === 1

      if (typeof value === "string") {
        const text = value.trim().toLowerCase()
        if (["1", "true", "yes", "y", "liked", "已点赞"].includes(text)) return true
        if (["0", "false", "no", "n", "unliked", "未点赞", ""].includes(text)) return false
      }

      return false
    },

    resolveLiked(post){
      const directFlag = post.liked ?? post.isLiked ?? post.is_liked ?? post.hasLiked ?? post.likeStatus
      if (directFlag !== undefined && directFlag !== null) {
        return this.toBooleanLikeFlag(directFlag)
      }

      const currentUserId = String(localStorage.getItem("userId") || "")
      if (!currentUserId) return false

      const likedUserIds = this.toArray(post.likedUserIds || post.likeUserIds || post.likerIds)
      return likedUserIds.map(item => String(item)).includes(currentUserId)
    },

    mapPost(post){
      if (!post || typeof post !== "object") return null

      const images = this.toArray(post.images || post.imageList || post.image_urls || post.imageUrls)
      const tags = this.toArray(post.tags || post.tagList || post.tagNames)

      return {
        id: post.id ?? post.postId ?? post.post_id,
        avatar: post.avatar || post.userAvatar || post.authorAvatar || this.profile.avatar || this.getFallbackAvatar(),
        name: post.name || post.username || post.userName || post.authorName || this.profile.username,
        time: this.formatTime(post.create_time || post.time || post.createTime || post.createdAt || post.created_at),
        images,
        content: post.content || post.text || "",
        tags,
        likes: Number(post.likes ?? post.likeCount ?? post.like_count ?? 0),
        comments: Number(post.comments ?? post.commentCount ?? post.comment_count ?? 0),
        liked: this.resolveLiked(post),
        isMine: true
      }
    },

    mapFavorite(service){
      if (!service || typeof service !== "object") return null

      const projects = this.toArray(service.projects || service.projectNames)
      const tags = this.toArray(service.tags || service.tagList || projects)
      const images = this.toArray(
        service.images ||
        service.imageList ||
        service.image_urls ||
        service.imageUrls
      )
      const coverImage =
        images[0] ||
        service.image ||
        service.cover ||
        service.coverImage ||
        this.getFallbackAvatar()

      return {
        id: service.id ?? service.serviceId ?? service.service_id,
        type: service.type || service.category || "pet",
        name: service.name || service.serviceName || "未命名服务",
        image: coverImage,
        address: service.address || service.location || "",
        rating: String(service.rating ?? service.score ?? "0"),
        distance: String(service.distance ?? service.distanceKm ?? "0"),
        tags,
        price: `¥${service.min_price || service.price || 0}起`
      }
    },

    mapOrder(order){
      if (!order || typeof order !== "object") return null

      const serviceName =
        order.serviceName ||
        order.project_name ||
        order.projectName ||
        order.service_name ||
        "未命名服务"

      return {
        id: order.id ?? order.orderId ?? order.order_id,
        status: this.normalizeOrderStatus(order.status),
        userName: order.userName || order.username || order.user_email || this.profile.email,
        serviceName,
        merchantName: order.merchantName || order.storeName || order.shopName || "",
        time: order.time || order.appointmentTime || order.appointment_time || "",
        appointmentTime: order.appointmentTime || order.appointment_time || order.time || "",
        remark: order.remark || "",
        price: this.normalizePrice(order.price),
        orderTime: order.orderTime || order.createTime || order.createdAt || "",
        updateTime: order.updateTime || order.updatedAt || ""
      }
    },

    async loadMineData(){
      try {
        // 个人信息单独请求，避免其它列表接口失败时影响用户名展示
        let profile = null

        try {
          const profileRes = await getCurrentUser()
          const profilePayload = this.unwrapPayload(profileRes)
          profile = this.mapProfile(profilePayload)
        } catch (meError) {
          const fallbackUserId = localStorage.getItem("userId")

          if (fallbackUserId) {
            const userRes = await getUser(fallbackUserId)
            const userPayload = this.unwrapPayload(userRes)
            profile = this.mapProfile(userPayload)
          } else {
            profile = this.readCachedProfile()
          }
        }

        if (profile) {
          this.profile = {
            ...this.profile,
            ...profile
          }
          localStorage.setItem("pawhub_user_profile", JSON.stringify(this.profile))
        }

        const [postsResult, favoritesResult, ordersResult] = await Promise.allSettled([
          getMyPosts(),
          getMyFavorites({ page: 1, pageSize: 20 }),
          getMyOrders({ page: 1, pageSize: 20 })
        ])

        const postsPayload = postsResult.status === "fulfilled"
          ? this.unwrapPayload(postsResult.value)
          : []

        const favoritesPayload = favoritesResult.status === "fulfilled"
          ? this.unwrapPayload(favoritesResult.value)
          : []

        const ordersPayload = ordersResult.status === "fulfilled"
          ? this.unwrapPayload(ordersResult.value)
          : []

        this.posts = this.extractList(postsPayload)
          .map(item => this.mapPost(item))
          .filter(Boolean)

        const favoriteItems = this.extractList(favoritesPayload)
        console.log("[MineView] 收藏接口原始数据", favoriteItems)

        this.favoriteServices = favoriteItems
          .map(item => {
            const mapped = this.mapFavorite(item)
            console.log("[MineView] 收藏项映射结果", {
              raw: item,
              imageCandidates: {
                image: item?.image,
                cover: item?.cover,
                coverImage: item?.coverImage,
                imageUrl: item?.imageUrl,
                imageUrls: item?.imageUrls,
                img: item?.img,
                pic: item?.pic
              },
              mapped
            })

            return mapped
          })
          .filter(Boolean)

        this.orders = this.extractList(ordersPayload)
          .map(item => this.mapOrder(item))
          .filter(Boolean)
      } catch (error) {
        const msg = error?.message || error?.response?.data?.message || "加载个人主页数据失败"
        this.$message.error(msg)

        if (error?.response?.status === 401) {
          this.$router.push("/")
        }
      }
    },

    goEditProfile(){
      this.$router.push({
        name:"editProfile"
      })
    },

    goSetting(){
      this.$router.push({
        name:"setting"
      })
    },

    selectTab(tab){
      this.activeTab = tab
    },

    goFocusList(){
      this.$router.push({
        name:'focusList'
      })
    },

    goFansList(){
      this.$router.push({
        name:'fansList'
      })
    },

    viewOrder(order){
      this.$router.push({
        name:"orderDetail",
        query:{
          orderId: order?.id,
          order: encodeURIComponent(JSON.stringify(order))
        }
      })
    },

    async cancelOrder(order){
      if (order.status !== "pending") return

      const confirmed = window.confirm("确认取消订单吗？")
      if (!confirmed) return

      const target = this.orders.find(item => item.id === order.id)
      if (!target) return

      try {
        await this.unwrapPayload(await updateOrderStatus(order.id, { status: "cancelled" }))

        const updateTime = this.formatNow()
        target.status = "cancelled"
        target.updateTime = updateTime
        this.$message.success("订单已取消")
      } catch (error) {
        const msg = error?.response?.data?.message || error?.message || "取消订单失败"
        this.$message.error(msg)
      }
    },

    async deleteOrder(order){
      const orderId = order?.id
      if (!orderId) return

      const confirmed = window.confirm("确认删除这条订单吗？")
      if (!confirmed) return

      try {
        await this.unwrapPayload(await deleteOrderApi(orderId))
        this.orders = this.orders.filter(item => item.id !== orderId)
        this.$message.success("订单已删除")
      } catch (error) {
        const msg = error?.response?.data?.message || error?.message || "删除订单失败"
        this.$message.error(msg)
      }
    },

    rebookOrder(order){
      this.$router.push({
        name:"serviceAppointment",
        query:{
          name:order.serviceName,
          address:order.merchantName,
          price:order.price,
          projects:JSON.stringify([
            { name: order.serviceName, price: order.price }
          ])
        }
      })
    },

    formatNow(){
      const date = new Date()
      const pad = (value) => String(value).padStart(2, "0")
      return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
    },

    parseDateTime(value){
      if (!value) return null

      if (value instanceof Date) {
        return Number.isNaN(value.getTime()) ? null : value
      }

      if (typeof value === "number") {
        const date = new Date(value)
        return Number.isNaN(date.getTime()) ? null : date
      }

      const text = String(value).trim()
      if (!text) return null

      const normalizedText = text.replace(" ", "T")
      const directDate = new Date(normalizedText)
      if (!Number.isNaN(directDate.getTime())) {
        return directDate
      }

      const match = text.match(
        /^(\d{4})-(\d{2})-(\d{2})(?:[T\s](\d{2}):(\d{2})(?::(\d{2}))?)?$/
      )

      if (!match) return null

      const [, year, month, day, hour = "0", minute = "0", second = "0"] = match
      const date = new Date(
        Number(year),
        Number(month) - 1,
        Number(day),
        Number(hour),
        Number(minute),
        Number(second)
      )

      return Number.isNaN(date.getTime()) ? null : date
    },

    formatTime(dateTime){
      const date = this.parseDateTime(dateTime)
      if (!date) return "刚刚"

      const now = new Date()
      const diff = now - date
      if (diff < 60000) return "刚刚"
      if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
      if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
      return `${Math.floor(diff / 86400000)}天前`
    },

    isLiking(postId){
      return this.likingPostIds.includes(postId)
    },

    async handleDeletePost(post){
      const postId = post?.id
      if (!postId) return

      const confirmed = window.confirm("确认删除这条动态吗？")
      if (!confirmed) return

      try {
        const response = await deletePost(postId)
        this.unwrapPayload(response)
        this.posts = this.posts.filter(item => item.id !== postId)
        this.$message.success("动态已删除")
      } catch (error) {
        const msg = error?.response?.data?.message || error?.message || "删除动态失败"
        this.$message.error(msg)
      }
    },

    async handleToggleLike(post){
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
        const msg = error?.response?.data?.message || error?.message || "点赞操作失败"
        this.$message.error(msg)
      } finally {
        this.likingPostIds = this.likingPostIds.filter(id => id !== postId)
      }
    }
  }
}
</script>

<style scoped>

.mine-page{
  min-height:100vh;
  background:#f5f5f7;
  padding-bottom:70px;
}

/* 顶部 */

.header{
  display:flex;
  justify-content:space-between;
  align-items:center;
  padding:16px;
  font-size:18px;
  font-weight:600;
  background:white;
}

.setting{
  font-size:20px;
  cursor:pointer;
  transition:0.2s;
}

.setting:hover{
  color:#8a84c8;
}

/* 个人信息 */

.profile{
  background:white;
  padding-bottom:20px;
}

/* 背景 */

.cover{
  height:120px;
  background:linear-gradient(120deg, #b385c8 0%, #899dce 45%, #eafbfd 100%);
}

/* 头像 + 用户信息 左对齐 */

.profile-row{
  display:flex;
  align-items:center;
  padding:12px 20px;
  margin-top:-40px;
  gap:12px;
}

/* 头像 */

.avatar{
  width:80px;
  height:80px;
  border-radius:50%;
  border:4px solid white;
  object-fit:cover;
  flex-shrink:0;
}

.name{
  font-size:18px;
  font-weight:500;
  line-height:1.3;
}

.user-info{
  flex:1;
  display:flex;
  flex-direction:column;
  align-items:flex-start;
  justify-content:center;
  padding-top:8px;
}

.bio{
  margin-top:4px;
  color:#666;
  font-size:14px;
  line-height:1.5;
}

/* 数据 */

.stats{
  display:flex;
  gap:40px;
  padding:10px 20px;
}

.stat{
  text-align:center;
  cursor:default;
}

.stat .num,
.stat .label{
  cursor:inherit;
}

.clickable{
  cursor:pointer;
}

.clickable .num,
.clickable .label{
  transition:0.2s;
}

.clickable:hover .num,
.clickable:hover .label{
  color:#8a84c8;
}

.num{
  font-size:16px;
  font-weight:600;
}

.label{
  font-size:12px;
  color:#666;
}

/* 编辑按钮 */

.edit-btn{
  margin:10px 20px;
  width:calc(100% - 40px);
  height:36px;
  border:1px solid #ccc;
  border-radius:18px;
  background:white;
}

/* tab */

.tabs{
  display:flex;
  background:white;
  margin-top:10px;
  border-bottom:1px solid #eee;
}

.tab{
  flex:1;
  text-align:center;
  padding:12px;
  color:#666;
  cursor:pointer;
}

.tab.active{
  color:#8a84c8;
  border-bottom:2px solid #8a84c8;
}

/* 动态列表 */

.post-list{
  padding:15px;
}

.service-list{
  padding:15px;
}

.order-list-wrap{
  padding-bottom:10px;
}

</style>