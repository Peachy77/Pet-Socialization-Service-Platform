import apiClient from "./client";

// 获取预约订单列表
export function getOrders() {
  return apiClient.get("/orders");
}

// 创建预约订单
export function createOrder(data) {
  return apiClient.post("/orders", data);
}