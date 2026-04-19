<template>
  <div class="publish-page">

    <!-- 顶部栏 -->
    <div class="top-bar">
      <span class="back" @click="$router.back()">‹</span>
      <span class="title">发布动态</span>
      <span class="publish-btn" :class="{ disabled: publishing }" @click="handlePublish">{{ publishing ? "发布中..." : "发布" }}</span>
    </div>

    <!-- 内容区域 -->
    <div class="content">

      <!-- 文本输入 -->
      <textarea
        v-model.trim="content"
        class="text-input"
        placeholder="分享你和宠物的美好时光..."
      ></textarea>

      <!-- 图片上传 -->
      <div class="image-upload">
        <div class="image-grid" v-if="imageUrls.length">
          <div v-for="(url, index) in imageUrls" :key="`${url}-${index}`" class="image-item">
            <img :src="url" alt="动态图片" class="uploaded-image" />
            <button class="remove-image-btn" @click="removeImage(index)">×</button>
          </div>
        </div>

        <label class="upload-box" v-if="imageUrls.length < maxImageCount">
          <div class="icon">🖼</div>
          <div class="text">点击添加图片</div>
          <div class="upload-sub">{{ imageUrls.length }}/{{ maxImageCount }}</div>
          <input
            type="file"
            accept="image/*"
            class="upload-input"
            @change="handleImageChange"
          />
        </label>
      </div>

      <!-- 标签区域 -->
      <div class="tag-section">
        <div class="tag-title">添加标签</div>

        <div class="tag-input-row">
          <input
            v-model.trim="tagInput"
            type="text"
            placeholder="输入标签后按回车"
            class="tag-input"
            @keyup.enter="addTag"
          />

          <button class="add-btn" @click="addTag">添加</button>
        </div>

        <div class="tag-list" v-if="tags.length">
          <span v-for="tag in tags" :key="tag" class="tag-chip">
            #{{ tag }}
            <button class="remove-tag-btn" @click="removeTag(tag)">×</button>
          </span>
        </div>

        <div class="tag-tip">
          最多添加5个标签
        </div>
      </div>

    </div>

    <!-- 底部导航 -->
    <BottomNav />

  </div>
</template>

<script>
import BottomNav from "@/components/BottomNav.vue"
import { createPost } from "@/api/posts"
import { uploadFile } from "@/api/upload"

