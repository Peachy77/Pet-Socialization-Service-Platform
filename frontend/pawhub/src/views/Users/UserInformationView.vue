<template>
	<div class="user-info-page">
		<div class="header">
			<button class="back-btn" @click="goBack">‹</button>
			<div class="title">用户主页</div>
			<span class="spacer"></span>
		</div>

		<div class="profile-card">
			<div class="cover"></div>

			<div class="profile-main">
				<div class="profile-row">
					<img class="avatar" :src="profile.avatar" />

					<div class="user-info">
						<div class="name">{{ profile.username }}</div>
						<div class="bio">{{ profile.bio }}</div>
					</div>

					<div class="profile-actions">
						<button
							class="action-btn action-btn-follow"
							:class="{ following: isFollowing }"
							@click="toggleFollow"
						>
							{{ isFollowing ? '已关注' : '关注' }}
						</button>
						<button class="action-btn action-btn-message" @click="goMessage">
							私信
						</button>
					</div>
				</div>

				<div class="stats">
					<div class="stat">
						<div class="num">{{ profile.followCount }}</div>
						<div class="label">关注</div>
					</div>
					<div class="stat">
						<div class="num">{{ profile.fansCount }}</div>
						<div class="label">粉丝</div>
					</div>
					<div class="stat">
						<div class="num">{{ profile.likesCount }}</div>
						<div class="label">获赞</div>
					</div>
				</div>
			</div>
		</div>

		<div class="section-title">动态</div>

		<div class="post-list">
			<PostCard
				v-for="post in posts"
				:key="post.id"
				:post="post"
			/>
		</div>
	</div>
</template>

<script>
import PostCard from "@/components/PostCard.vue"

export default {
	name: "UserInformationView",

	components: {
		PostCard
	},

	data() {
		return {
			isFollowing: false,
			profile: {
				id: "",
				username: "宠物爱好者",
				avatar: "https://images.unsplash.com/photo-1601758123927-1967a3f7f3b4",
				bio: "热爱生活，爱宠物 🐾",
				followCount: 125,
				fansCount: 568,
				likesCount: 2345
			},
			posts: [
				{
					id: 1,
					avatar: "https://images.unsplash.com/photo-1601758123927-1967a3f7f3b4",
					name: "柴犬小乖",
					time: "2小时前",
					images: [
						"https://images.unsplash.com/photo-1548199973-03cce0bbc87b",
						"https://images.unsplash.com/photo-1558788353-f76d92427f16"
					],
					content: "今天带狗狗去公园散步，太开心啦！",
					tags: ["遛狗", "柴犬"],
					likes: 128,
					comments: 32
				},
				{
					id: 2,
					avatar: "https://images.unsplash.com/photo-1601758123927-1967a3f7f3b4",
					name: "柴犬小乖",
					time: "昨天",
					images: ["https://images.unsplash.com/photo-1517841905240-472988babdf9"],
					content: "给毛孩子做了个新玩具，玩得停不下来。",
					tags: ["手工", "宠物日常"],
					likes: 96,
					comments: 18
				}
			]
		}
	},

	created() {
		this.loadProfile()
	},

	methods: {
		goBack() {
			this.$router.back()
		},

		loadProfile() {
			const query = this.$route.query || {}
			const rawUser = query.user

			let parsedUser = null

			if (rawUser) {
				try {
					parsedUser = JSON.parse(decodeURIComponent(rawUser))
				} catch (error) {
					parsedUser = null
				}
			}

			const user = parsedUser || query

			this.profile = {
				...this.profile,
				id: user.id || this.profile.id,
				username: user.name || user.username || this.profile.username,
				avatar: user.avatar || this.profile.avatar,
				bio: user.bio || this.profile.bio,
				followCount: Number(user.followCount || this.profile.followCount),
				fansCount: Number(user.fansCount || this.profile.fansCount),
				likesCount: Number(user.likesCount || this.profile.likesCount)
			}
		},

		toggleFollow() {
			this.isFollowing = !this.isFollowing
		},

		goMessage() {
			this.$router.push({
				name: "messagesDetails",
				query: {
					username: this.profile.username,
					avatar: this.profile.avatar,
					type: "private"
				}
			})
		}
	}
}
</script>

<style scoped>
.user-info-page{
	min-height:100vh;
	background:#f5f5f7;
	padding-bottom:24px;
}

.header{
	display:flex;
	align-items:center;
	justify-content:space-between;
	padding:14px 16px;
	background:white;
	border-bottom:1px solid #eee;
}

.back-btn{
	width:36px;
	height:36px;
	border:none;
	background:#f5f6fb;
	border-radius:12px;
	font-size:26px;
	line-height:1;
	color:#222;
}

.title{
	font-size:18px;
	font-weight:700;
	color:#111827;
}

.spacer{
	width:36px;
}

.profile-card{
	background:white;
	overflow:hidden;
}

.cover{
	height:120px;
	background:linear-gradient(120deg, #b385c8 0%, #899dce 45%, #eafbfd 100%);
}

.profile-main{
	padding:0 16px 18px;
}

.profile-row{
	display:flex;
	align-items:flex-start;
	gap:12px;
	margin-top:-36px;
}

.avatar{
	width:76px;
	height:76px;
	border-radius:50%;
	border:4px solid white;
	object-fit:cover;
	background:white;
	flex-shrink:0;
}

.user-info{
	flex:1;
	min-width:0;
	padding-top:34px;
	text-align:left;
}

.name{
	font-size:18px;
	font-weight:700;
	line-height:1.3;
	color:#111827;
}

.bio{
	margin-top:4px;
	color:#6b7280;
	font-size:14px;
	line-height:1.5;
}

.profile-actions{
	display:flex;
	gap:8px;
	margin-left:auto;
	padding-top:34px;
	flex-shrink:0;
}

.action-btn{
	min-width:72px;
	height:34px;
	padding:0 14px;
	border-radius:18px;
	font-size:13px;
	border:1px solid transparent;
}

.action-btn-follow{
	background:#8a84c8;
	color:white;
}

.action-btn-follow.following{
	background:#eef2ff;
	color:#6b7280;
}

.action-btn-message{
	background:white;
	color:#8a84c8;
	border-color:#c9c6ef;
}

.stats{
	display:flex;
	gap:34px;
	padding:18px 0 0;
}

.stat{
	text-align:center;
	cursor:default;
}

.stat .num,
.stat .label{
	cursor:inherit;
}

.num{
	font-size:17px;
	font-weight:700;
	color:#111827;
}

.label{
	margin-top:4px;
	font-size:12px;
	color:#6b7280;
}

.intro-card{
	background:white;
	margin-top:10px;
	padding:16px;
}

.intro-title,
.section-title{
	font-size:16px;
	font-weight:700;
	color:#111827;
}

.intro-text{
	margin-top:8px;
	color:#4b5563;
	font-size:14px;
	line-height:1.6;
}

.section-title{
	padding:16px 16px 10px;
}

.post-list{
	padding:0 16px;
}
</style>
