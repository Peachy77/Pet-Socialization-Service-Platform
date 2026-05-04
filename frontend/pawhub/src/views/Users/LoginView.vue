<template>
  <div class="login-page">

    <!-- Logo区域 -->
    <div class="logo-area">
      <div class="logo">
        🐾
      </div>

      <div class="title">PawHub</div>
      <div class="subtitle">爪爪集</div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">

      <h2 class="welcome">欢迎回来</h2>

      <div class="form-item">
        <label>邮箱</label>
        <input
          type="text"
          v-model="form.account"
          placeholder="请输入邮箱"
        />
      </div>

      <div class="form-item">
        <label>密码</label>
        <input
          type="password"
          v-model="form.password"
          placeholder="请输入密码"
        />
      </div>

      <div class="forget">
        <a href="#">忘记密码？</a>
      </div>

      <button class="login-btn" @click="handleLogin">
        登录
      </button>

      <div class="register">
        还没有账号？
        <router-link to="/register">立即注册</router-link>
      </div>

    </div>

  </div>
</template>

<script>
import { login } from "@/api/users";

export default {
  name: "LoginView",

  data() {
    return {
      form: {
        account: "",
        password: ""
      },
      loading: false
    };
  },

  methods: {
    async handleLogin() {
      if (!this.form.account || !this.form.password) {
        this.$message.error("请输入账号和密码");
        return;
      }

      try {
        this.loading = true;

        const res = await login(this.form);
        const payload = res?.data || res || {};

        // client 的响应拦截器已返回 response.data，这里兼容两种结构
        const token = payload?.token;

        if (!token) {
          throw new Error("登录响应中未找到 token");
        }

        localStorage.setItem("token", token);

        const userId = payload?.userId || payload?.id;
        if (userId !== undefined && userId !== null) {
          localStorage.setItem("userId", String(userId));
        }

        localStorage.setItem("user", JSON.stringify({
          userId: userId,
          username: payload?.username || "",
          avatar: payload?.avatar || ""
      }));
      
        const profile = {
          username: payload?.username || "",
          email: payload?.email || this.form.account,
          avatar: payload?.avatar || "",
          bio: payload?.bio || "",
          followerCount: Number(payload?.followerCount ?? 0),
          followingCount: Number(payload?.followingCount ?? 0)
        };
        localStorage.setItem("pawhub_user_profile", JSON.stringify(profile));

        this.$message.success("登录成功");

        this.$router.push("/home");

      } catch (error) {
        const msg = error?.response?.data?.message || "登录失败，请检查账号密码";
        this.$message.error(msg);
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>

.login-page {
  min-height: 100vh;
  background: linear-gradient(to bottom,#8fb4c6,#87b6c6);
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 80px;
}

/* logo区域 */

.logo-area{
  text-align:center;
  margin-bottom:40px;
}

.logo{
  width:80px;
  height:80px;
  border-radius:50%;
  background:white;
  display:flex;
  align-items:center;
  justify-content:center;
  font-size:32px;
  margin:auto;
  box-shadow:0 4px 10px rgba(0,0,0,0.1);
}

.title{
  font-size:28px;
  margin-top:15px;
  font-weight:600;
}

.subtitle{
  color:#555;
  margin-top:6px;
}

/* 登录卡片 */

.login-card{
  width:320px;
  background:white;
  border-radius:20px;
  padding:30px;
  box-shadow:0 10px 25px rgba(0,0,0,0.15);
}

.welcome{
  text-align:center;
  margin-bottom:25px;
}

/* 输入框 */

.form-item{
  margin-bottom:18px;
}

.form-item label{
  display: block;
  font-size:14px;
  width: 100%;
  text-align: left;
  color:#333;

}

.form-item input{
  width:100%;
  margin-top:6px;
  padding:10px;
  border-radius:10px;
  border:1px solid #ddd;
  outline:none;
}

.form-item input:focus{
  border-color:#a8a2d5;
}

/* 忘记密码 */

.forget{
  text-align:right;
  font-size:13px;
  margin-bottom:15px;
}

.forget a{
  color:#8a84c8;
  text-decoration:none;
}

/* 登录按钮 */

.login-btn{
  width:100%;
  height:40px;
  border:none;
  border-radius:10px;
  background:#9a95c9;
  color:white;
  font-size:16px;
  cursor:pointer;
}

.login-btn:hover{
  opacity:0.9;
}

/* 注册 */

.register{
  margin-top:18px;
  text-align:center;
  font-size:14px;
}

.register a{
  color:#8a84c8;
  text-decoration:none;
}

</style>