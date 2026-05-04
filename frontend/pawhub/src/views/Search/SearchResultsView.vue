<template>
  <div class="search-results">

    <!-- 顶部搜索栏 -->
    <div class="top-bar">

      <!-- 返回 -->
      <div class="back" @click="$router.back()">
        ‹
      </div>

      <!-- 搜索框 -->
      <div class="search-wrapper" @click="goSearch">
        <SearchBar
          :placeholder="keyword"
          @search="handleSearch"
        />
      </div>

      <div class="search-btn" @click="handleSearch(keyword || '')">
        搜索
      </div>

    </div>

    <!-- Tabs -->
    <div class="tabs">

      <div
        class="tab"
        :class="{active:activeTab==='post'}"
        @click="activeTab='post'"
      >
        动态
      </div>

      <div
        class="tab"
        :class="{active:activeTab==='user'}"
        @click="activeTab='user'"
      >
        用户
      </div>

      <div
        class="tab"
        :class="{active:activeTab==='service'}"
        @click="activeTab='service'"
      >
        商户
      </div>

    </div>


    <!-- 内容区域 -->
    <div class="content">

      <!-- 动态 -->
      <div v-if="activeTab==='post'">

        <PostCard
          v-for="post in posts"
          :key="post.id"
          :post="post"
          @toggle-like="handleToggleLike"
        />

      </div>


      <!-- 用户 -->
      <div v-if="activeTab==='user'">

        <UserCard
          v-for="user in users"
          :key="user.id"
          :user="user"
          :following="!!user.following"
          @click="handleUserClick"
          @follow="handleFollow"
        />

      </div>


      <!-- 商户 -->
      <div v-if="activeTab==='service'">

        <ServiceCard
          v-for="service in services"
          :key="service.id"
          :service="service"
        />

      </div>

    </div>

  </div>
</template>

<script>

import SearchBar from "@/components/SearchBar.vue"
import PostCard from "@/components/PostCard.vue"
import ServiceCard from "@/components/ServiceCard.vue"
import UserCard from "@/components/UserCard.vue"

import { searchPosts } from "@/api/posts"
import { followUser, getMyFollowing, searchUsers, unfollowUser } from "@/api/users"
import {  searchServices } from "@/api/services"

