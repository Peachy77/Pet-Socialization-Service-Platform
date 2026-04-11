<template>
	<div class="setting-page">
		<div class="hero">
			<div class="topbar">
				<button class="back-btn" @click="goBack">‹</button>
				<div class="title">设置</div>
				<span class="spacer"></span>
			</div>

			<div class="subtitle">账号与应用信息</div>
		</div>

		<div class="menu-card">
			<div class="menu-item" v-for="item in menuItems" :key="item.title">
				<div class="left">
					<span class="dot"></span>
					<span class="label">{{ item.title }}</span>
				</div>
				<span class="right">></span>
			</div>
		</div>

		<div class="logout-wrap">
			<button class="logout-btn" @click="handleLogout">退出登录</button>
		</div>
	</div>
</template>

<script>
export default {
	name: "SettingView",

	data() {
		return {
			menuItems: [
				{ title: "隐私政策" },
				{ title: "个人信息收集清单" },
				{ title: "第三方信息数据共享" },
				{ title: "关于我们" }
			]
		}
	},

	methods: {
		goBack() {
			this.$router.back()
		},

		handleLogout() {
			const confirmed = window.confirm("是否确定退出登录？")
			if (!confirmed) return

			localStorage.removeItem("token")
			this.$message.success("已退出登录")
			this.$router.replace({ name: "login" })
		}
	}
}
</script>

<style scoped>
.setting-page{
	min-height:100vh;
	background:linear-gradient(180deg, #f7f2ff 0%, #f4f7fb 38%, #f5f5f7 100%);
	padding:12px 14px 40px;
}

.hero{
	background:linear-gradient(135deg, #8a84c8 0%, #6f8fcd 55%, #86b8c8 100%);
	border-radius:20px;
	padding:14px;
	color:white;
	box-shadow:0 14px 30px rgba(77,88,158,0.22);
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

.subtitle{
	margin-top:14px;
	font-size:14px;
	opacity:0.9;
}

.menu-card{
	background:white;
	border:1px solid #eceff4;
	border-radius:18px;
	box-shadow:0 8px 20px rgba(15,23,42,0.06);
	overflow:hidden;
}

.menu-item{
	display:flex;
	align-items:center;
	justify-content:space-between;
	padding:14px 16px;
	border-bottom:1px solid #f1f5f9;
}

.menu-item:last-child{
	border-bottom:none;
}

.left{
	display:flex;
	align-items:center;
	gap:10px;
}

.dot{
	width:8px;
	height:8px;
	border-radius:50%;
	background:#8a84c8;
}

.label{
	font-size:15px;
	color:#111827;
	font-weight:500;
}

.right{
	font-size:12px;
	color:#9aa1af;
}

.logout-wrap{
	margin-top:18px;
}

.logout-btn{
	width:100%;
	height:48px;
	border:none;
	border-radius:16px;
	background:linear-gradient(135deg, #9a2ce3 0%, #66deff 100%);
	color:white;
	font-size:16px;
	font-weight:700;
	box-shadow:0 12px 24px rgba(255,107,129,0.28);
}
</style>
