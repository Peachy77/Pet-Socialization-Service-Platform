<template>
	<div class="order-detail-page">
		<div class="topbar">
			<button class="back-btn" @click="goBack">‹</button>
			<div class="title">订单详情</div>
			<span class="spacer"></span>
		</div>

		<div class="hero-card">
			<div class="hero-left">
				<div class="hero-label">当前状态</div>
				<div class="hero-status" :class="`status-${order.status}`">
					{{ statusText }}
				</div>
			</div>
			<div class="hero-right">
				<div class="hero-amount-label">订单金额</div>
				<div class="hero-amount">{{ order.price || '-' }}</div>
			</div>
		</div>

		<div class="detail-card">
			<div class="section-title">预约信息</div>

			<div class="row-item">
				<span class="label">下单用户</span>
				<span class="value">{{ order.userName || '-' }}</span>
			</div>
			<div class="row-item">
				<span class="label">服务项名称</span>
				<span class="value">{{ order.serviceName || '-' }}</span>
			</div>
			<div class="row-item">
				<span class="label">预约店铺</span>
				<span class="value">{{ order.merchantName || '-' }}</span>
			</div>
			<div class="row-item">
				<span class="label">预约时间</span>
				<span class="value">{{ order.appointmentTime || order.time || '-' }}</span>
			</div>
			<div class="row-item column-item">
				<span class="label">备注信息</span>
				<div class="remark-box">{{ order.remark || '无' }}</div>
			</div>
		</div>

		<div class="detail-card">
			<div class="section-title">订单信息</div>

			<div class="row-item">
				<span class="label">订单号</span>
				<span class="value">{{ order.id || '-' }}</span>
			</div>
			<div class="row-item">
				<span class="label">下单时间</span>
				<span class="value">{{ order.orderTime || '-' }}</span>
			</div>
			<div class="row-item">
				<span class="label">更新时间</span>
				<span class="value">{{ order.updateTime || '-' }}</span>
			</div>
			<div class="row-item">
				<span class="label">订单状态</span>
				<span class="value status-tag" :class="`status-${order.status}`">{{ statusText }}</span>
			</div>
		</div>

		<div v-if="order.status === 'pending'" class="bottom-action">
			<button class="cancel-btn" @click="cancelOrder">
				取消订单
			</button>
		</div>
	</div>
</template>

<script>
import { getOrderDetail, updateOrderStatus } from "@/api/orders"

export default {
	name: "OrderDetailsView",

	data() {
		return {
			order: {}
		}
	},

	computed: {
		statusText() {
			if (this.order.status === "pending") return "待服务"
			if (this.order.status === "completed") return "已完成"
			if (this.order.status === "cancelled") return "已取消"
			return "未知状态"
		}
	},

	async created() {
		const raw = this.$route.query.order
		const queryOrderId = this.$route.query.orderId
		let resolvedOrderId = queryOrderId ? Number(queryOrderId) : null

		if (raw) {
			try {
				this.order = JSON.parse(decodeURIComponent(raw))
				this.applyStoredStatus()
				resolvedOrderId = resolvedOrderId || Number(this.order.id)
			} catch (error) {
				console.error("订单详情解析失败", error)
				this.order = {}
			}
		}

		if (resolvedOrderId) {
			await this.loadOrderDetail(resolvedOrderId)
		}
	},

	methods: {
		async loadOrderDetail(orderId) {
			try {
				const response = await getOrderDetail(orderId)
				const payload = this.unwrapPayload(response)
				const mapped = this.mapOrderDetail(payload)
				if (mapped) {
					this.order = {
						...this.order,
						...mapped
					}
					this.applyStoredStatus()
				}
			} catch (error) {
				const msg = error?.response?.data?.message || error?.message || "加载订单详情失败"
				this.$message?.error?.(msg)
			}
		},

		mapOrderDetail(order) {
			if (!order || typeof order !== "object") return null

			const normalizedStatus = this.normalizeOrderStatus(order.status)

			return {
				id: order.id ?? order.orderId ?? order.order_id,
				status: normalizedStatus,
				userName: order.userName || order.username || order.user_email || "-",
				serviceName: order.serviceName || order.project_name || order.projectName || order.service_name || "-",
				merchantName: order.merchantName || order.storeName || order.shopName || "-",
				appointmentTime: order.appointmentTime || order.appointment_time || order.time || "-",
				time: order.time || order.appointmentTime || order.appointment_time || "-",
				remark: order.remark || "",
				price: this.normalizePrice(order.price),
				orderTime: order.orderTime || order.createTime || order.createdAt || order.create_time || "-",
				updateTime: order.updateTime || order.updatedAt || order.update_time || "-"
			}
		},

		normalizePrice(value) {
			if (value === undefined || value === null || value === "") return "-"
			const text = String(value)
			if (text === "-") return "-"
			return text.startsWith("¥") ? text : `¥${text}`
		},

		normalizeOrderStatus(status) {
			const text = String(status || "").toLowerCase()
			if (["pending", "wait", "waiting", "0", "待服务", "待处理"].includes(text)) return "pending"
			if (["completed", "done", "finish", "finished", "1", "已完成"].includes(text)) return "completed"
			if (["cancelled", "canceled", "cancel", "2", "已取消"].includes(text)) return "cancelled"
			return "pending"
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
			this.$router.back()
		},

		async cancelOrder() {
			if (this.order.status !== "pending") return

			const confirmed = window.confirm("确认取消订单吗？")
			if (!confirmed) return

			try {
				await this.unwrapPayload(await updateOrderStatus(this.order.id, { status: "cancelled" }))
				const updateTime = this.formatNow()
				this.order = {
					...this.order,
					status: "cancelled",
					updateTime
				}
				this.saveOrderStatus(this.order.id, "cancelled", updateTime)
				this.$message?.success?.("取消订单成功")
			} catch (error) {
				const msg = error?.response?.data?.message || error?.message || "取消订单失败"
				this.$message?.error?.(msg)
			}
		},

		formatNow() {
			const date = new Date()
			const pad = (value) => String(value).padStart(2, "0")
			return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
		},

		saveOrderStatus(orderId, status, updateTime) {
			if (!orderId) return
			const cache = JSON.parse(localStorage.getItem("pawhub_order_status") || "{}")
			cache[orderId] = { status, updateTime }
			localStorage.setItem("pawhub_order_status", JSON.stringify(cache))
		},

		applyStoredStatus() {
			if (!this.order.id) return
			const cache = JSON.parse(localStorage.getItem("pawhub_order_status") || "{}")
			const stored = cache[this.order.id]
			if (!stored) return

			this.order = {
				...this.order,
				status: stored.status || this.order.status,
				updateTime: stored.updateTime || this.order.updateTime
			}
		}
	}
}
</script>

