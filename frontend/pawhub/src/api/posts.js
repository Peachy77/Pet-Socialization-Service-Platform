import apiClient from "./client";

// 获取帖子
export function getPosts() {
  return apiClient.get("/posts");
}

// 创建帖子
export function createPost(data) {
  return apiClient.post("/posts", data);
}

// 删除帖子
export function deletePost(id) {
  return apiClient.delete(`/posts/${id}`);
}

// 点赞
export function likePost(id) {
  return apiClient.post(`/posts/${id}/like`);
}

// 获取评论
export function getComments(postId) {
  return apiClient.get(`/posts/${postId}/comments`);
}

// 创建评论
export function createComment(postId, data) {
  return apiClient.post(`/posts/${postId}/comments`, data);
}