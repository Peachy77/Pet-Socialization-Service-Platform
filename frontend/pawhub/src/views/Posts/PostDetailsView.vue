<!-- <template>
	<div class="post-detail-page">
		<div class="top-bar">
			<button class="back-btn" @click="goBack">‹</button>
			<div class="top-title">动态详情</div>
			<div class="top-spacer"></div>
		</div>

		<div class="content-shell">
			<div class="author-card">
				<div
					class="author-left"
					role="button"
					tabindex="0"
					@click="openAuthorProfile"
					@keydown.enter.prevent="openAuthorProfile"
				>
					<img :src="post.avatar" class="avatar" />
					<div class="author-meta">
						<div class="author-name">{{ post.name }}</div>
						<div class="author-time">{{ post.time }}</div>
					</div>
				</div>
				<button class="follow-btn" :class="{ active: followed }" @click="toggleFollow">
					{{ followed ? "已关注" : "关注" }}
				</button>
			</div>

			<div
				v-if="post.images.length"
				class="gallery"
			>
				<div
					ref="galleryTrack"
					class="gallery-track"
					@scroll="handleGalleryScroll"
				>
					<div
					v-for="(image, index) in post.images"
					:key="index"
					class="gallery-item"
				>
						<img :src="image" alt="动态图片" />
					</div>
				</div>

				<div v-if="post.images.length > 1" class="gallery-dots">
					<span
						v-for="(_, index) in post.images"
						:key="`dot-${index}`"
						:class="['dot', { active: index === activeImageIndex }]"
					></span>
				</div>
			</div>

			<div class="post-body">
				<p class="lead">{{ post.content }}</p>

				<div class="tag-list">
					<span v-for="tag in post.tags" :key="tag" class="tag">#{{ tag }}</span>
				</div>

				<div class="action-row">
					<div class="stat-item like-btn" :class="{ liked }" @click="toggleLike">
						<svg viewBox="0 0 24 24" class="heart-icon" aria-hidden="true">
							<path
								d="M12 21s-7.2-4.7-9.6-9C.6 8.7 2.1 5.2 5.6 5c2.1-.1 3.4 1 4.4 2.2C11 6 12.3 4.9 14.4 5c3.5.2 5 3.7 3.2 7-2.4 4.3-9.6 9-9.6 9z"
							/>
						</svg>
						<span>{{ displayLikes }}</span>
					</div>
					<div class="stat-item">
						<span class="icon">💬</span>
						<span>{{ displayCommentCount }}</span>
					</div>
				</div>
			</div>

			<PostCommentList
				:comments="commentList"
				:current-user-avatar="currentUser.avatar"
				@like-comment="handleLikeComment"
				@reply-comment="handleReplyComment"
			/>
		</div>

		<div class="comment-bar">
			<input
<<<<<<< HEAD
				v-model="commentInput"
				type="text"
				class="input-box"
				placeholder="写评论..."
				@keyup.enter="submitComment"
			/>
			<button class="send-btn">➤</button>
=======
				ref="commentImageInput"
				type="file"
				accept="image/*"
				class="image-input"
				@change="handleCommentImageChange"
			/>
			<button class="attach-btn" @click="triggerCommentImage">＋</button>
			<textarea
				v-model.trim="commentDraft"
				class="input-box"
				placeholder="写评论..."
				rows="1"
			/>
			<button class="send-btn" @click="submitComment">➤</button>
		</div>

		<div v-if="commentImage" class="comment-image-preview">
			<img :src="commentImage" alt="待发布图片" />
			<button class="remove-image-btn" @click="clearCommentImage">×</button>
>>>>>>> 3c551e171800da51349e9c969fc119aae6263936
		</div>
	</div>
</template>  -->

