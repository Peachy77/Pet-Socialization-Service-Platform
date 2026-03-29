<template>
  <div class="container">
    <h1>PawHub API 测试页面</h1>

    <button @click="loadUsers">Users</button>
    <button @click="loadPosts">Posts</button>
    <button @click="loadServices">Services</button>
    <button @click="loadOrders">Orders</button>
    <button @click="loadMessages">Messages</button>

    <h2>返回结果</h2>
    <pre>{{ result }}</pre>
  </div>
</template>

<script>
import * as usersApi from "../api/users";
import * as postsApi from "../api/posts";
import * as servicesApi from "../api/services";
import * as ordersApi from "../api/orders";
import * as messagesApi from "../api/messages";

export default {
  name: "TestApi",

  data() {
    return {
      result: ""
    };
  },

  methods: {
    async loadUsers() {
      const res = await usersApi.getUsers();
      this.result = res; //拦截器写了 response => response.data，所以不能写res.data
      console.log(res);
    },

    async loadPosts() {
      const res = await postsApi.getPosts();
      this.result = res;
      console.log(res);
    },

    async loadServices() {
      const res = await servicesApi.getServices();
      this.result = res;
      console.log(res);
    },

    async loadOrders() {
      const res = await ordersApi.getOrders();
      this.result = res;
      console.log(res);
    },

    async loadMessages() {
      const res = await messagesApi.getMessages();
      this.result = res;
      console.log(res);
    }
  }
};
</script>

<style>
.container {
  padding: 40px;
}

button {
  margin: 10px;
  padding: 10px 15px;
}
</style>