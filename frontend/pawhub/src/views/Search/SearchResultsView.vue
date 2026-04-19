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
import { searchUsers } from "@/api/users"
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

  created(){
    this.posts = this.normalizePosts(this.posts)
  },

  methods:{

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
        const [postRes, userRes, serviceRes] = await Promise.all([
          this.searchPosts(),
          this.searchUsers(),
          this.searchServices()
        ])

        // 处理动态结果
        if (postRes && (postRes.code === 1 || postRes.code === 0)) {
          const list = postRes.data?.list || []
          this.posts = list.map(p => ({
            id: p.post_id,
            name: p.username,
            time: this.formatTime(p.create_time),
            avatar: p.avatar || 'default.jpg',
            images: this.parseJson(p.images),
            content: p.content,
            tags: this.parseJson(p.tags),
            likes: p.like_count || 0,
            comments: p.comment_count || 0
          }))
        } else {
          this.posts = []
        }

        // 处理用户结果
        if (userRes && (userRes.code === 1 || userRes.code === 0)) {
          const list = userRes.data?.list || []
          this.users = list.map(u => ({
            id: u.user_id,
            name: u.username,
            bio: u.bio || '',
            avatar: u.avatar || 'default.jpg'
          }))
        } else {
          this.users = []
        }

        // 处理商户结果
        if (serviceRes && (serviceRes.code === 1 || serviceRes.code === 0)) {
          const list = serviceRes.data?.list || []
          this.services = list.map(s => ({
            id: s.service_id,
            name: s.name,
            address: s.address || '暂无地址',
            rating: s.rating || 0,
            distance: s.distance || '未知',
            tags: this.parseJson(s.tags) || [],
            price: s.price ? `¥${s.price}起` : '暂无',
            image: s.image || (s.images && s.images[0]) || 'https://placekitten.com/200/200'
          }))
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
      console.log("关注用户", user)

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