<template>
	<div class="post-detail-page">
		<div class="top-bar">
			<button class="back-btn" @click="goBack">‹</button>
			<div class="top-title">动态详情</div>
			<div class="top-spacer"></div>
		</div>

		<!-- 加载中 -->
		<div v-if="loading" class="loading-container">
			<div class="loading-spinner">加载中...</div>
		</div>

		<!-- 有数据 -->
		<div v-else-if="post" class="content-shell">
			<div class="author-card">
				<div class="author-left">
					<img :src="post.avatar" class="avatar" />
					<div class="author-meta">
						<div class="author-name">{{ post.name }}</div>
						<div class="author-time">{{ post.time }}</div>
					</div>
				</div>
				<button class="follow-btn">关注</button>
			</div>

			<div v-if="post.images && post.images.length" class="gallery">
				<div ref="galleryTrack" class="gallery-track" @scroll="handleGalleryScroll">
					<div v-for="(image, index) in post.images" :key="index" class="gallery-item">
						<img :src="image" alt="动态图片" />
					</div>
				</div>
				<div v-if="post.images.length > 1" class="gallery-dots">
					<span v-for="(_, index) in post.images" :key="`dot-${index}`" :class="['dot', { active: index === activeImageIndex }]"></span>
				</div>
			</div>

			<div class="post-body">
				<p class="lead">{{ post.content }}</p>
				<div class="tag-list">
					<span v-for="tag in post.tags" :key="tag" class="tag">#{{ tag }}</span>
				</div>
				<div class="action-row">
					<div class="stat-item like-btn" :class="{ liked: liked }" @click="toggleLike">
						<svg viewBox="0 0 24 24" class="heart-icon">
							<path d="M12 21s-7.2-4.7-9.6-9C.6 8.7 2.1 5.2 5.6 5c2.1-.1 3.4 1 4.4 2.2C11 6 12.3 4.9 14.4 5c3.5.2 5 3.7 3.2 7-2.4 4.3-9.6 9-9.6 9z"/>
						</svg>
						<span>{{ post.likes }}</span>
					</div>
					<div class="stat-item">
						<span class="icon">💬</span>
						<span>{{ post.comments }}</span>
					</div>
				</div>
			</div>

			<PostCommentList :comments="commentList" @like-comment="handleLikeComment" @reply-comment="handleReplyComment" />
		</div>

		<!-- 无数据 -->
		<div v-else class="error-container">
			<p>动态不存在</p>
			<button @click="goBack" class="back-home-btn">返回首页</button>
		</div>

		<!-- 评论栏（只在有数据时显示） -->
		<div v-if="post" class="comment-bar">
			<input 
				v-model="commentInput" 
				type="text" 
				class="input-box" 
				placeholder="写评论..."
				@keyup.enter="submitComment"
			/>
			<button class="send-btn" @click="submitComment">➤</button>
		</div>
	</div>
</template>

<script>
import PostCommentList from "@/components/PostCommentList.vue"
import { getPostDetail, likePost, unlikePost, getComments, createComment } from "@/api/posts"

