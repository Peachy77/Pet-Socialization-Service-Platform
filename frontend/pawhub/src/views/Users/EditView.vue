<template>
	<div class="edit-page">
		<div class="hero">
			<div class="topbar">
				<button class="back-btn" @click="goBack">‹</button>
				<div class="title">编辑资料</div>
				<span class="spacer"></span>
			</div>

			<div class="hero-info">
				<div class="hero-label">当前账号</div>
				<div class="hero-email">{{ form.email }}</div>
				<div class="hero-tip">邮箱仅用于登录显示，不可修改</div>
			</div>
		</div>

		<div class="form-card">
			<div class="section-title">基础资料</div>

			<div class="avatar-panel">
				<img :src="avatarPreview" class="avatar" />
				<div class="avatar-actions">
					<div class="avatar-name">头像</div>
					<div class="avatar-desc">上传新的头像图片，保存后立即生效</div>
					<label class="upload-btn">
						更换头像
						<input type="file" accept="image/*" @change="handleAvatarChange" />
					</label>
				</div>
			</div>

			<div class="field">
				<label>用户名</label>
				<input v-model.trim="form.username" type="text" placeholder="请输入用户名" />
			</div>

			<div class="field">
				<label>邮箱账号</label>
				<input :value="form.email" type="text" disabled />
			</div>

			<div class="field">
				<label>个人简介</label>
				<textarea v-model.trim="form.bio" rows="4" placeholder="写一句介绍自己的话"></textarea>
			</div>
		</div>

		<div class="form-card">
			<div class="section-title">密码修改</div>

			<div class="field">
				<label>旧密码</label>
				<input v-model="form.oldPassword" type="password" placeholder="请输入当前密码" />
			</div>

			<div class="field">
				<label>新密码</label>
				<input v-model="form.newPassword" type="password" placeholder="请输入新密码" />
			</div>

			<div class="field">
				<label>确认新密码</label>
				<input v-model="form.confirmPassword" type="password" placeholder="请再次输入新密码" />
			</div>

			<div class="password-hint">
				为了安全，修改密码时必须先验证旧密码。
			</div>
		</div>

		<div class="action-bar">
			<button class="cancel-btn" @click="goBack">取消</button>
			<button class="save-btn" @click="handleSave">保存修改</button>
		</div>
	</div>
</template>

<script>
import { updateCurrentUser } from "@/api/users"
import { uploadFile } from "@/api/upload"

export default {
	name: "EditView",

	data() {
		return {
			avatarPreview: "https://images.unsplash.com/photo-1601758123927-1967a3f7f3b4",
			form: {
				username: "宠物爱好者",
				email: "3153159098@qq.com",
				bio: "热爱生活，爱宠物 🐾",
				avatar: "https://images.unsplash.com/photo-1601758123927-1967a3f7f3b4",
				password: "123456",
				oldPassword: "",
				newPassword: "",
				confirmPassword: ""
			}
		}
	},

	created() {
		this.loadProfile()
	},

	methods: {
		loadProfile() {
			const stored = JSON.parse(localStorage.getItem("pawhub_user_profile") || "null")
			if (!stored) {
				this.avatarPreview = this.form.avatar
				return
			}

			this.form = {
				...this.form,
				...stored,
				oldPassword: "",
				newPassword: "",
				confirmPassword: ""
			}
			this.avatarPreview = stored.avatar || this.form.avatar
		},

		resolveUploadedAvatarUrl(response) {
			if (!response) return ""

			if (typeof response === "string") {
				return response
			}

			if (typeof response !== "object") {
				return ""
			}

			const candidates = [
				response.data,
				response.url,
				response.path,
				response.fileUrl,
				response.filePath
			]

			for (const candidate of candidates) {
				if (typeof candidate === "string" && candidate) {
					return candidate
				}
			}

			if (response.data && typeof response.data === "object") {
				return this.resolveUploadedAvatarUrl(response.data)
			}

			return ""
		},

		handleAvatarChange(event) {
		const file = event.target.files && event.target.files[0]
		if (!file) return

		// 先本地预览
		this.avatarPreview = URL.createObjectURL(file)

		// 上传到后端
		uploadFile(file).then(res => {
			const url = this.resolveUploadedAvatarUrl(res)  // 兼容后端多层返回结构

			if (!url) {
				this.$message.warning("头像已上传，但未返回可用地址")
				return
			}

			this.form.avatar = url
			this.avatarPreview = url

			this.$message.success("头像上传成功")
		}).catch(() => {
			this.$message.error("头像上传失败")
		})
		},

		handleSave() {
			if (!this.form.username) {
				this.$message.error("请输入用户名")
				return
			}

			if (!this.form.bio) {
				this.$message.error("请输入个人简介")
				return
			}

			const wantsChangePassword = !!(
				this.form.oldPassword ||
				this.form.newPassword ||
				this.form.confirmPassword
			)

			if (wantsChangePassword) {
				if (!this.form.oldPassword) {
					this.$message.error("请输入旧密码")
					return
				}

				if (!this.form.newPassword || !this.form.confirmPassword) {
					this.$message.error("请完整填写新密码")
					return
				}

				if (this.form.oldPassword !== this.form.password) {
					this.$message.error("旧密码不正确")
					return
				}

				if (this.form.newPassword.length < 6) {
					this.$message.error("新密码长度至少 6 位")
					return
				}

				if (this.form.newPassword !== this.form.confirmPassword) {
					this.$message.error("两次输入的新密码不一致")
					return
				}

				this.form.password = this.form.newPassword
			}

			const profile = {
				username: this.form.username,
				avatar: this.form.avatar,
				bio: this.form.bio
			}

			updateCurrentUser(profile)
				.then(() => {
					const cachedProfile = {
						...JSON.parse(localStorage.getItem("pawhub_user_profile") || "{}"),
						username: this.form.username,
						email: this.form.email,
						avatar: this.form.avatar,
						bio: this.form.bio
					}

					localStorage.setItem("pawhub_user_profile", JSON.stringify(cachedProfile))
					this.$message.success("资料已保存")
					this.$router.push({ name: "mine" })
				})
				.catch(error => {
					const msg = error?.response?.data?.message || error?.message || "资料保存失败"
					this.$message.error(msg)
				})
		},

		goBack() {
			this.$router.back()
		}
	}
}
</script>