export default {
  name: "PublishView",
  data() {
    return {
      content: "",
      imageUrls: [],
      tags: [],
      tagInput: "",
      maxTagCount: 5,
      maxImageCount: 9,
      publishing: false
    }
  },
  components: {
    BottomNav
  },
  methods: {
    unwrapPayload(response) {
      const code = response?.code
      const normalizedCode = code === undefined || code === null ? null : String(code)
      const isBusinessSuccess =
        normalizedCode === null ||
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
    },

    addTag() {
      if (!this.tagInput) return

      const normalized = this.tagInput.replace(/^#/, "").trim()
      if (!normalized) {
        this.tagInput = ""
        return
      }

      if (this.tags.includes(normalized)) {
        this.$message.warning("标签已存在")
        this.tagInput = ""
        return
      }

      if (this.tags.length >= this.maxTagCount) {
        this.$message.warning("最多添加 5 个标签")
        return
      }

      this.tags.push(normalized)
      this.tagInput = ""
    },

    removeTag(tag) {
      this.tags = this.tags.filter(item => item !== tag)
    },

    async handleImageChange(event) {
      const file = event.target.files && event.target.files[0]
      event.target.value = ""

      if (!file) return

      if (this.imageUrls.length >= this.maxImageCount) {
        this.$message.warning("最多上传 9 张图片")
        return
      }

      try {
        const response = await uploadFile(file)
        const url = this.unwrapPayload(response)

        if (!url || typeof url !== "string") {
          this.$message.warning("图片已上传，但未返回可用地址")
          return
        }

        this.imageUrls.push(url)
        this.$message.success("图片上传成功")
      } catch (error) {
        const msg = error?.response?.data?.message || error?.message || "图片上传失败"
        this.$message.error(msg)
      }
    },

    removeImage(index) {
      this.imageUrls.splice(index, 1)
    },

    async handlePublish() {
      if (this.publishing) return

      if (!this.content && this.imageUrls.length === 0) {
        this.$message.warning("请输入内容或上传图片后再发布")
        return
      }

      this.publishing = true

      try {
        const response = await createPost({
          content: this.content,
          images: this.imageUrls,
          tags: this.tags
        })

        const postId = this.unwrapPayload(response)
        this.$message.success(postId ? `发布成功，动态ID: ${postId}` : "发布成功")
        this.$router.push({ name: "home" })
      } catch (error) {
        const msg = error?.response?.data?.message || error?.message || "发布失败"
        this.$message.error(msg)
      } finally {
        this.publishing = false
      }
    }
  }
}
</script>

<style scoped>

.publish-page{
  min-height:100vh;
  background:#f5f5f7;
  padding-bottom:70px;
}

/* 顶部栏 */

.top-bar{
  height:50px;
  background:white;
  display:flex;
  align-items:center;
  justify-content:space-between;
  padding:0 15px;
  border-bottom:1px solid #eee;
}

.back{
  font-size:22px;
  cursor:pointer;
}

.title{
  font-size:16px;
  font-weight:600;
}

.publish-btn{
  color:#8a84c8;
  font-size:14px;
  cursor:pointer;
}

.publish-btn.disabled{
  color:#b9b4dc;
  cursor:not-allowed;
}

/* 内容区域 */

.content{
  padding:15px;
}

/* 文本输入 */

.text-input{
  width:100%;
  height:120px;
  border:none;
  outline:none;
  resize:none;
  font-size:14px;
  background:transparent;
  margin-bottom:20px;
}

/* 图片上传 */

.image-upload{
  background:white;
  padding:20px;
  border-radius:12px;
  margin-bottom:20px;
}

.image-grid{
  display:grid;
  grid-template-columns:repeat(3, 1fr);
  gap:10px;
  margin-bottom:12px;
}

.image-item{
  position:relative;
  aspect-ratio:1 / 1;
  border-radius:10px;
  overflow:hidden;
}

.uploaded-image{
  width:100%;
  height:100%;
  object-fit:cover;
}

.remove-image-btn{
  position:absolute;
  right:6px;
  top:6px;
  width:22px;
  height:22px;
  border:none;
  border-radius:50%;
  background:rgba(0, 0, 0, 0.6);
  color:white;
  line-height:1;
  cursor:pointer;
}

.upload-box{
  border:2px dashed #ddd;
  height:120px;
  border-radius:10px;
  display:flex;
  flex-direction:column;
  justify-content:center;
  align-items:center;
  color:#999;
  cursor:pointer;
}

.upload-input{
  display:none;
}

.upload-sub{
  margin-top:6px;
  font-size:12px;
}

.icon{
  font-size:28px;
  margin-bottom:5px;
}

/* 标签 */

.tag-section{
  background:white;
  padding:15px;
  border-radius:12px;
}

.tag-title{
  font-size:14px;
  margin-bottom:10px;
}

.tag-input-row{
  display:flex;
  gap:10px;
}

.tag-input{
  flex:1;
  height:36px;
  border:1px solid #eee;
  border-radius:18px;
  padding:0 12px;
  outline:none;
}

.add-btn{
  background:#8a84c8;
  border:none;
  color:white;
  padding:0 16px;
  border-radius:18px;
  cursor:pointer;
}

.tag-list{
  display:flex;
  flex-wrap:wrap;
  gap:8px;
  margin-top:10px;
}

.tag-chip{
  display:inline-flex;
  align-items:center;
  gap:6px;
  padding:6px 10px;
  border-radius:999px;
  background:#f0edff;
  color:#6f63b8;
  font-size:12px;
}

.remove-tag-btn{
  width:16px;
  height:16px;
  border:none;
  border-radius:50%;
  background:#d8d1ff;
  color:#544aa1;
  line-height:1;
  cursor:pointer;
}

.tag-tip{
  font-size:12px;
  color:#999;
  margin-top:6px;
}

</style>