export default {
	name: "PostDetailsView",

	components: {
		PostCommentList
	},

	data() {
		return {
			liked: false,
			followed: false,
			activeImageIndex: 0,
<<<<<<< HEAD
			// fallbackPost: {
			// 	id: 1,
			// 	name: "柴犬小乖",
			// 	time: "2小时前",
			// 	avatar: "https://i.pravatar.cc/100?img=3",
			// 	images: [
			// 		"https://images.unsplash.com/photo-1583511655857-d19b40a7a54e",
			// 		"https://images.unsplash.com/photo-1558788353-f76d92427f16"
			// 	],
			// 	content:
			// 		"今天带我家毛孩子去美容院做了个新造型，超级可爱！推荐大家去试试这家店，服务态度很好，技术也专业。",
			// 	tags: ["宠物美容", "柴犬", "日常分享"],
			// 	likes: 234,
			// 	comments: 45
			// },
			// commentList: [
			// 	{
			// 		id: 1,
			// 		name: "猫咪铲屎官",
			// 		time: "1小时前",
			// 		avatar: "https://i.pravatar.cc/100?img=5",
			// 		content: "好可爱！我家猫咪也想去试试。",
			// 		likes: 12,
			// 		replyCount: 2,
			// 		lastReply: "",
			// 		liked: false
			// 	},
			// 	{
			// 		id: 2,
			// 		name: "小鱼",
			// 		time: "刚刚",
			// 		avatar: "https://i.pravatar.cc/100?img=12",
			// 		content: "这家店看起来很不错，收藏了。",
			// 		likes: 6,
			// 		replyCount: 1,
			// 		lastReply: "",
			// 		liked: false
			// 	}
			// ]
			loading: true,
			post: null,
			commentList: [],
			commentInput: ""
=======
			commentDraft: "",
			commentImage: "",
			extraCommentCount: 0,
			currentUser: {
				username: "宠物爱好者",
				avatar: "https://images.unsplash.com/photo-1601758123927-1967a3f7f3b4"
			},
			fallbackPost: {
				id: 1,
				name: "柴犬小乖",
				time: "2小时前",
				avatar: "https://i.pravatar.cc/100?img=3",
				images: [
					"https://images.unsplash.com/photo-1583511655857-d19b40a7a54e",
					"https://images.unsplash.com/photo-1558788353-f76d92427f16"
				],
				content:
					"今天带我家毛孩子去美容院做了个新造型，超级可爱！推荐大家去试试这家店，服务态度很好，技术也专业。",
				tags: ["宠物美容", "柴犬", "日常分享"],
				likes: 234,
				comments: 45
			},
			commentList: [
				{
					id: 1,
					name: "猫咪铲屎官",
					time: "1小时前",
					avatar: "https://i.pravatar.cc/100?img=5",
					content: "好可爱！我家猫咪也想去试试。",
					likes: 12,
					replyCount: 2,
					replies: [],
					lastReply: "",
					liked: false
				},
				{
					id: 2,
					name: "小鱼",
					time: "刚刚",
					avatar: "https://i.pravatar.cc/100?img=12",
					content: "这家店看起来很不错，收藏了。",
					likes: 6,
					replyCount: 1,
					replies: [],
					lastReply: "",
					liked: false
				}
			]
>>>>>>> 3c551e171800da51349e9c969fc119aae6263936
		}
	},

	created() {
		this.loadCurrentUser()
	},

	computed: {
		// post() {
		// 	const query = this.$route.query || {}

		// 	return {
		// 		...this.fallbackPost,
		// 		id: query.id ? Number(query.id) || this.fallbackPost.id : this.fallbackPost.id,
		// 		name: query.name || this.fallbackPost.name,
		// 		time: query.time || this.fallbackPost.time,
		// 		avatar: query.avatar || this.fallbackPost.avatar,
		// 		content: query.content || this.fallbackPost.content,
		// 		images: this.normalizeArray(query.images, this.fallbackPost.images),
		// 		tags: this.normalizeArray(query.tags, this.fallbackPost.tags),
		// 		likes: this.normalizeNumber(query.likes, this.fallbackPost.likes),
		// 		comments: this.normalizeNumber(query.comments, this.fallbackPost.comments)
		// 	}
		// },

		displayLikes() {
			return (this.post.likes || 0) + (this.liked ? 1 : 0)
		},

		displayCommentCount() {
			return (this.post.comments || 0) + this.extraCommentCount
		},

	},
	async mounted() {
		const postId = this.$route.params.id || this.$route.query.id
		if (!postId) return

		try {
			// 获取动态详情
			const res = await getPostDetail(postId)
			if (res.code === 1 || res.code === 0) {
				const data = res.data
				this.post = {
					id: data.post_id,
					name: data.username,
					time: this.formatTime(data.create_time),
					avatar: data.avatar || 'default.jpg',
					images: this.parseJson(data.images),
					content: data.content,
					tags: this.parseJson(data.tags),
					likes: data.like_count || 0,
					comments: data.comment_count || 0
				}
				this.liked = data.is_liked || false
			}

			// 获取评论列表
			const commentRes = await getComments(postId)
			if (commentRes.code === 1 || commentRes.code === 0) {
				this.commentList = (commentRes.data?.list || []).map(c => ({
					id: c.comment_id,
					name: c.username,
					time: this.formatTime(c.create_time),
					avatar: c.avatar || 'default.jpg',
					content: c.content,
					likes: 0,
					replyCount: 0,
					liked: false
				}))
			}
		} catch (error) {
			console.error('加载失败:', error)
		} finally {
			this.loading = false
		}
	},

	methods: {
		async toggleLike() {
			if (!this.post) return
			try {
				if (this.liked) {
					await unlikePost(this.post.id)
					this.post.likes -= 1
				} else {
					await likePost(this.post.id)
					this.post.likes += 1
				}
				this.liked = !this.liked
			} catch (error) {
				console.error('点赞失败:', error)
			}
		},

		toggleFollow() {
			this.followed = !this.followed
		},

		loadCurrentUser() {
			const stored = JSON.parse(localStorage.getItem("pawhub_user_profile") || "null")

			if (!stored) {
				return
			}

			this.currentUser = {
				username: stored.username || stored.name || this.currentUser.username,
				avatar: stored.avatar || this.currentUser.avatar
			}
		},

		submitComment() {
			const text = String(this.commentDraft || "").trim()

			if (!text && !this.commentImage) {
				return
			}

			this.commentList = [{
				id: Date.now(),
				name: this.currentUser.username,
				time: this.formatNow(),
				avatar: this.currentUser.avatar,
				content: text,
				image: this.commentImage,
				likes: 0,
				replyCount: 0,
				replies: [],
				lastReply: "",
				liked: false,
				isMine: true
			}, ...this.commentList]

			this.extraCommentCount += 1
			this.commentDraft = ""
			this.clearCommentImage()
		},

		triggerCommentImage() {
			if (this.$refs.commentImageInput) {
				this.$refs.commentImageInput.click()
			}
		},

		handleCommentImageChange(event) {
			const file = event.target.files && event.target.files[0]

			if (!file) {
				return
			}

			const reader = new FileReader()
			reader.onload = () => {
				this.commentImage = reader.result
			}
			reader.readAsDataURL(file)
		},

		clearCommentImage() {
			this.commentImage = ""
			if (this.$refs.commentImageInput) {
				this.$refs.commentImageInput.value = ""
			}
		},

		handleLikeComment(commentId) {
			this.commentList = this.commentList.map(c => {
				if (c.id !== commentId) return c
				return { ...c, liked: !c.liked, likes: c.liked ? c.likes - 1 : c.likes + 1 }
			})
		},

		handleReplyComment(payload) {
<<<<<<< HEAD
			this.commentList = this.commentList.map(c => {
				if (c.id !== payload.id) return c
				return { ...c, replyCount: c.replyCount + 1, lastReply: payload.text }
			})
		},

		async submitComment() {
	if (!this.commentInput.trim()) return
	if (!this.post) return

	try {
		const res = await createComment(this.post.id, {
			content: this.commentInput,
			images: []
		})
		console.log('发表评论响应:', res)

		if (res.code === 1) {
			this.commentInput = ""

			// 重新加载评论列表
			const commentRes = await getComments(this.post.id, { page: 1, pageSize: 20 })
			if (commentRes.code === 1) {
				const list = commentRes.data?.list || []
				this.commentList = list.map(c => ({
					id: c.comment_id,
					name: c.username,
					time: this.formatTime(c.create_time),
					avatar: c.avatar || 'default.jpg',
					content: c.content,
					likes: 0,
					replyCount: 0,
					liked: false
				}))
			}
			this.post.comments += 1
		}
	} catch (error) {
		console.error('发表评论失败:', error)
	}
},
=======
			this.commentList = this.commentList.map(comment => {
				if (comment.id !== payload.id) {
					return comment
				}

				const nextReplies = Array.isArray(comment.replies)
					? [...comment.replies]
					: []

				nextReplies.push({
					text: payload.text,
					image: payload.image || "",
					targetName: payload.targetName || comment.name,
					avatar: this.currentUser.avatar,
					time: this.formatNow()
				})

				return {
					...comment,
					replyCount: (comment.replyCount || 0) + 1,
					replies: nextReplies,
					lastReply: payload.text || (payload.image ? "[图片]" : "")
				}
			})
		},

		formatNow() {
			const now = new Date()
			const pad = value => String(value).padStart(2, "0")
			return `${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}`
		},
>>>>>>> 3c551e171800da51349e9c969fc119aae6263936

		handleGalleryScroll(event) {
			const container = event.target
			const width = container.clientWidth
			if (width) this.activeImageIndex = Math.round(container.scrollLeft / width)
		},

		parseJson(value) {
			if (!value) return []
			if (Array.isArray(value)) return value
			try { return JSON.parse(value) } catch { return [] }
		},

		formatTime(dateTime) {
			if (!dateTime) return '刚刚'
			const date = new Date(dateTime)
			const now = new Date()
			const diff = now - date
			if (diff < 60000) return '刚刚'
			if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
			if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
			return `${Math.floor(diff / 86400000)}天前`
		},

		openAuthorProfile() {
			const user = {
				id: this.post.id,
				name: this.post.name,
				avatar: this.post.avatar
			}

			this.$router.push({
				name: "userInformation",
				query: {
					user: encodeURIComponent(JSON.stringify(user))
				}
			})
		},

		goBack() {
			window.history.length > 1 ? this.$router.back() : this.$router.push('/home')
		},

		normalizeArray(value, fallback) { return value || fallback },
		normalizeNumber(value, fallback) { return Number(value) || fallback }
	}

	// methods: {
	// 	toggleLike() {
	// 		this.liked = !this.liked
	// 	},

	// 	handleLikeComment(commentId) {
	// 		this.commentList = this.commentList.map(comment => {
	// 			if (comment.id !== commentId) {
	// 				return comment
	// 			}

	// 			const nextLiked = !comment.liked
	// 			const nextLikes = nextLiked
	// 				? (comment.likes || 0) + 1
	// 				: Math.max((comment.likes || 0) - 1, 0)

	// 			return {
	// 				...comment,
	// 				liked: nextLiked,
	// 				likes: nextLikes
	// 			}
	// 		})
	// 	},

	// 	handleReplyComment(payload) {
	// 		this.commentList = this.commentList.map(comment => {
	// 			if (comment.id !== payload.id) {
	// 				return comment
	// 			}

	// 			return {
	// 				...comment,
	// 				replyCount: (comment.replyCount || 0) + 1,
	// 				lastReply: payload.text
	// 			}
	// 		})
	// 	},

	// 	handleGalleryScroll(event) {
	// 		const container = event.target
	// 		const width = container.clientWidth

	// 		if (!width) {
	// 			return
	// 		}

	// 		this.activeImageIndex = Math.round(container.scrollLeft / width)
	// 	},

	// 	goBack() {
	// 		if (window.history.length > 1) {
	// 			this.$router.back()
	// 			return
	// 		}

	// 		this.$router.push({ path: "/home" })
	// 	},

	// 	normalizeArray(value, fallback) {
	// 		if (!value) {
	// 			return Array.isArray(fallback) ? fallback : []
	// 		}

	// 		if (Array.isArray(value)) {
	// 			return value
	// 		}

	// 		if (typeof value === "string") {
	// 			try {
	// 				const parsed = JSON.parse(value)

	// 				if (Array.isArray(parsed)) {
	// 					return parsed
	// 				}
	// 			} catch (error) {
	// 				return value.split(",").map(item => item.trim()).filter(Boolean)
	// 			}
	// 		}

	// 		return Array.isArray(fallback) ? fallback : []
	// 	},

	// 	normalizeNumber(value, fallback) {
	// 		const number = Number(value)
	// 		return Number.isFinite(number) ? number : fallback
	// 	}
	// }
}
</script>

