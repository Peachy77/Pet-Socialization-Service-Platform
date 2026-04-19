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
import { likePost, unlikePost } from "@/api/posts"

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

      likingPostIds:[],

      posts:[
         {
          id:1,
          name:"柴犬小乖",
          time:"2小时前",
          avatar:"https://i.pravatar.cc/100?img=3",
          images:[
            "https://images.unsplash.com/photo-1583511655857-d19b40a7a54e",
            "https://images.unsplash.com/photo-1558788353-f76d92427f16"
          ],
          content:"今天带我家毛孩子去美容院做了个新造型，超级可爱！",
          tags:["宠物美容","柴犬"],
          likes:234,
          comments:45,
          liked:false
        }
      ],

      users:[
        {
          id:1,
          name:"用户名1",
          bio:"简介内容...",
          avatar:"https://placekitten.com/100/100"
        },
        {
          id:2,
          name:"用户名2",
          bio:"简介内容...",
          avatar:"https://placekitten.com/101/101"
        }
      ],

      services:[
        {
          id:1,
          name:"爱宠美容工作室",
          address:"朝阳区建国路88号",
          rating:"4.8",
          distance:"1.2",
          tags:["洗澡","美容","造型"],
          price:"¥88起",
          image:"https://placekitten.com/200/200"
        }
      ]

    }
  },

  created(){
    this.posts = this.normalizePosts(this.posts)
  },

  methods:{

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

    normalizePosts(posts){
      return (posts || [])
        .map(post => {
          const directFlag = post.liked ?? post.isLiked ?? post.is_liked ?? post.hasLiked ?? post.likeStatus
          const liked = directFlag !== undefined && directFlag !== null
            ? this.toBooleanLikeFlag(directFlag)
            : false

          const likedUserIds = this.toArray(post.likedUserIds || post.likeUserIds || post.likerIds)
          const currentUserId = String(localStorage.getItem("userId") || "")
          const likedByList = currentUserId
            ? likedUserIds.map(item => String(item)).includes(currentUserId)
            : false

          return {
            ...post,
            liked: directFlag !== undefined && directFlag !== null ? liked : likedByList
          }
        })
        .filter(Boolean)
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