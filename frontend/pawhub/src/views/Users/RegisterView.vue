<template>
	<div class="register-page">
		<div class="logo-area">
			<div class="logo">🐾</div>
			<div class="title">PawHub</div>
			<div class="subtitle">爪爪集</div>
		</div>

		<div class="register-card">
			<h2 class="welcome">创建账号</h2>

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

			<div class="form-item">
				<label>确认密码</label>
				<input
					type="password"
					v-model="form.confirmPassword"
					placeholder="请再次输入密码"
				/>
			</div>

			<button class="register-btn" @click="handleRegister" :disabled="loading">
				{{ loading ? "注册中..." : "注册" }}
			</button>

			<div class="to-login">
				已有账户？
				<router-link to="/">去登录</router-link>
			</div>
		</div>
	</div>
</template>

<script>
import { register } from "@/api/users";

export default {
	name: "RegisterView",

	data() {
		return {
			form: {
				account: "",
				password: "",
				confirmPassword: ""
			},
			loading: false
		};
	},

	methods: {
		async handleRegister() {
			if (!this.form.account || !this.form.password || !this.form.confirmPassword) {
				this.$message.error("请完整填写注册信息");
				return;
			}

			if (this.form.password !== this.form.confirmPassword) {
				this.$message.error("两次密码输入不一致");
				return;
			}

			try {
				this.loading = true;

				await register({
					account: this.form.account,
					password: this.form.password
				});

				this.$message.success("注册成功，请登录");
				this.$router.push("/");
			} catch (error) {
				const msg = error?.response?.data?.message || "注册失败";
				this.$message.error(msg);
			} finally {
				this.loading = false;
			}
		}
	}
};
</script>

<style scoped>
.register-page {
	min-height: 100vh;
	background: linear-gradient(to bottom, #8fb4c6, #87b6c6);
	display: flex;
	flex-direction: column;
	align-items: center;
	padding-top: 80px;
}

.logo-area {
	text-align: center;
	margin-bottom: 40px;
}

.logo {
	width: 80px;
	height: 80px;
	border-radius: 50%;
	background: white;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 32px;
	margin: auto;
	box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.title {
	font-size: 28px;
	margin-top: 15px;
	font-weight: 600;
}

.subtitle {
	color: #555;
	margin-top: 6px;
}

.register-card {
	width: 320px;
	background: white;
	border-radius: 20px;
	padding: 30px;
	box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
}

.welcome {
	text-align: center;
	margin-bottom: 25px;
}

.form-item {
	margin-bottom: 18px;
}

.form-item label {
	display: block;
	width: 100%;
	text-align: left;
	font-size: 14px;
	color: #333;
	margin-bottom: 6px;
}

.form-item input {
	width: 100%;
	padding: 10px;
	border-radius: 10px;
	border: 1px solid #ddd;
	outline: none;
	box-sizing: border-box;
}

.form-item input:focus {
	border-color: #a8a2d5;
}

.register-btn {
	width: 100%;
	height: 40px;
	border: none;
	border-radius: 10px;
	background: #9a95c9;
	color: white;
	font-size: 16px;
	cursor: pointer;
}

.register-btn:hover {
	opacity: 0.9;
}

.register-btn:disabled {
	opacity: 0.7;
	cursor: not-allowed;
}

.to-login {
	margin-top: 18px;
	text-align: center;
	font-size: 14px;
}

.to-login a {
	color: #8a84c8;
	text-decoration: none;
}
</style>