<style scoped>
.post-detail-page {
	min-height: 100vh;
	background: #f6f6fa;
	padding-bottom: 96px;
}

.top-bar {
	height: 64px;
	background: #fff;
	display: flex;
	align-items: center;
	justify-content: space-between;
	padding: 0 14px;
	box-shadow: 0 1px 10px rgba(23, 24, 38, 0.06);
	position: sticky;
	top: 0;
	z-index: 10;
}

.back-btn,
.send-btn {
	border: none;
	background: transparent;
	padding: 0;
	cursor: pointer;
}

.back-btn {
	width: 34px;
	height: 34px;
	font-size: 30px;
	line-height: 34px;
	color: #222;
	text-align: left;
}

.top-title {
	font-size: 18px;
	font-weight: 600;
	color: #222;
	letter-spacing: 1px;
}

.top-spacer {
	width: 34px;
}

.content-shell {
	max-width: 760px;
	margin: 0 auto;
	padding: 14px 18px 0;
}

.author-card {
	display: flex;
	align-items: center;
	justify-content: space-between;
	gap: 12px;
	background: #fff;
	border-radius: 18px 18px 0 0;
	padding: 16px 18px 10px;
}

.author-left {
	display: flex;
	align-items: center;
	gap: 12px;
	min-width: 0;
	cursor: pointer;
}

