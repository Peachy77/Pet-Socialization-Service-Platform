import apiClient from "./client";

// 获取商户列表
export function getServices() {
  return apiClient.get("/services");
}

// 获取单个商户信息
export function getServiceReviews(serviceId) {
  return apiClient.get(`/services/${serviceId}/reviews`);
}

// 创建商户评论
export function createServiceReview(serviceId, data) {
  return apiClient.post(`/services/${serviceId}/reviews`, data);
}

// 添加商户到收藏
export function addFavorite(data) {
  return apiClient.post("/favorites", data);
}