<style scoped>
.order-detail-page{
	min-height:100vh;
	background:linear-gradient(180deg, #f8f5ff 0%, #f3f6fb 35%, #f5f5f7 100%);
	padding:12px 14px 98px;
}

.topbar{
	display:flex;
	align-items:center;
	justify-content:space-between;
	margin-bottom:14px;
}

.back-btn{
	width:36px;
	height:36px;
	border:none;
	background:white;
	border-radius:10px;
	font-size:26px;
	line-height:1;
	box-shadow:0 2px 8px rgba(51,65,85,0.12);
}

.title{
	font-size:20px;
	font-weight:700;
	color:#111827;
}

.spacer{
	width:36px;
}

.hero-card{
	display:flex;
	justify-content:space-between;
	align-items:center;
	background:linear-gradient(135deg, #ffffff 0%, #f8f2ff 100%);
	border:1px solid #ece8ff;
	border-radius:16px;
	padding:16px;
	box-shadow:0 10px 24px rgba(149,116,209,0.12);
	margin-bottom:14px;
}

.hero-label,
.hero-amount-label{
	font-size:12px;
	color:#6b7280;
	margin-bottom:6px;
}

.hero-status{
	font-size:18px;
	font-weight:700;
}

.hero-amount{
	font-size:26px;
	font-weight:700;
	color:#8b76d9;
	text-align:right;
}

.detail-card{
	background:white;
	border-radius:16px;
	border:1px solid #eceff4;
	box-shadow:0 8px 20px rgba(15,23,42,0.06);
	padding:14px;
	margin-bottom:12px;
}

.section-title{
	font-size:16px;
	font-weight:700;
	color:#111827;
	margin-bottom:10px;
}

.row-item{
	display:flex;
	justify-content:space-between;
	align-items:flex-start;
	padding:11px 0;
	border-bottom:1px solid #f1f5f9;
	gap:14px;
}

.row-item:last-child{
	border-bottom:none;
}

.column-item{
	display:block;
}

.label{
	color:#6b7280;
	font-size:13px;
	min-width:72px;
}

.value{
	color:#0f172a;
	font-size:14px;
	font-weight:500;
	text-align:right;
}

.remark-box{
	margin-top:8px;
	background:#f8fafc;
	border:1px solid #e2e8f0;
	border-radius:10px;
	color:#334155;
	font-size:13px;
	line-height:1.6;
	padding:10px;
}

.status-pending{
	color:#ff6b00;
}

.status-completed{
	color:#00b24a;
}

.status-cancelled{
	color:#8d96a8;
}

.status-tag{
	font-weight:700;
}

.bottom-action{
	position:fixed;
	left:0;
	right:0;
	bottom:0;
	background:rgba(255,255,255,0.96);
	backdrop-filter:blur(6px);
	border-top:1px solid #eceff4;
	padding:12px 14px;
}

.cancel-btn{
	width:100%;
	height:44px;
	border-radius:22px;
	border:1px solid #ff9ea5;
	background:#fff5f6;
	color:#ff4d5d;
	font-size:15px;
	font-weight:600;
}
</style>
