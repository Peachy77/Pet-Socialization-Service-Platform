<template>
  <div class="user-list-page">
    <div class="topbar">
      <button class="back-btn" @click="goBack">‹</button>
      <div class="title">粉丝列表</div>
      <span class="spacer"></span>
    </div>

    <div class="summary-card">
      <div class="summary-title">关注我的用户</div>
      <div class="summary-desc">这里展示所有关注你的粉丝</div>
    </div>

    <div class="list-wrap">
      <UserCard
        v-for="user in users"
        :key="user.id"
        :user="user"
        :following="user.following"
        @follow="handleFollow"
      />
    </div>
  </div>
</template>

<script>
import UserCard from "@/components/UserCard.vue"

export default {
  name: "FansView",

  components: {
    UserCard
  },

  data() {
    return {
      users: [
        {
          id: 1,
          name: "宠物摄影师小林",
          bio: "喜欢记录每一只可爱的小家伙。",
          avatar: "https://images.unsplash.com/photo-1500648767791-00dcc994a43e",
          following: false
        },
        {
          id: 2,
          name: "喵星人日记",
          bio: "分享猫咪日常与养护技巧。",
          avatar: "https://images.unsplash.com/photo-1517849845537-4d257902454a",
          following: true
        },
        {
          id: 3,
          name: "遛狗小分队",
          bio: "每天都和狗狗一起去散步。",
          avatar: "https://images.unsplash.com/photo-1544717305-2782549b5136",
          following: false
        }
      ]
    }
  },

  methods: {
    goBack() {
      this.$router.back()
    },

    handleFollow(user) {
      user.following = true
    }
  }
}
</script>

<style scoped>
.user-list-page{
  min-height:100vh;
  background:linear-gradient(180deg, #f8f5ff 0%, #f5f5f7 100%);
  padding:12px 14px 20px;
}

.topbar{
  display:flex;
  align-items:center;
  justify-content:space-between;
  margin-bottom:14px;
}

.back-btn{
  width:36px;
  height:36px;
  border:none;
  background:white;
  border-radius:10px;
  font-size:26px;
  line-height:1;
  box-shadow:0 2px 8px rgba(51,65,85,0.12);
}

.title{
  font-size:20px;
  font-weight:700;
  color:#111827;
}

.spacer{
  width:36px;
}

.summary-card{
  background:white;
  border-radius:16px;
  padding:14px;
  box-shadow:0 8px 20px rgba(15,23,42,0.06);
  border:1px solid #eceff4;
  margin-bottom:12px;
}

.summary-title{
  font-size:16px;
  font-weight:700;
  color:#111827;
}

.summary-desc{
  margin-top:6px;
  font-size:13px;
  color:#6b7280;
}

.list-wrap{
  padding-bottom:12px;
}
</style>