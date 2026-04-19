<template>
  <div class="home-page">

<!-- 页面标题 -->
<h1 class="home-title">PawHub首页</h1>

<!-- 搜索框 -->
<SearchBar @search="handleSearch"/>

<!-- 服务菜单 -->
<ServiceMenu/>

<!-- 推荐动态标题 -->
<div class="section-title">
  推荐动态
</div>

<!-- 动态列表 -->
<div class="post-list">
  <PostCard
    v-for="post in posts"
    :key="post.id"
    :post="post"
    @toggle-like="handleToggleLike"
  />
  <div v-if="loading" class="tips">加载中...</div>
  <div v-else-if="!posts.length" class="tips">暂无动态，快去发布第一条吧</div>
</div>

<!-- 底部导航 -->
<BottomNav/>

  </div>
</template>

<script>

import SearchBar from "@/components/SearchBar.vue"
import ServiceMenu from "@/components/ServiceMenu.vue"
import PostCard from "@/components/PostCard.vue"
import BottomNav from "@/components/BottomNav.vue"
import { getPosts, likePost, unlikePost } from "@/api/posts"

export default {

  name:"HomeView",

  components:{
    SearchBar,
    ServiceMenu,
    PostCard,
    BottomNav
  },

  data(){
    return{
      loading:false,
      posts:[],
      likingPostIds:[]
    }
  },

  async created(){
    await this.loadPosts()
  },

  methods:{
    unwrapPayload(response){
      const code = response?.code
      const normalizedCode = code === undefined || code === null ? null : String(code)
      const isBusinessSuccess =
        normalizedCode === null ||
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
      if (Array.isArray(payload?.posts)) return payload.posts
      return []
    },

    toArray(value){
      if (Array.isArray(value)) return value

      if (typeof value === "string") {
        try {
          const parsed = JSON.parse(value)
          if (Array.isArray(parsed)) return parsed
        } catch (error) {
          // 非 JSON 字符串按逗号拆分
        }

        return value
          .split(",")
          .map(item => item.trim())
          .filter(Boolean)
      }

      return []
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

      return {
        id: post.id ?? post.postId ?? post.post_id,
        name: post.name || post.username || post.userName || post.authorName || "匿名用户",
        time: post.time || post.createTime || post.createdAt || post.created_at || "刚刚",
        avatar: post.avatar || post.userAvatar || post.authorAvatar || "https://images.unsplash.com/photo-1601758123927-1967a3f7f3b4",
        images: this.toArray(post.images || post.imageList || post.image_urls || post.imageUrls),
        content: post.content || post.text || "",
        tags: this.toArray(post.tags || post.tagList || post.tagNames),
        likes: Number(post.likes ?? post.likeCount ?? post.like_count ?? 0),
        comments: Number(post.comments ?? post.commentCount ?? post.comment_count ?? 0),
        liked: this.resolveLiked(post)
      }
    },

    isLiking(postId){
      return this.likingPostIds.includes(postId)
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
    },

    async loadPosts(){
      this.loading = true

      try {
        const response = await getPosts({ page: 1, pageSize: 20 })
        const payload = this.unwrapPayload(response)

        this.posts = this.extractList(payload)
          .map(item => this.mapPost(item))
          .filter(Boolean)
      } catch (error) {
        const msg = error?.response?.data?.message || error?.message || "加载动态失败"
        this.$message.error(msg)
        this.posts = []
      } finally {
        this.loading = false
      }
    },

    handleSearch(keyword){
      console.log("搜索:",keyword)

      this.$router.push({
        path:"/search",
        query:{q:keyword}
      })
    }

  }

}
</script>

<style scoped>

/* 页面标题 */
.home-title {
  width:100%;
  box-sizing:border-box;
  padding:20px 18px;
  border-radius:9px;
  border:1px solid #dfe6f5;
  background:linear-gradient(120deg, #b385c8 0%, #899dce 45%, #eafbfd 100%);
  box-shadow:0 10px 24px rgba(49, 72, 140, 0.14);
  font-size:25px;
  font-weight:800;
  margin:0 0 16px 0;
  text-align:center;
  color:#2f2a4a;
  letter-spacing:0.8px;
}

.home-page{
  background:linear-gradient(#fcfcfe, #f1edf4);
  min-height:100vh;
  padding:15px;
  padding-bottom:80px;
}

/* 推荐动态标题 */

.section-title{
  font-size:18px;
  font-weight:600;
  margin:20px 0 10px 0;
  text-align: left; 
  color:#706f78;
}

/* 动态列表 */

.post-list{
  display:grid;

  /* 自动根据屏幕宽度调整列数 */
  grid-template-columns: repeat(auto-fit, minmax(350px,1fr));

  gap:20px;

  margin-bottom:20px;
}

.tips{
  padding:18px;
  border-radius:12px;
  background:#ffffff;
  color:#707070;
  font-size:14px;
}



</style>