.author-meta {
	min-width: 0;
}

.avatar {
	width: 56px;
	height: 56px;
	border-radius: 50%;
	object-fit: cover;
	box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
}

.author-name {
	font-size: 18px;
	font-weight: 600;
	color: #3d2f23;
}

.author-time {
	margin-top: 4px;
	font-size: 14px;
	color: #9a8e95;
}

.follow-btn {
	height: 34px;
	padding: 0 16px;
	border: none;
	border-radius: 999px;
	background: #8673d6;
	color: #fff;
	font-size: 14px;
	font-weight: 500;
	cursor: pointer;
	flex: none;
}

.follow-btn.active {
	background: #ece7ee;
	color: #5f5468;
}

.gallery {
	background: #fff;
	padding: 0 18px 14px;
}

.gallery-track {
	display: flex;
	overflow-x: auto;
	scroll-snap-type: x mandatory;
	-webkit-overflow-scrolling: touch;
	scrollbar-width: none;
}

.gallery-track::-webkit-scrollbar {
	display: none;
}

.gallery-item {
	flex: 0 0 100%;
	scroll-snap-align: start;
	overflow: hidden;
	border-radius: 10px;
	background: #f2f2f2;
}

.gallery-item img {
	display: block;
	width: 100%;
	aspect-ratio: 16 / 10;
	object-fit: cover;
}

