import apiClient from "./client";

// 获取用户列表
export function getUsers() {
  return apiClient.get("/users");
}

// 获取单个用户信息
export function getUser(id) {
  return apiClient.get(`/users/${id}`);
}

// 创建用户
export function createUser(data) {
  return apiClient.post("/users", data);
}

// 更新用户
export function updateUser(id, data) {
  return apiClient.put(`/users/${id}`, data);
}

// 删除用户
export function deleteUser(id) {
  return apiClient.delete(`/users/${id}`);
}