export default {

  name:"SearchResultsView",

  components:{
    SearchBar,
    PostCard,
    ServiceCard,
    UserCard
  },

  data(){
    return{

      keyword:this.$route.query.keyword || "",

      activeTab:"post",

      loading: false,
      followingUserIds: [],
      posts: [],
      users: [],
      services: []

      // posts:[
      //    {
      //     id:1,
      //     name:"柴犬小乖",
      //     time:"2小时前",
      //     avatar:"https://i.pravatar.cc/100?img=3",
      //     images:[
      //       "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e",
      //       "https://images.unsplash.com/photo-1558788353-f76d92427f16"
      //     ],
      //     content:"今天带我家毛孩子去美容院做了个新造型，超级可爱！",
      //     tags:["宠物美容","柴犬"],
      //     likes:234,
      //     comments:45
      //   }
      // ],

      // users:[
      //   {
      //     id:1,
      //     name:"用户名1",
      //     bio:"简介内容...",
      //     avatar:"https://placekitten.com/100/100"
      //   },
      //   {
      //     id:2,
      //     name:"用户名2",
      //     bio:"简介内容...",
      //     avatar:"https://placekitten.com/101/101"
      //   }
      // ],

      // services:[
      //   {
      //     id:1,
      //     name:"爱宠美容工作室",
      //     address:"朝阳区建国路88号",
      //     rating:"4.8",
      //     distance:"1.2",
      //     tags:["洗澡","美容","造型"],
      //     price:"¥88起",
      //     image:"https://placekitten.com/200/200"
      //   }
      // ]

    }
  },
    watch: {
    // 监听路由参数变化，重新搜索
    '$route.query.keyword': {
      handler(newKeyword) {
        if (newKeyword && newKeyword !== this.keyword) {
          this.keyword = newKeyword
          this.performSearch()
        }
      },
      immediate: true
    }
  },

  mounted() {
    if (this.keyword) {
      this.performSearch()
    }
  },

  methods:{
    isBusinessSuccess(res) {
      const code = res?.code
      const normalizedCode = code === undefined || code === null ? null : String(code)
      return (
        normalizedCode === null ||
        normalizedCode === "0" ||
        normalizedCode === "1" ||
        normalizedCode === "200" ||
        res?.success === true
      )
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

    toBooleanFollowFlag(value) {
      if (typeof value === "boolean") return value
      if (typeof value === "number") return value === 1

      if (typeof value === "string") {
        const text = value.trim().toLowerCase()
        if (["1", "true", "yes", "y", "followed", "已关注"].includes(text)) return true
        if (["0", "false", "no", "n", "unfollowed", "未关注", ""].includes(text)) return false
      }

      return false
    },

    extractUserId(user) {
      if (!user || typeof user !== "object") return null
      return user.id ?? user.userId ?? user.user_id
    },

    async loadFollowingIdSet() {
      try {
        const response = await getMyFollowing({ page: 1, pageSize: 500 })
        if (!this.isBusinessSuccess(response)) return new Set()

        const payload = response && Object.prototype.hasOwnProperty.call(response, "data")
          ? response.data
          : response

        const list = this.extractList(payload)
        return new Set(
          list
            .map(item => this.extractUserId(item))
            .filter(id => id !== null && id !== undefined)
            .map(id => String(id))
        )
      } catch (error) {
        console.warn("[SearchResultsView] 获取关注列表失败，降级使用搜索接口中的关注字段", error)
        return new Set()
      }
    },

      async performSearch() {
      if (!this.keyword || !this.keyword.trim()) {
        this.posts = []
        this.users = []
        this.services = []
        return
      }

      this.loading = true

      try {
        // 并行请求三个接口
        const [postRes, userRes, serviceRes, followingIdSet] = await Promise.all([
          this.searchPosts(),
          this.searchUsers(),
          this.searchServices(),
          this.loadFollowingIdSet()
        ])

        console.log("[SearchResultsView] 动态接口原始响应", postRes)
        console.log("[SearchResultsView] 用户接口原始响应", userRes)
        console.log("[SearchResultsView] 商户接口原始响应", serviceRes)

        // 处理动态结果
        if (postRes && this.isBusinessSuccess(postRes)) {
          const list = this.extractList(postRes?.data ?? postRes)
          console.log("[SearchResultsView] 动态列表提取结果", {
            count: list.length,
            list
          })

          this.posts = list.map(p => ({
            id: p.post_id ?? p.id ?? p.postId,
            name: p.username || p.name || p.userName || "匿名用户",
            time: this.formatTime(p.create_time),
            avatar: p.avatar || 'default.jpg',
            images: this.parseJson(p.images || p.imageList || p.image_urls || p.imageUrls),
            content: p.content || p.text || "",
            tags: this.parseJson(p.tags || p.tagList || p.tagNames),
            likes: p.like_count || 0,
            comments: p.comment_count || 0
          })).filter(item => item.id !== undefined && item.id !== null)
        } else {
          this.posts = []
        }

        // 处理用户结果
        if (userRes && this.isBusinessSuccess(userRes)) {
          const list = this.extractList(userRes?.data ?? userRes)
          this.users = list.map(u => ({
            id: this.extractUserId(u),
            name: u.username || u.name || "未知用户",
            bio: u.bio || '',
            avatar: u.avatar || 'default.jpg',
            following: this.toBooleanFollowFlag(u.isFollowing ?? u.following ?? u.is_following) || followingIdSet.has(String(this.extractUserId(u)))
          })).filter(item => item.id !== undefined && item.id !== null)
        } else {
          this.users = []
        }

        // 处理商户结果
        if (serviceRes && this.isBusinessSuccess(serviceRes)) {
          const list = this.extractList(serviceRes?.data ?? serviceRes)
          this.services = list.map(s => ({
            id: s.service_id ?? s.id ?? s.serviceId,
            name: s.name,
            address: s.address || '暂无地址',
            rating: s.rating || 0,
            distance: s.distance || '未知',
            tags: this.parseJson(s.tags) || [],
            price: s.min_price ? `¥${s.min_price}起` : (s.price ? `¥${s.price}起` : '暂无'),
            image: s.image || (s.images && s.images[0]) || 'https://placekitten.com/200/200'
          })).filter(item => item.id !== undefined && item.id !== null)
        } else {
          this.services = []
        }

      } catch (error) {
        console.error('搜索失败:', error)
      } finally {
        this.loading = false
      }
    },

    async searchPosts() {
      try {
        // 使用 searchPosts 接口
        return await searchPosts(this.keyword, { page: 1, pageSize: 20 })
      } catch (error) {
        console.error('搜索动态失败:', error)
        return null
      }
    },

    async searchUsers() {
      try {
        return await searchUsers(this.keyword, { page: 1, pageSize: 20 })
      } catch (error) {
        console.error('搜索用户失败:', error)
        return null
      }
    },

    async searchServices() {
      try {
        return await searchServices(this.keyword, { page: 1, pageSize: 20 })
      } catch (error) {
        console.error('搜索商户失败:', error)
        return null
      }
    },

    handleSearch(keyword){


      const nextKeyword = String(keyword || "").trim()

      if(!nextKeyword) return

      if(nextKeyword === this.keyword && this.$route.query.keyword === nextKeyword){
        return
      }

      this.keyword = nextKeyword

      this.$router.push({
        path:"/search/results",
        query:{keyword:nextKeyword}
      })

    },

    goSearch(){

      this.$router.push({
        path:"/search"
      })

    },

    handleUserClick(user){
      this.$router.push({
        name: "userInformation",
        query: {
          user: encodeURIComponent(JSON.stringify(user))
        }
      })
    },

    handleFollow(user){
      this.toggleFollow(user)

    },

    async toggleFollow(user) {
      const targetUserId = user?.id
      if (!targetUserId || this.followingUserIds.includes(targetUserId)) return

      const currentUser = this.users.find(item => item.id === targetUserId)
      if (!currentUser) return

      const previousFollowing = !!currentUser.following
      this.followingUserIds.push(targetUserId)

      try {
        const response = previousFollowing
          ? await unfollowUser(targetUserId)
          : await followUser(targetUserId)

        if (!this.isBusinessSuccess(response)) {
          throw new Error(response?.message || response?.msg || (previousFollowing ? "取消关注失败" : "关注失败"))
        }

        this.users = this.users.map(item => (
          item.id === targetUserId
            ? { ...item, following: !previousFollowing }
            : item
        ))
      } catch (error) {
        const msg = error?.response?.data?.message || error?.message || (previousFollowing ? "取消关注失败" : "关注失败")
        this.$message?.error?.(msg)
      } finally {
        this.followingUserIds = this.followingUserIds.filter(id => id !== targetUserId)
      }

    },

    handleToggleLike(post) {
      console.log("[SearchResultsView] 点赞切换", post)
    },

        parseJson(value) {
      if (!value) return []
      if (Array.isArray(value)) return value
      try {
        return JSON.parse(value)
      } catch {
        return []
      }
    },

    formatTime(dateTime) {
      if (!dateTime) return '刚刚'
      const date = new Date(dateTime)
      const now = new Date()
      const diff = now - date
      if (diff < 60000) return '刚刚'
      if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
      if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
      if (diff < 604800000) return `${Math.floor(diff / 86400000)}天前`
      return `${date.getMonth() + 1}/${date.getDate()}`
    }

  }

}
</script>

<style scoped>

.search-results{
  background:#f5f5f7;
  min-height:100vh;
}


/* 顶部 */

.top-bar{
  display:flex;
  align-items:center;
  padding:10px;
  background:white;
}

.back{
  font-size:20px;
  margin-right:8px;
  cursor:pointer;
  transition:0.2s;
}

.back:hover{
  color:#8e7ddc;
}

.search-wrapper{
  flex:1;
}

.search-btn{
  color:#8e7ddc;
  margin-left:8px;
  cursor:pointer;
  transition:0.2s;
}

.search-btn:hover{
  color:#6d5bd0;
}


/* tabs */

.tabs{
  display:flex;
  justify-content:space-around;
  background:white;
  border-bottom:1px solid #eee;
}

.tab{
  padding:12px 0;
  font-size:14px;
  color:#666;
  position:relative;
  cursor:pointer;
  transition:0.2s;
}

.tab:hover{
  color:#8e7ddc;
}
.tab.active{
  color:#8e7ddc;
}


.tab.active::after{
  content:"";
  position:absolute;
  bottom:0;
  left:50%;
  transform:translateX(-50%);
  width:40px;
  height:3px;
  background:#8e7ddc;
  border-radius:3px;
}


/* 内容 */

.content{
  padding:12px;
}


/* 用户卡片 */

</style>