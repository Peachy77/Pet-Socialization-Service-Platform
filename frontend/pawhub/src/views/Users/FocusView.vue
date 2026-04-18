<template>
  <div class="user-list-page">
    <div class="topbar">
      <button class="back-btn" @click="goBack">‹</button>
      <div class="title">关注列表</div>
      <span class="spacer"></span>
    </div>


    <div class="list-wrap">
      <UserCard
        v-for="user in users"
        :key="user.id"
        :user="user"
        :following="true"
        @click="openUserInfo"
      />
    </div>
  </div>
</template>

<script>
import UserCard from "@/components/UserCard.vue"

export default {
  name: "FocusView",

  components: {
    UserCard
  },

  data() {
    return {
      users: [
        {
          id: 1,
          name: "爱宠美容工作室",
          bio: "专注宠物美容、护理与造型服务。",
          avatar: "https://images.unsplash.com/photo-1517841905240-472988babdf9"
        },
        {
          id: 2,
          name: "萌宠乐园",
          bio: "提供洗护、寄养、接送一站式服务。",
          avatar: "https://images.unsplash.com/photo-1525253086316-d0c936c814f8"
        },
        {
          id: 3,
          name: "毛孩子SPA会所",
          bio: "精致护理，让毛孩子放松又舒适。",
          avatar: "https://images.unsplash.com/photo-1552053831-71594a27632d"
        }
      ]
    }
  },

  methods: {
    goBack() {
      this.$router.back()
    },

    openUserInfo(user) {
      this.$router.push({
        name: "userInformation",
        query: {
          user: encodeURIComponent(JSON.stringify(user))
        }
      })
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


.summary-desc{
  margin-top:6px;
  font-size:13px;
  color:#6b7280;
}

.list-wrap{
  padding-bottom:12px;
}
</style>