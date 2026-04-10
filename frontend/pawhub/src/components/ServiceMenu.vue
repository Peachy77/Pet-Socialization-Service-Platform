<template>
  <div class="service-menu">

<div
  class="menu-item"
  v-for="item in menus"
  :key="item.name"
  @click="goPage(item)"
>
  <div class="icon">
    {{ item.icon }}
  </div>

  <div class="text">
    {{ item.name }}
  </div>
</div>

  </div>
</template>

<script>
export default {
  name: "ServiceMenu",

  data() {
    return {
      menus: [
        { name: "美容", icon: "✂️" },
        { name: "遛狗", icon: "🐕" },
        { name: "寄养", icon: "🏠" },
        { name: "托管", icon: "❤️" },
        { name: "医院", icon: "🩺" },
        { name: "救助", icon: "❗" }
      ]
    };
  },

  methods: {
    goPage(item) {
      this.$router.push({
        path: "/service",
        query: {
          type: item.name
        }
      });
    }
  }
};
</script>

<style scoped>

.service-menu{
  display:flex;
  justify-content:space-between;
  margin:20px 0;
}

.menu-item{
  display:flex;
  flex-direction:column;
  align-items:center;
  font-size:12px;
  cursor:pointer;
}

.icon{
  width:46px;
  height:46px;
  border-radius:50%;
  background:#f0f1f6;
  display:flex;
  align-items:center;
  justify-content:center;
  font-size:22px;
  margin-bottom:6px;
}

.text{
  color:#333;
}

.menu-item:hover .icon{
  background:linear-gradient(#8d8df0, #cca0f9);
}

</style>
