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
        :following="!!user.following"
        @click="openUserInfo"
        @follow="handleFollow"
      />
    </div>
  </div>
</template>

<script>
import UserCard from "@/components/UserCard.vue"
import { followUser, getMyFollowing, unfollowUser } from "@/api/users"

export default {
  name: "FocusView",

  components: {
    UserCard
  },

  data() {
    return {
      followingUserIds: [],
      users: [],
      page: 1,
      pageSize: 20
    }
  },

  async created() {
    await this.loadFollowingUsers()
  },

  methods: {
    async loadFollowingUsers() {
      try {
        const response = await getMyFollowing({ page: this.page, pageSize: this.pageSize })
        const payload = this.unwrapPayload(response)
        const list = this.extractList(payload)
        this.users = list.map(item => this.mapFollowingUser(item)).filter(Boolean)
      } catch (error) {
        const msg = error?.response?.data?.message || error?.message || "加载关注列表失败"
        this.$message?.error?.(msg)
        this.users = []
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

    mapFollowingUser(user) {
      if (!user || typeof user !== "object") return null

      return {
        id: user.id ?? user.userId ?? user.user_id,
        name: user.name || user.username || "未知用户",
        bio: user.bio || user.intro || "这个人很神秘，什么介绍也没有~",
        avatar: user.avatar || user.avatarUrl || "",
        following: true
      }
    },

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
    },

    async handleFollow(user) {
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
        this.unwrapPayload(response)

        if (previousFollowing) {
          this.users = this.users.filter(item => item.id !== targetUserId)
        } else {
          this.users = this.users.map(item => (
            item.id === targetUserId
              ? { ...item, following: true }
              : item
          ))
        }
      } catch (error) {
        const msg = error?.response?.data?.message || error?.message || (previousFollowing ? "取消关注失败" : "关注失败")
        this.$message?.error?.(msg)
      } finally {
        this.followingUserIds = this.followingUserIds.filter(id => id !== targetUserId)
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