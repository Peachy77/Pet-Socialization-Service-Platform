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
				<button
					v-if="!isOwnAuthor"
					class="follow-btn"
					:class="{ active: followed }"
					@click="toggleFollow"
				>
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
				v-model="commentInput"
				type="text"
				class="input-box"
				placeholder="写评论..."
				@keyup.enter="submitComment"
			/>
			<button class="send-btn">➤</button>
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
				<button v-if="!isOwnAuthor" class="follow-btn" :class="{ active: followed }" @click="toggleFollow">
					{{ followed ? "已关注" : "关注" }}
				</button>
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

			<PostCommentList
				:comments="commentList"
				:current-user-id="currentUser.id"
				:current-user-name="currentUser.username"
				@like-comment="handleLikeComment"
				@like-reply="handleLikeReply"
				@reply-comment="handleReplyComment"
				@delete-comment="handleDeleteComment"
			/>
		</div>

		<!-- 无数据 -->
		<div v-else class="error-container">
			<p>动态不存在</p>
			<button @click="goBack" class="back-home-btn">返回首页</button>
		</div>

		<!-- 评论栏（只在有数据时显示） -->
		<div v-if="post" class="comment-bar">
			<input 
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
		</div>
	</div>
</template>

<script>
import PostCommentList from "@/components/PostCommentList.vue"
import { getPostDetail, likePost, unlikePost, getComments, createComment, deleteComment, likeComment, unlikeComment, replyComment } from "@/api/posts"
import { uploadFile } from "@/api/upload"
import { followUser, getUser, unfollowUser } from "@/api/users"

