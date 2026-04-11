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
  />
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
          comments:45
        },

        {
          id:2,
          name:"猫咪铲屎官",
          time:"5小时前",
          avatar:"https://i.pravatar.cc/100?img=5",
          images:[
            "https://images.unsplash.com/photo-1518791841217-8f162f1e1131"
          ],
          content:"我家猫猫今天超级乖，在阳台晒太阳一下午。",
          tags:["猫咪","日常"],
          likes:120,
          comments:12
        },

        {
          id:3,
          name:"金毛阿福",
          time:"昨天",
          avatar:"https://i.pravatar.cc/100?img=8",
          images:[
            "https://images.unsplash.com/photo-1507146426996-ef05306b995a",
            "https://images.unsplash.com/photo-1561037404-61cd46aa615b",
            "https://images.unsplash.com/photo-1548199973-03cce0bbc87b"
          ],
          content:"带阿福去海边玩了一整天，太开心啦！",
          tags:["金毛","海边"],
          likes:380,
          comments:60
        },

        {
          id:4,
          name:"布偶猫团子",
          time:"昨天",
          avatar:"https://i.pravatar.cc/100?img=9",
          images:[
            "https://images.unsplash.com/photo-1513364776144-60967b0f800f",
            "https://images.unsplash.com/photo-1592194996308-7b43878e84a6",
            "https://images.unsplash.com/photo-1518791841217-8f162f1e1131",
            "https://images.unsplash.com/photo-1574158622682-e40e69881006"
          ],
          content:"新买的猫爬架，团子玩疯了。",
          tags:["布偶猫"],
          likes:188,
          comments:21
        },

        {
          id:5,
          name:"宠物摄影师",
          time:"1天前",
          avatar:"https://i.pravatar.cc/100?img=11",
          images:[

            /* 九宫格 */

            "https://images.unsplash.com/photo-1543466835-00a7907e9de1",
            "https://images.unsplash.com/photo-1552053831-71594a27632d",
            "https://images.unsplash.com/photo-1568572933382-74d440642117",

            "https://images.unsplash.com/photo-1583511655826-05700442b31b",
            "https://images.unsplash.com/photo-1507149833265-60c372daea22",
            "https://images.unsplash.com/photo-1517423440428-a5a00ad493e8",

            "https://images.unsplash.com/photo-1558788353-f76d92427f16",
            "https://images.unsplash.com/photo-1518717758536-85ae29035b6d",
            "https://images.unsplash.com/photo-1583337130417-3346a1be7dee"
          ],
          content:"今天拍了一组狗狗写真，分享给大家。",
          tags:["宠物摄影"],
          likes:650,
          comments:90
        }
      ]

    }
  },

  methods:{

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



</style>