.gallery-dots {
	display: flex;
	justify-content: center;
	align-items: center;
	gap: 6px;
	margin-top: 10px;
}

.dot {
	width: 6px;
	height: 6px;
	border-radius: 50%;
	background: #d4cfdd;
	transition: all 0.2s ease;
}

.dot.active {
	width: 16px;
	border-radius: 999px;
	background: #9f90c8;
}

.post-body {
	background: #fff;
	padding: 0 18px 16px;
}

.post-body {
	padding-top: 4px;
}

.lead,
.description {
	margin: 0;
	font-size: 16px;
	line-height: 1.9;
	color: #3f3232;
	text-align: justify;
}

.lead {
	padding: 8px 0 12px;
}

.tag-list {
	display: flex;
	flex-wrap: wrap;
	gap: 10px;
	margin-top: 14px;
}

.tag {
	display: inline-flex;
	align-items: center;
	padding: 7px 14px;
	border-radius: 999px;
	background: #f4f0ff;
	color: #b1a5d6;
	font-size: 13px;
}

.action-row {
	display: flex;
	align-items: center;
	gap: 22px;
	padding: 16px 0 8px;
	color: #4f5562;
	border-bottom: 1px solid #ece7ee;
}

.stat-item {
	display: flex;
	align-items: center;
	gap: 8px;
	font-size: 16px;
}