export default {
	name: "PostDetailsView",

	components: {
		PostCommentList
	},

	data() {
		return {
			liked: false,
			followed: false,
			followLoading: false,
			activeImageIndex: 0,
			commentDraft: "",
			commentImage: "",
			commentImageFile: null,
			extraCommentCount: 0,
			currentUser: {
				id: "",
				username: "宠物爱好者",
				avatar: "https://images.unsplash.com/photo-1601758123927-1967a3f7f3b4"
			},
			loading: true,
			post: null,
			commentList: []
		}
	},

	created() {
		this.loadCurrentUser()
	},

	computed: {
		isOwnAuthor() {
			const currentUserId = String(localStorage.getItem("userId") || this.currentUser.id || "")
			const authorId = String(this.post?.userId ?? this.post?.user_id ?? this.post?.authorId ?? this.post?.author_id ?? "")
			return Boolean(currentUserId && authorId && currentUserId === authorId)
		},

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
			console.log("[PostDetailsView] getPostDetail 原始响应:", res)
			console.table(res)
			
			if (res.code === 1 || res.code === 0) {
				const data = res.data
				console.log("[PostDetailsView] 帖子数据详情:", data)
				console.table(data)
				
				const postImages = this.parseJson(data.images).map(item => this.toDisplayImageUrl(item))
				this.post = {
					id: data.post_id,
					userId: data.user_id,
					name: data.username,
					time: this.formatTime(data.create_time),
					avatar: this.toDisplayImageUrl(data.avatar) || 'default.jpg',
					images: postImages,
					content: data.content,
					tags: this.parseJson(data.tags),
					likes: data.like_count || 0,
					comments: data.comment_count || 0
				}
				console.log("[PostDetailsView] 处理后的 post 对象:", this.post)
				this.liked = data.is_liked || false

				if (this.post.userId) {
					try {
						const userRes = await getUser(this.post.userId)
						const userData = this.unwrapPayload(userRes)
						this.followed = this.toBooleanLikeFlag(userData?.isFollowing ?? userData?.following ?? userData?.is_following)
					} catch (error) {
						console.error("[PostDetailsView] 获取作者关注状态失败:", error)
					}
				}
			}

			// 获取评论列表
			const commentRes = await getComments(postId)
			if (commentRes.code === 1 || commentRes.code === 0) {
				this.commentList = (commentRes.data?.list || []).map(c => this.normalizeCommentItem(c))
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

		async toggleFollow() {
			const targetUserId = this.post?.userId ?? this.post?.user_id ?? this.post?.authorId ?? this.post?.author_id
			if (!targetUserId || this.followLoading) return

			const previousFollowed = this.followed
			this.followed = !previousFollowed
			this.followLoading = true

			try {
				const response = previousFollowed
					? await unfollowUser(targetUserId)
					: await followUser(targetUserId)
				this.unwrapPayload(response)
			} catch (error) {
				this.followed = previousFollowed
				const msg = error?.response?.data?.message || error?.message || (previousFollowed ? "取消关注失败" : "关注失败")
				this.$message.error(msg)
			} finally {
				this.followLoading = false
			}
		},

		loadCurrentUser() {
			const stored = JSON.parse(localStorage.getItem("pawhub_user_profile") || "null")

			if (!stored) {
				return
			}

			this.currentUser = {
				id: String(localStorage.getItem("userId") || stored.id || stored.userId || ""),
				username: stored.username || stored.name || this.currentUser.username,
				avatar: stored.avatar || this.currentUser.avatar
			}
		},

		// submitComment() {
		// 	const text = String(this.commentDraft || "").trim()

		// 	if (!text && !this.commentImage) {
		// 		return
		// 	}

		// 	this.commentList = [{
		// 		id: Date.now(),
		// 		name: this.currentUser.username,
		// 		time: this.formatNow(),
		// 		avatar: this.currentUser.avatar,
		// 		content: text,
		// 		image: this.commentImage,
		// 		likes: 0,
		// 		replyCount: 0,
		// 		replies: [],
		// 		lastReply: "",
		// 		liked: false,
		// 		isMine: true
		// 	}, ...this.commentList]

		// 	this.extraCommentCount += 1
		// 	this.commentDraft = ""
		// 	this.clearCommentImage()
		// },

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

			this.commentImageFile = file
			this.commentImage = URL.createObjectURL(file)
		},

		clearCommentImage() {
			if (this.commentImage && this.commentImage.startsWith("blob:")) {
				URL.revokeObjectURL(this.commentImage)
			}
			this.commentImage = ""
			this.commentImageFile = null
			if (this.$refs.commentImageInput) {
				this.$refs.commentImageInput.value = ""
			}
		},

		normalizeCommentItem(comment) {
			const commentImages = this.parseJson(comment.images || comment.imageList || comment.image_urls || comment.imageUrls)
			const currentUserId = String(this.currentUser.id || localStorage.getItem("userId") || "")
			const commentUserId = String(comment.user_id ?? comment.userId ?? comment.authorId ?? comment.author_id ?? "")
			const currentUserName = String(this.currentUser.username || "").trim()
			const commentName = String(comment.username || comment.name || "").trim()
			const replyList = Array.isArray(comment.replies)
				? comment.replies.map(reply => this.normalizeReplyItem(reply, comment.username || comment.name || "匿名用户"))
				: []

			return {
				id: comment.comment_id ?? comment.id,
				name: comment.username || comment.name || "匿名用户",
				time: this.formatTime(comment.create_time || comment.createdAt || comment.created_at),
				avatar: this.toDisplayImageUrl(comment.avatar || comment.userAvatar || comment.authorAvatar) || 'default.jpg',
				content: comment.content || "",
				image: this.toDisplayImageUrl(commentImages[0] || ""),
				likes: Number(comment.like_count ?? comment.likes ?? 0),
				liked: this.toBooleanLikeFlag(comment.liked ?? comment.is_liked ?? comment.likeStatus ?? comment.hasLiked),
				userId: commentUserId,
				isMine: Boolean(comment.isMine || comment.is_mine || (currentUserId && commentUserId && currentUserId === commentUserId) || (currentUserName && commentName && currentUserName === commentName)),
				replyCount: Number(comment.reply_count ?? comment.replyCount ?? 0),
				replies: replyList
			}
		},

		normalizeReplyItem(reply, fallbackTargetName) {
			const replyImages = this.parseJson(reply.images || reply.imageList || reply.image_urls || reply.imageUrls)
			const text = String(reply.text || reply.content || reply.comment || "").trim()
			const currentUserId = String(this.currentUser.id || localStorage.getItem("userId") || "")
			const replyUserId = String(reply.user_id ?? reply.userId ?? reply.uid ?? reply.userID ?? reply.authorId ?? reply.author_id ?? "")
			const currentUserName = String(this.currentUser.username || "").trim()
			const replyName = String(reply.username || reply.name || reply.userName || reply.nickname || "").trim()

			return {
				...reply,
				id: reply.id ?? reply.comment_id,
				comment_id: reply.comment_id ?? reply.id,
				content: reply.content || text,
				text,
				displayText: text,
				image: this.toDisplayImageUrl(reply.image || replyImages[0] || ""),
				targetName: reply.targetName || fallbackTargetName || "",
				avatar: this.toDisplayImageUrl(reply.avatar || reply.userAvatar || reply.authorAvatar) || "",
				likes: Number(reply.likes ?? reply.like_count ?? 0),
				liked: this.toBooleanLikeFlag(reply.liked ?? reply.is_liked ?? reply.likeStatus ?? reply.hasLiked),
				userId: replyUserId,
				name: replyName,
				isMine: Boolean(reply.isMine || reply.is_mine || reply.isSelf || reply.is_self || (currentUserId && replyUserId && currentUserId === replyUserId) || (currentUserName && replyName && currentUserName === replyName))
			}
		},

		toBooleanLikeFlag(value) {
			if (typeof value === "boolean") return value
			if (typeof value === "number") return value === 1

			if (typeof value === "string") {
				const text = value.trim().toLowerCase()
				if (["1", "true", "yes", "y", "liked", "已点赞"].includes(text)) return true
				if (["0", "false", "no", "n", "unliked", "未点赞", ""].includes(text)) return false
			}

			return false
		},

		async refreshComments() {
			const commentRes = await getComments(this.post.id, { page: 1, pageSize: 20 })
			if (commentRes.code === 1 || commentRes.code === 0) {
				this.commentList = (commentRes.data?.list || []).map(c => this.normalizeCommentItem(c))
			}
		},

	resolveUploadedUrl(response) {
		if (!response) return ""

		if (typeof response === "string") {
			return this.normalizeUploadPath(response)
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
				return this.normalizeUploadPath(candidate)
			}
		}

		if (response.data && typeof response.data === "object") {
			return this.resolveUploadedUrl(response.data)
		}

		return ""
	},

	normalizeUploadPath(path) {
		if (!path) return ""
		if (/^https?:\/\//.test(path)) return path
		if (path.startsWith("/")) return path
		if (path.startsWith("uploads/")) return `/${path}`
		if (path.startsWith("uploads\\")) return `/${path.replace(/\\/g, "/")}`
		return path
	},

	toDisplayImageUrl(path) {
		const normalized = this.normalizeUploadPath(path)
		if (!normalized) return ""
		if (/^https?:\/\//.test(normalized)) return normalized
		if (normalized.startsWith("/uploads")) return `http://localhost:8080${normalized}`
		return normalized
	},

		async submitComment() {
		const text = String(this.commentDraft || "").trim()

		if (!text && !this.commentImage) {
			return
		}

		if (!this.post) return

		try {
			let uploadedImageUrl = ""

			// 如果有图片，先上传
			if (this.commentImageFile) {
				const uploadRes = await uploadFile(this.commentImageFile)
				uploadedImageUrl = this.resolveUploadedUrl(uploadRes) || ""

				if (!uploadedImageUrl) {
					this.$message.error("评论图片上传失败")
					return
				}
			}

			const res = await createComment(this.post.id, {
				content: text,
				images: uploadedImageUrl ? [uploadedImageUrl] : []
			})

			if (res.code === 1 || res.code === 0) {
				this.commentDraft = ""
				this.clearCommentImage()

				// 重新加载评论列表
				const commentRes = await getComments(this.post.id, { page: 1, pageSize: 20 })
				if (commentRes.code === 1 || commentRes.code === 0) {
					const list = commentRes.data?.list || []
					this.commentList = list.map(c => ({
						id: c.comment_id,
						name: c.username,
						time: this.formatTime(c.create_time),
						avatar: this.toDisplayImageUrl(c.avatar) || 'default.jpg',
						content: c.content,
						image: this.toDisplayImageUrl(this.parseJson(c.images)[0] || ""),
						likes: c.like_count || 0,
						replyCount: 0,
						liked: false
					}))
				}
				this.post.comments += 1
			} else {
				this.$message.error(res.message || "评论失败")
			}
		} catch (error) {
			console.error('发表评论失败:', error)
			this.$message.error("发表评论失败")
		}
	},

		async handleLikeComment(commentId) {
			if (!this.post) return

			const target = this.commentList.find(comment => comment.id === commentId)
			if (!target) return

			const previousLiked = !!target.liked
			const previousLikes = Number(target.likes || 0)

			try {
				if (previousLiked) {
					await unlikeComment(this.post.id, commentId)
					target.liked = false
					target.likes = Math.max(0, previousLikes - 1)
				} else {
					await likeComment(this.post.id, commentId)
					target.liked = true
					target.likes = previousLikes + 1
				}
			} catch (error) {
				target.liked = previousLiked
				target.likes = previousLikes
				const msg = error?.response?.data?.message || error?.message || "评论点赞失败"
				this.$message.error(msg)
			}
		},

		async handleLikeReply(payload) {
			if (!this.post || !payload?.id) return

			const replyId = payload.id
			let targetReply = null

			for (const comment of this.commentList) {
				const replies = Array.isArray(comment.replies) ? comment.replies : []
				targetReply = replies.find(reply => (reply.id ?? reply.comment_id) === replyId)
				if (targetReply) break
			}

			if (!targetReply) return

			const previousLiked = !!targetReply.liked
			const previousLikes = Number(targetReply.likes || 0)

			try {
				if (previousLiked) {
					await unlikeComment(this.post.id, replyId)
					targetReply.liked = false
					targetReply.likes = Math.max(0, previousLikes - 1)
				} else {
					await likeComment(this.post.id, replyId)
					targetReply.liked = true
					targetReply.likes = previousLikes + 1
				}
			} catch (error) {
				targetReply.liked = previousLiked
				targetReply.likes = previousLikes
				const msg = error?.response?.data?.message || error?.message || "回复点赞失败"
				this.$message.error(msg)
			}
		},

		async handleDeleteComment(comment) {
			if (!this.post || !comment?.id) return

			try {
				await deleteComment(this.post.id, comment.id)
				this.commentList = this.commentList.filter(item => item.id !== comment.id)
				this.post.comments = Math.max(0, Number(this.post.comments || 0) - 1)
			} catch (error) {
				const msg = error?.response?.data?.message || error?.message || "删除评论失败"
				this.$message.error(msg)
			}
		},

		async handleReplyComment(payload) {
			if (!this.post || !payload?.id) return

			try {
				let uploadedImageUrl = ""

				if (payload.image) {
					const blob = await fetch(payload.image).then(response => response.blob())
					const file = new File([blob], `reply-${Date.now()}.png`, { type: blob.type || "image/png" })
					const uploadRes = await uploadFile(file)
					uploadedImageUrl = this.resolveUploadedUrl(uploadRes) || ""
					if (!uploadedImageUrl) {
						this.$message.error("回复图片上传失败")
						return
					}
				}

				if (uploadedImageUrl) {
					await createComment(this.post.id, {
						content: payload.text || "[图片]",
						images: [uploadedImageUrl],
						parentCommentId: payload.id
					})
				} else {
					await replyComment(this.post.id, payload.id, {
						content: payload.text || ""
					})
				}

				await this.refreshComments()
				this.post.comments = Number(this.post.comments || 0) + 1
			} catch (error) {
				const msg = error?.response?.data?.message || error?.message || "回复评论失败"
				this.$message.error(msg)
			}
		},

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

		async openAuthorProfile() {
			if (!this.post) return

			console.log("[PostDetailsView] openAuthorProfile - 当前 post 对象:", this.post)
			
			// 获取作者 ID（兼容多种字段名）
			const authorId = this.post.userId ?? this.post.user_id ?? this.post.authorId ?? this.post.author_id
			console.log("[PostDetailsView] 尝试获取的 authorId:", authorId)
			
			if (!authorId) {
				console.error("[PostDetailsView] 无法从 post 对象中获取 userId")
				this.$message.error("无法获取用户信息")
				return
			}

			try {
				console.log("[PostDetailsView] 开始调用 getUser(", authorId, ")")
				const response = await getUser(authorId)
				console.log("[PostDetailsView] getUser 返回响应:", response)
				console.table(response)
				
				const userData = this.unwrapPayload(response)
				console.log("[PostDetailsView] 解析后的 userData:", userData)

				this.$router.push({
					name: "userInformation",
					query: {
						user: encodeURIComponent(JSON.stringify(userData))
					}
				})
			} catch (error) {
				console.error("[PostDetailsView] getUser 请求失败:", error)
				console.error("[PostDetailsView] 错误响应:", error?.response?.data)
				const msg = error?.response?.data?.message || error?.message || "获取用户信息失败"
				this.$message.error(msg)
			}
		},

		unwrapPayload(response) {
			const code = response?.code
			const normalizedCode = code === undefined || code === null ? null : String(code)
			const isBusinessSuccess =
				normalizedCode === null ||
				normalizedCode === "0" ||
				normalizedCode === "1" ||
				normalizedCode === "200" ||
				response?.success === true

			if (!isBusinessSuccess) {
				throw new Error(response?.message || response?.msg || "请求失败")
			}

			if (response && Object.prototype.hasOwnProperty.call(response, "data")) {
				return response.data
			}

			return response
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
	background: #ddd8f4;
	color: #5f5877;
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
	color: #222;
	min-height: 42px;
	max-height: 120px;
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
