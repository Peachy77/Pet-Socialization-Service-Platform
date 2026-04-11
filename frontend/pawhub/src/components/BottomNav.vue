<template>
  <div class="bottom-nav">

<router-link to="/home" class="nav-item">
  <div>🏠</div>
  <span>首页</span>
</router-link>

<router-link to="/publish" class="nav-item">
  <div>➕</div>
  <span>发布</span>
</router-link>

<router-link to="/message" class="nav-item">
  <div>💬</div>
  <span>消息</span>
</router-link>

<router-link to="/mine" class="nav-item">
  <div>👤</div>
  <span>我的</span>
</router-link>

  </div>
</template>

<script>
export default{
  name:"BottomNav"
}
</script>

<style scoped>

.bottom-nav{
  position:fixed;
  bottom:0;
  left:0;
  right:0;
  height:60px;
  background:white;
  border-top:1px solid #eee;
  display:flex;
  justify-content:space-around;
  align-items:center;
}

.nav-item{
  text-align:center;
  font-size:12px;
  color:#666;
  text-decoration:none;
}

.nav-item div{
  font-size:22px;
}

.router-link-exact-active{
  color:#8a84c8;
  background-color: #f0eef8;  
  border-radius: 16px;         
  padding: 5px 10px;          
}

</style>