.like-btn {
	transition: color 0.2s ease;
}

.heart-icon {
	width: 20px;
	height: 20px;
	flex: none;
}

.heart-icon path {
	fill: transparent;
	stroke: #4f5562;
	stroke-width: 1.8;
	transition: 0.2s;
}

.like-btn.liked {
	color: #ef4444;
}

.like-btn.liked .heart-icon path {
	fill: #ef4444;
	stroke: #ef4444;
}

.icon {
	font-size: 22px;
	line-height: 1;
}

.comment-bar {
	position: fixed;
	left: 0;
	right: 0;
	bottom: 0;
	z-index: 20;
	display: flex;
	align-items: center;
	gap: 12px;
	padding: 12px 18px calc(12px + env(safe-area-inset-bottom));
	background: rgba(246, 246, 250, 0.94);
	backdrop-filter: blur(14px);
}

.image-input {
	display: none;
}

.attach-btn {
	width: 46px;
	height: 46px;
	border: none;
	border-radius: 50%;
	background: #fff;
	color: #8d79d2;
	font-size: 28px;
	line-height: 1;
	box-shadow: 0 8px 20px rgba(43, 35, 55, 0.1);
	cursor: pointer;
}

.input-box {
	flex: 1;
	height: 56px;
	padding: 16px 20px;
	border-radius: 28px;
	background: #fff;
	color: #b3acb8;
	font-size: 16px;
	box-shadow: 0 8px 24px rgba(43, 35, 55, 0.06);
	border: none;
	outline: none;
	resize: none;
	line-height: 1.4;
	font-family: inherit;
}

.send-btn {
	width: 52px;
	height: 52px;
	border-radius: 50%;
	background: #885dce;
	color: #fff;
	font-size: 22px;
	box-shadow: 0 10px 24px rgba(200, 184, 238, 0.45);
}

.comment-image-preview {
	position: fixed;
	right: 18px;
	bottom: calc(74px + env(safe-area-inset-bottom));
	z-index: 21;
	background: #fff;
	border-radius: 12px;
	padding: 8px;
	box-shadow: 0 8px 24px rgba(43, 35, 55, 0.14);
}

.comment-image-preview img {
	width: 88px;
	height: 88px;
	object-fit: cover;
	border-radius: 8px;
	display: block;
}

.remove-image-btn {
	position: absolute;
	right: -8px;
	top: -8px;
	width: 22px;
	height: 22px;
	border: none;
	border-radius: 50%;
	background: #8d79d2;
	color: #fff;
	font-size: 14px;
	line-height: 1;
	cursor: pointer;
}

@media (max-width: 640px) {
	.content-shell {
		padding-left: 12px;
		padding-right: 12px;
	}

	.author-card,
	.gallery,
	.post-body {
		padding-left: 14px;
		padding-right: 14px;
	}

	.lead,
	.description {
		font-size: 15px;
	}

	.top-title {
		font-size: 17px;
	}

	.loading-container, .error-container {
	display: flex;
	justify-content: center;
	align-items: center;
	height: 50vh;
	flex-direction: column;
}

.loading-spinner {
	font-size: 16px;
	color: #8673d6;
}

.error-container p {
	color: #999;
	margin-bottom: 16px;
}

.back-home-btn {
	padding: 10px 24px;
	background: #8673d6;
	color: white;
	border: none;
	border-radius: 20px;
	cursor: pointer;
}
}
</style>
