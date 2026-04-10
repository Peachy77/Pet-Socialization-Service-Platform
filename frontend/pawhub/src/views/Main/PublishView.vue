<template>
  <div class="publish-page">

    <!-- 顶部栏 -->
    <div class="top-bar">
      <span class="back" @click="$router.back()">‹</span>
      <span class="title">发布动态</span>
      <span class="publish-btn">发布</span>
    </div>

    <!-- 内容区域 -->
    <div class="content">

      <!-- 文本输入 -->
      <textarea
        class="text-input"
        placeholder="分享你和宠物的美好时光..."
      ></textarea>

      <!-- 图片上传 -->
      <div class="image-upload">
        <div class="upload-box">
          <div class="icon">🖼</div>
          <div class="text">点击添加图片</div>
        </div>
      </div>

      <!-- 标签区域 -->
      <div class="tag-section">
        <div class="tag-title">添加标签</div>

        <div class="tag-input-row">
          <input
            type="text"
            placeholder="输入标签后按回车"
            class="tag-input"
          />

          <button class="add-btn">添加</button>
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

export default {
  name: "PublishView",
  components: {
    BottomNav
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

.upload-box{
  border:2px dashed #ddd;
  height:120px;
  border-radius:10px;
  display:flex;
  flex-direction:column;
  justify-content:center;
  align-items:center;
  color:#999;
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

.tag-tip{
  font-size:12px;
  color:#999;
  margin-top:6px;
}

</style>