<style scoped>
.edit-page{
	min-height:100vh;
	background:linear-gradient(180deg, #f7f2ff 0%, #f5f7fb 42%, #f5f5f7 100%);
	padding:12px 14px 100px;
}

.hero{
	background:linear-gradient(135deg, #8a84c8 0%, #6f8fcd 55%, #86b8c8 100%);
	border-radius:22px;
	padding:14px;
	color:white;
	box-shadow:0 14px 30px rgba(77, 88, 158, 0.22);
	margin-bottom:14px;
}

.topbar{
	display:flex;
	align-items:center;
	justify-content:space-between;
}

.back-btn{
	width:36px;
	height:36px;
	border:none;
	background:rgba(255,255,255,0.2);
	color:white;
	border-radius:10px;
	font-size:26px;
	line-height:1;
}

.title{
	font-size:20px;
	font-weight:700;
}

.spacer{
	width:36px;
}

.hero-info{
	margin-top:18px;
	padding:10px 0 4px;
}

.hero-label{
	font-size:12px;
	opacity:0.85;
}

.hero-email{
	font-size:22px;
	font-weight:700;
	margin-top:6px;
}

.hero-tip{
	margin-top:8px;
	font-size:13px;
	opacity:0.9;
}

.form-card{
	background:white;
	border:1px solid #eceff4;
	border-radius:18px;
	box-shadow:0 8px 20px rgba(15,23,42,0.06);
	padding:16px;
	margin-bottom:12px;
}

.section-title{
	font-size:16px;
	font-weight:700;
	color:#111827;
	margin-bottom:14px;
}

.avatar-panel{
	display:flex;
	gap:14px;
	align-items:center;
	padding:12px;
	background:linear-gradient(135deg, #fbfbff 0%, #f6f8ff 100%);
	border-radius:16px;
	border:1px solid #eef1f8;
	margin-bottom:16px;
}

.avatar{
	width:76px;
	height:76px;
	border-radius:18px;
	object-fit:cover;
	border:3px solid white;
	box-shadow:0 6px 16px rgba(15,23,42,0.12);
}

.avatar-actions{
	flex:1;
}

.avatar-name{
	font-size:15px;
	font-weight:700;
	color:#111827;
}

.avatar-desc{
	margin-top:6px;
	font-size:13px;
	color:#6b7280;
	line-height:1.5;
}

.upload-btn{
	display:inline-flex;
	align-items:center;
	justify-content:center;
	margin-top:10px;
	padding:8px 14px;
	border-radius:999px;
	background:#8a84c8;
	color:white;
	font-size:13px;
	cursor:pointer;
}

.upload-btn input{
	display:none;
}

.field{
	margin-bottom:14px;
}

.field label{
	display:block;
	font-size:13px;
	color:#4b5563;
	margin-bottom:7px;
	font-weight:600;
}

.field input,
.field textarea{
	width:100%;
	border:1px solid #d8dce6;
	background:#fafbff;
	border-radius:14px;
	padding:12px 14px;
	font-size:14px;
	outline:none;
	transition:0.2s;
	box-sizing:border-box;
}

.field input:focus,
.field textarea:focus{
	border-color:#8a84c8;
	background:white;
	box-shadow:0 0 0 4px rgba(138,132,200,0.12);
}

.field input[disabled]{
	color:#6b7280;
	cursor:not-allowed;
	background:#f3f4f6;
}

.password-hint{
	margin-top:6px;
	font-size:12px;
	color:#6b7280;
	background:#f8fafc;
	border-radius:12px;
	padding:10px 12px;
}

.action-bar{
	position:fixed;
	left:0;
	right:0;
	bottom:0;
	background:rgba(255,255,255,0.96);
	backdrop-filter:blur(8px);
	border-top:1px solid #eceff4;
	padding:12px 14px;
	display:flex;
	gap:10px;
	box-sizing:border-box;
}

.cancel-btn,
.save-btn{
	flex:1;
	height:46px;
	border-radius:16px;
	border:none;
	font-size:15px;
	font-weight:700;
}

.cancel-btn{
	background:#eef2f7;
	color:#334155;
}

.save-btn{
	background:linear-gradient(135deg, #8a84c8 0%, #6f8fcd 100%);
	color:white;
	box-shadow:0 10px 20px rgba(138,132,200,0.25);
}
</style>
