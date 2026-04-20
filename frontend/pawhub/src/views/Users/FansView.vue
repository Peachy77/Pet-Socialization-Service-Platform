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
        :following="!!user.following"
        @click="openUserInfo"
        @follow="handleFollow"
      />
    </div>
  </div>
</template>

<script>
import UserCard from "@/components/UserCard.vue"
import { followUser, getMyFollowers, getMyFollowing, unfollowUser } from "@/api/users"

export default {
  name: "FansView",

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
    await this.loadFollowersUsers()
  },

  methods: {
    async loadFollowersUsers() {
      try {
        const [followersResponse, followingResponse] = await Promise.all([
          getMyFollowers({ page: this.page, pageSize: this.pageSize }),
          getMyFollowing({ page: 1, pageSize: 200 })
        ])

        const followersPayload = this.unwrapPayload(followersResponse)
        const followingPayload = this.unwrapPayload(followingResponse)

        const followersList = this.extractList(followersPayload)
        const followingList = this.extractList(followingPayload)

        const followingIdSet = new Set(
          followingList
            .map(item => this.extractUserId(item))
            .filter(id => id !== null && id !== undefined)
            .map(id => String(id))
        )

        this.users = followersList
          .map(item => this.mapFollowerUser(item, followingIdSet))
          .filter(Boolean)
      } catch (error) {
        const msg = error?.response?.data?.message || error?.message || "加载粉丝列表失败"
        this.$message?.error?.(msg)
        this.users = []
      }
    },

    extractUserId(user) {
      if (!user || typeof user !== "object") return null
      return user.id ?? user.userId ?? user.user_id
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

    mapFollowerUser(user, followingIdSet = new Set()) {
      if (!user || typeof user !== "object") return null

      const userId = this.extractUserId(user)
      const backendFollowing = this.toBooleanFollowFlag(user.isFollowing ?? user.following ?? user.is_following)
      const inferredFollowing = userId !== null && userId !== undefined && followingIdSet.has(String(userId))

      return {
        id: userId,
        name: user.name || user.username || "未知用户",
        bio: user.bio || user.intro || "这个人很神秘，什么介绍也没有~",
        avatar: user.avatar || user.avatarUrl || "",
        following: backendFollowing || inferredFollowing
      }
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