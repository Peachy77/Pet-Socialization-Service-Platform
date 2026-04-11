<template>
  <div class="user-card" @click="$emit('click', user)">
    <img :src="user.avatar" class="avatar" />

    <div class="user-info">
      <div class="username">
        {{ user.name }}
      </div>

      <div class="bio">
        {{ user.bio }}
      </div>
    </div>

    <button
      class="follow"
      :class="{ following }"
      :disabled="following"
      @click.stop="handleAction"
    >
      {{ following ? '已关注' : '关注' }}
    </button>
  </div>
</template>

<script>
export default {
  name: "UserCard",

  props: {
    user: {
      type: Object,
      required: true
    },
    following: {
      type: Boolean,
      default: false
    }
  },

  methods: {
    handleAction() {
      if (this.following) return
      this.$emit('follow', this.user)
    }
  }
}
</script>

<style scoped>
.user-card{
  display:flex;
  align-items:center;
  background:white;
  padding:12px;
  border-radius:14px;
  margin-bottom:12px;
  justify-content:space-between;
  width:100%;
}

.avatar{
  width:48px;
  height:48px;
  border-radius:50%;
  object-fit:cover;
}

.user-info{
  flex:1;
  margin-left:10px;
  text-align:left;
}

.username{
  font-size:15px;
  font-weight:600;
}

.bio{
  font-size:13px;
  color:#888;
  margin-top:4px;
}

.follow{
  background:#9d8bdc;
  color:white;
  border:none;
  padding:6px 16px;
  border-radius:18px;
  font-size:13px;
  cursor:pointer;
  transition:0.2s;
}

.follow:hover{
  background:#8673d6;
}

.follow.following{
  background:#eef2ff;
  color:#6b7280;
  cursor:default;
}

.follow.following:hover{
  background:#eef2ff;
}
</style>