import apiClient from "./client";

function normalizeOrderPayload(data = {}) {
  return {
    service_id: data.service_id || data.serviceId || null,
    project_name: data.project_name || data.projectName || "",
    appointment_date: data.appointment_date || data.appointmentDate || "",
    appointment_time: data.appointment_time || data.appointmentTime || "",
    remark: data.remark || "",
    price: data.price || ""
  };
}

function getOrderScopedPath(userId, suffix) {
  if (!suffix) {
    return userId ? `/users/${userId}/orders` : "/orders";
  }

  return userId ? `/users/${userId}/orders/${suffix}` : `/orders/${suffix}`;
}

// 获取预约订单列表
// params 常用字段：page、pageSize、status、keyword、sort
export function getOrders(params = {}, userId) {
  return apiClient.get(getOrderScopedPath(userId, ""), { params });
}

// 创建预约订单
// data 建议字段：service_id、project_name、appointment_date、appointment_time、remark、price
export function createOrder(data) {
  return apiClient.post("/orders", normalizeOrderPayload(data));
}

// 获取当前用户订单列表
// 不传 userId 时走 /users/me/orders，由后端根据 token 识别
export function getMyOrders(params = {}) {
  return apiClient.get("/orders", { params });
  // return apiClient.get(getOrderScopedPath(userId, ""), { params });
}

// 获取订单详情
export function getOrderDetail(orderId) {
  return apiClient.get(`/orders/${orderId}`);
}

// 取消订单
export function cancelOrder(orderId) {
  return apiClient.delete(`/orders/${orderId}`);
}

// 更新订单状态
// data 建议字段：status, update_time, remark
export function updateOrderStatus(orderId, data) {
  return apiClient.patch(`/orders/${orderId}/status`, data);
}

// 预约创建别名：更贴近业务语义
export function createAppointmentOrder(data) {
  return createOrder(data);
}