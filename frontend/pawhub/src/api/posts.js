import apiClient from "./client";

function normalizePostPayload(data = {}) {
  return {
    content: data.content || "",
    images: Array.isArray(data.images) ? data.images : [],
    tags: Array.isArray(data.tags) ? data.tags : []
  };
}

// 获取动态列表
// params 常用字段：page、pageSize、keyword、tag、sort
export function getPosts(params = {}) {
  return apiClient.get("/posts", { params });
}

// 根据关键词筛选动态
// keyword: 搜索关键词
// params: 可继续携带 page、pageSize、tag、sort 等条件
export function searchPosts(keyword, params = {}) {
  return apiClient.get("/posts", {
    params: {
      ...params,
      keyword
    }
  });
}

// 兼容别名：按关键词获取动态
export function getPostsByKeyword(keyword, params = {}) {
  return searchPosts(keyword, params);
}

// 获取动态详情（PostDetailsView）
export function getPostDetail(postId) {
  return apiClient.get(`/posts/${postId}`);
}

// 创建动态（PublishView）
// data 建议字段：content、images(string[])、tags(string[])
export function createPost(data) {
  return apiClient.post("/posts", normalizePostPayload(data));
}

// 删除帖子
export function deletePost(postId) {
  return apiClient.delete(`/posts/${postId}`);
}

// 点赞动态（PostDetailsView 点赞按钮）
export function likePost(postId) {
  return apiClient.post(`/posts/${postId}/like`);
}

// 取消点赞动态
export function unlikePost(postId) {
  return apiClient.delete(`/posts/${postId}/like`);
}

// 获取评论列表（PostDetailsView 评论区）
// params 常用字段：page、pageSize、sort
export function getComments(postId, params = {}) {
  return apiClient.get(`/posts/${postId}/comments`, { params });
}

// 创建评论
export function createComment(postId, data) {
  return apiClient.post(`/posts/${postId}/comments`, data);
}

// 点赞评论
export function likeComment(postId, commentId) {
  return apiClient.post(`/posts/${postId}/comments/${commentId}/like`);
}

// 取消点赞评论
export function unlikeComment(postId, commentId) {
  return apiClient.delete(`/posts/${postId}/comments/${commentId}/like`);
}

// 删除评论
export function deleteComment(postId, commentId) {
  return apiClient.delete(`/posts/${postId}/comments/${commentId}`);
}

// 回复评论
// data 建议字段：content
export function replyComment(postId, commentId, data) {
  return apiClient.post(`/posts/${postId}/comments/${commentId}/replies`, data);
}