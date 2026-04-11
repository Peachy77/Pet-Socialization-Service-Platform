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
          <div class="num">125</div>
          <div class="label">关注</div>
        </div>

        <div class="stat clickable" @click="goFansList">
          <div class="num">568</div>
          <div class="label">粉丝</div>
        </div>

        <div class="stat">
          <div class="num">2345</div>
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

export default {
  name: "MineView",

  components: {
    BottomNav,
    PostCard,
    ServiceCard,
    OrderList
  },

  data(){
    return{
      profile:{
        email:"3153159098@qq.com",
        username:"宠物爱好者",
        avatar:"https://images.unsplash.com/photo-1601758123927-1967a3f7f3b4",
        bio:"热爱生活，爱宠物 🐾",
        password:"123456"
      },
      activeTab:"posts",
      posts:[
        {
          id:1,
          avatar:"https://images.unsplash.com/photo-1601758123927-1967a3f7f3b4",
          name:"柴犬小乖",
          time:"2小时前",
          images:[
            "https://images.unsplash.com/photo-1548199973-03cce0bbc87b",
            "https://images.unsplash.com/photo-1558788353-f76d92427f16"
          ],
          content:"今天带狗狗去公园散步，太开心啦！",
          tags:["遛狗","柴犬"],
          likes:128,
          comments:32
        }
      ],
      favoriteServices:[
        {
          id:101,
          type:"pet",
          name:"萌宠乐园",
          image:"https://images.unsplash.com/photo-1525253013412-55c1a69a5738",
          address:"南山大道 88 号",
          rating:"4.9",
          distance:"1.2",
          tags:["洗护","寄养","接送"],
          price:"¥88"
        },
        {
          id:102,
          type:"pet",
          name:"爱宠美容工作室",
          image:"https://images.unsplash.com/photo-1517849845537-4d257902454a",
          address:"花园路 26 号",
          rating:"4.8",
          distance:"2.1",
          tags:["美容","护理","SPA"],
          price:"¥158"
        }
      ],
      orders:[
        {
          id:1,
          status:"pending",
          userName:"3153159098@qq.com",
          serviceName:"精致美容",
          merchantName:"爱宠美容工作室",
          time:"2026-03-15 14:00",
          appointmentTime:"2026-03-15 14:00",
          remark:"希望美容后做指甲修剪，狗狗对吹风声音比较敏感。",
          price:"¥158",
          orderTime:"2026-03-13 19:26",
          updateTime:"2026-03-13 19:26"
        },
        {
          id:2,
          status:"completed",
          userName:"3153159098@qq.com",
          serviceName:"基础洗澡",
          merchantName:"萌宠乐园",
          time:"2026-03-10 10:00",
          appointmentTime:"2026-03-10 10:00",
          remark:"使用低敏沐浴露。",
          price:"¥88",
          orderTime:"2026-03-08 16:20",
          updateTime:"2026-03-10 11:45"
        },
        {
          id:3,
          status:"cancelled",
          userName:"3153159098@qq.com",
          serviceName:"豪华SPA",
          merchantName:"毛孩子SPA会所",
          time:"2026-03-08 15:00",
          appointmentTime:"2026-03-08 15:00",
          remark:"临时有事，已提前取消。",
          price:"¥288",
          orderTime:"2026-03-06 14:05",
          updateTime:"2026-03-07 09:30"
        }
      ]
    }
  },

  created(){
    this.loadProfile()
    this.applyOrderStatusFromStorage()
  },

  methods:{
    loadProfile(){
      const stored = JSON.parse(localStorage.getItem("pawhub_user_profile") || "null")
      if(!stored) return
      this.profile = {
        ...this.profile,
        ...stored
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
          order: encodeURIComponent(JSON.stringify(order))
        }
      })
    },

    cancelOrder(order){
      if (order.status !== "pending") return

      const confirmed = window.confirm("确认取消订单吗？")
      if (!confirmed) return

      const updateTime = this.formatNow()
      const target = this.orders.find(item => item.id === order.id)
      if (!target) return

      target.status = "cancelled"
      target.updateTime = updateTime
      this.saveOrderStatus(target.id, target.status, updateTime)
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

    saveOrderStatus(orderId, status, updateTime){
      const cache = JSON.parse(localStorage.getItem("pawhub_order_status") || "{}")
      cache[orderId] = { status, updateTime }
      localStorage.setItem("pawhub_order_status", JSON.stringify(cache))
    },

    applyOrderStatusFromStorage(){
      const cache = JSON.parse(localStorage.getItem("pawhub_order_status") || "{}")
      this.orders = this.orders.map(order => {
        const stored = cache[order.id]
        if (!stored) return order
        return {
          ...order,
          status: stored.status || order.status,
          updateTime: stored.updateTime || order.updateTime
        }
      })
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