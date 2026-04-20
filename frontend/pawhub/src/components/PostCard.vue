<template>
  <div class="post-card" @click="openDetail">

    <!-- 用户信息 -->
    <div class="post-header">
      <img :src="post.avatar" class="avatar" />

      <div class="info">
        <div class="name">{{ post.name }}</div>
        <div class="time">{{ post.time }}</div>
      </div>
    </div>

    <!-- 图片 -->
    <div
      class="post-images"
      v-if="showImages.length"
      :class="{ single: showImages.length === 1 }"
    >
      <img
        v-for="(img,index) in showImages"
        :key="index"
        :src="img"
      />
    </div>

    <!-- 内容 + 标签：撑开中间空间 -->
    <div class="content-area">
      <div class="content">
        {{ post.content }}
      </div>

      <div class="tags">
        <span v-for="tag in post.tags" :key="tag">#{{ tag }}</span>
      </div>
    </div>

    <!-- 底部 -->
    <div class="post-footer">
      <div class="like-btn" :class="{ liked }" @click.stop="toggleLike">
        <svg viewBox="0 0 24 24" class="heart-icon" aria-hidden="true">
          <path
            d="M12 21s-7.2-4.7-9.6-9C.6 8.7 2.1 5.2 5.6 5c2.1-.1 3.4 1 4.4 2.2C11 6 12.3 4.9 14.4 5c3.5.2 5 3.7 3.2 7-2.4 4.3-9.6 9-9.6 9z"
          />
        </svg>
        <span>{{ likeCount }}</span>
      </div>
      <div>💬 {{ post.comments }}</div>      <div v-if="post.isMine" class="delete-btn" @click.stop="deletePost">
        🗑
      </div>    </div>

  </div>
</template>

<script>
export default {
  name: "PostCard",

  props: {
    post: Object
  },

  computed: {
    // 最多显示两张图片
    showImages() {
      if(!this.post.images) return []
      return this.post.images.slice(0,2)
    },

    liked() {
      return !!this.post.liked
    },

    likeCount() {
      return Number(this.post.likes || 0)
    }
  },

  methods: {
    toggleLike() {
      this.$emit("toggle-like", this.post)
    },

    deletePost() {
      this.$emit("delete-post", this.post)
    },

    openDetail() {
      this.$router.push({
        name: "postDetail",
        query: {
          id: this.post.id,
          name: this.post.name,
          time: this.post.time,
          avatar: this.post.avatar,
          content: this.post.content,
          images: JSON.stringify(this.post.images || []),
          tags: JSON.stringify(this.post.tags || []),
          likes: this.post.likes,
          comments: this.post.comments
        }
      })
    }
  }
}
</script>

<style scoped>
.post-card{
  background:white;
  border-radius:16px;
  padding:15px;
  margin-bottom:20px;
  box-shadow:0 2px 10px rgba(0,0,0,0.05);
  cursor:pointer;

  /* 固定高度，让footer贴底 */
  display:flex;
  flex-direction:column;
  height:auto;
}

/* 用户信息 */
.post-header{
  display:flex;
  align-items:center;
  margin-bottom:10px;
}

.avatar{
  width:40px;
  height:40px;
  border-radius:50%;
  margin-right:10px;
}

.name{
  font-weight:600;
}

.time{
  font-size:12px;
  color:#888;
}

/* 图片区域 */
.post-images{
  display:grid;
  grid-template-columns:repeat(2,1fr);
  gap:6px;
  margin-bottom:10px;
}

/* 单张图片 */
.post-images.single{
  grid-template-columns:1fr;
}

.post-images img{
  width:100%;
  aspect-ratio:1/1;
  object-fit:cover;
  border-radius:8px;
}

/* 单图高度更大 */
.post-images.single img{
  aspect-ratio:16/10;
}

/* 内容 + 标签区域撑开中间空间 */
.content-area{
  flex:1; /* 撑开剩余空间，让footer贴底 */
  display:flex;
  flex-direction:column;
}

.content{
  font-size:14px;
  margin-bottom:8px;
  overflow:hidden;
}

.tags{
  margin-top:auto; /* 标签在内容下方，撑开空间 */
}

.tags span{
  background:#f2f2ff;
  padding:4px 8px;
  border-radius:10px;
  font-size:12px;
  margin-right:6px;
}

/* 底部操作栏 */
.post-footer{
  display:flex;
  justify-content:space-between;
  margin-top:10px;
  color:#666;
  font-size:14px;
  border-top:1px solid #eee;
  padding-top:8px;
}

.like-btn{
  display:flex;
  align-items:center;
  gap:4px;
  transition:0.2s;
}

.heart-icon{
  width:17px;
  height:17px;
}

.heart-icon path{
  fill:transparent;
  stroke:#666;
  stroke-width:1.8;
  transition:0.2s;
}

.like-btn.liked{
  color:#ef4444;
}

.like-btn.liked .heart-icon path{
  fill:#ef4444;
  stroke:#ef4444;
}

.delete-btn{
  padding:0;
  border:none;
  background:transparent;
  color:#999;
  cursor:pointer;
  font-size:18px;
  transition:0.2s;
}

.delete-btn:hover{
  color:#ef4444;
}
</style>