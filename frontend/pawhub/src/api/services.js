import apiClient from "./client";

// eslint-disable-next-line no-unused-vars
function normalizeServicePayload(data = {}) {
  return {
    name: data.name || "",
    type: data.type || "",
    address: data.address || "",
    price: data.price || "",
    intro: data.intro || "",
    image: data.image || "",
    images: Array.isArray(data.images) ? data.images : [],
    tags: Array.isArray(data.tags) ? data.tags : [],
    projects: Array.isArray(data.projects) ? data.projects : []
  };
}

// eslint-disable-next-line no-unused-vars
function getUserScopedPath(userId, suffix) {
  if (!suffix) {
    return userId ? `/users/${userId}/services` : "/services";
  }

  return userId ? `/users/${userId}/services/${suffix}` : `/services/${suffix}`;
}

// 获取商户列表
// params 常用字段：page、pageSize、keyword、type、sort、distance、rating
export function getServices(params = {}) {
  return apiClient.get("/services", { params });
}

// 根据关键词搜索商户
export function searchServices(keyword, params = {}) {
  return apiClient.get("/services", {
    params: {
      ...params,
      keyword
    }
  });
}

// 根据分类获取商户列表
export function getServicesByType(type, params = {}) {
  return apiClient.get("/services", {
    params: {
      ...params,
      type
    }
  });
}

// 获取单个商户详情
export function getServiceDetail(serviceId) {
  return apiClient.get(`/services/${serviceId}`);
}

// 获取单个商户信息
// 兼容旧命名：实际返回商户详情
export function getService(serviceId) {
  return getServiceDetail(serviceId);
}

// 获取商户评论列表
// params 常用字段：page、pageSize、sort
export function getServiceReviews(serviceId, params = {}) {
  return apiClient.get(`/services/${serviceId}/reviews`, { params });
}

// 创建商户评论
export function createServiceReview(serviceId, data) {
  return apiClient.post(`/services/${serviceId}/reviews`, data);
}

// 回复商户评论
// data 建议字段：content
export function replyServiceReview(serviceId, reviewId, data) {
  return apiClient.post(`/services/${serviceId}/reviews/${reviewId}/replies`, data);
}

// 点赞商户评论
export function likeServiceReview(serviceId, reviewId) {
  return apiClient.post(`/services/${serviceId}/reviews/${reviewId}/like`);
}

// 取消点赞商户评论
export function unlikeServiceReview(serviceId, reviewId) {
  return apiClient.delete(`/services/${serviceId}/reviews/${reviewId}/like`);
}

// 添加商户到收藏
// data 建议字段：service_id / serviceId
export function addFavorite(data) {
  return apiClient.post("/favorites", data);
}

// 从收藏中移除商户
// data 建议字段：service_id / serviceId
export function removeFavorite(data) {
  return apiClient.delete("/favorites", { data });
}
