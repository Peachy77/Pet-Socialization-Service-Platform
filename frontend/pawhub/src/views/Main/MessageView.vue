<template>
  <div class="message-page">

    <!-- 顶部标题 -->
    <div class="header">
      消息通知
    </div>

    <!-- 消息列表 -->
    <MessageList />

    <!-- 底部导航 -->
    <BottomNav />

  </div>
</template>

<script>
import BottomNav from "@/components/BottomNav.vue"
import MessageList from "@/components/MessageList.vue"

export default {
  name: "MessageView",
  components: {
    BottomNav,
    MessageList
  }
}
</script>

<style scoped>

.message-page{
  min-height:100vh;
  background:#f5f5f7;
  padding-bottom:70px;
}

/* 顶部 */

.header{
  background:white;
  font-size:18px;
  font-weight:600;
  padding:16px;
  border-bottom:1px solid #eee;
  text-align:left;
}

</style>