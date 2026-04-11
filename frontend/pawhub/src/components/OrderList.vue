<template>
  <div class="order-list">
    <div
      v-for="order in orders"
      :key="order.id"
      class="order-card"
    >
      <div class="order-head">
        <div class="status" :class="`status-${order.status}`">
          <span class="status-icon">{{ getStatusIcon(order.status) }}</span>
          <span>{{ getStatusText(order.status) }}</span>
        </div>
        <div class="order-id">订单号：{{ order.id }}</div>
      </div>

      <div class="order-body">
        <div class="service-name">{{ order.serviceName }}</div>
        <div class="meta">商户：{{ order.merchantName }}</div>
        <div class="meta">时间：{{ order.time }}</div>
      </div>

      <div class="order-foot">
        <div class="price">{{ order.price }}</div>

        <div class="actions">
          <button class="btn btn-outline" @click="$emit('view', order)">
            查看详情
          </button>

          <button
            v-if="order.status === 'pending'"
            class="btn btn-danger"
            @click="$emit('cancel', order)"
          >
            取消订单
          </button>

          <button
            v-if="order.status === 'completed'"
            class="btn btn-primary"
            @click="$emit('rebook', order)"
          >
            再次预约
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "OrderList",

  props: {
    orders: {
      type: Array,
      default: () => []
    }
  },

  methods: {
    getStatusText(status) {
      if (status === "pending") return "待服务"
      if (status === "completed") return "已完成"
      if (status === "cancelled") return "已取消"
      return "未知状态"
    },

    getStatusIcon(status) {
      if (status === "pending") return "◷"
      if (status === "completed") return "✓"
      if (status === "cancelled") return "✕"
      return "•"
    }
  }
}
</script>

<style scoped>
.order-list{
  padding:15px;
}

.order-card{
  background:white;
  border:1px solid #e9e9ee;
  border-radius:16px;
  box-shadow:0 2px 8px rgba(31,38,135,0.08);
  overflow:hidden;
  margin-bottom:16px;
}

.order-head{
  display:flex;
  justify-content:space-between;
  align-items:center;
  padding:14px 18px;
  border-bottom:1px solid #f0f0f3;
}

.status{
  display:flex;
  align-items:center;
  gap:8px;
  font-size:14px;
  color:#333;
}

.status-icon{
  font-size:16px;
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

.order-id{
  color:#58627a;
  font-size:14px;
}

.order-body{
  padding:18px;
  border-bottom:1px solid #f0f0f3;
}

.service-name{
  font-size:22px;
  font-weight:600;
  margin-bottom:14px;
  color:#0f172a;
}

.meta{
  font-size:16px;
  color:#334155;
  margin-top:8px;
}

.order-foot{
  display:flex;
  align-items:center;
  justify-content:space-between;
  padding:16px 18px;
}

.price{
  font-size:32px;
  color:#a8a0d9;
}

.actions{
  display:flex;
  gap:10px;
}

.btn{
  min-width:118px;
  height:40px;
  border-radius:18px;
  border:1px solid transparent;
  background:white;
  font-size:14px;
  padding:0 16px;
}

.btn-outline{
  border-color:#cfd4de;
  color:#1f2937;
}

.btn-danger{
  border-color:#ff9ea5;
  color:#ff4d5d;
}

.btn-primary{
  background:#a8a0d9;
  color:white;
}
</style>