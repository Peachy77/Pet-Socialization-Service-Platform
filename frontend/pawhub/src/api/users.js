import apiClient from "./client";

function normalizeAuthPayload(data) {
  const payload = data || {};
  const email = payload.email || payload.account;

  return {
    ...payload,
    email
  };
}

function getUserScopedPath(userId, suffix) {
  return userId ? `/users/${userId}/${suffix}` : `/users/me/${suffix}`;
}

// 用户登录
// 兼容当前前端的 account 字段和后端数据库的 email 字段，最终统一按 email 传给后端
export function login(data) {
  return apiClient.post("/users/login", normalizeAuthPayload(data));
}

// 用户注册
// 新用户仅需要传 email 和 password；如果前端仍传 account，也会自动映射为 email
export function register(data) {
  return apiClient.post("/users/register", normalizeAuthPayload(data));
}

// 获取用户列表
// params 常用字段：page、pageSize、keyword、status、sort
export function getUsers(params = {}) {
  return apiClient.get("/users", { params });
}

// 根据关键词搜索用户列表
// keyword: 搜索关键词，通常匹配 username 或 email
// params: 可继续携带 page、pageSize、status、sort 等条件
export function searchUsers(keyword, params = {}) {
  return apiClient.get("/users", {
    params: {
      ...params,
      keyword
    }
  });
}

// 兼容别名：按关键词获取用户列表
export function getUsersByKeyword(keyword, params = {}) {
  return searchUsers(keyword, params);
}

// 获取单个用户信息
// 通常用于管理员或公开用户主页，返回 user_id、username、email、avatar、bio、follower_count、following_count
export function getUser(id) {
  return apiClient.get(`/users/${id}`);
}

// 获取当前登录用户信息
// 建议后端根据 token 识别当前用户，返回完整用户资料和统计字段
export function getCurrentUser() {
  return apiClient.get("/users/me");
}

// 更新用户
// 用于编辑指定用户资料，适合管理后台或调试场景
export function updateUser(id, data) {
  return apiClient.put(`/users/${id}`, data);
}

// 更新当前登录用户资料
// EditView 后续建议优先调用这个接口，后端按 token 更新当前账号对应的数据
export function updateCurrentUser(data) {
  return apiClient.put("/users/me", data);
}

// 关注用户
// targetUserId 为被关注用户的 user_id
export function followUser(targetUserId) {
  return apiClient.post(`/users/follow/${targetUserId}`);
}

// 取消关注用户
// targetUserId 为被取消关注用户的 user_id
export function unfollowUser(targetUserId) {
  return apiClient.delete(`/users/follow/${targetUserId}`);
}

// 获取当前用户发布的动态（Mine-动态）
// 不传 userId 时走 /users/me/posts，由后端根据 token 识别
export function getMyPosts(params = {}, userId) {
  return apiClient.get(getUserScopedPath(userId, "posts"), { params });
}

// 获取当前用户收藏的店家/服务（Mine-收藏）
// 不传 userId 时走 /users/me/favorites，由后端根据 token 识别
export function getMyFavorites(params = {}, userId) {
  return apiClient.get(getUserScopedPath(userId, "favorites"), { params });
}

// 获取当前用户订单列表（Mine-订单）
// 不传 userId 时走 /users/me/orders，由后端根据 token 识别
export function getMyOrders(params = {}, userId) {
  return apiClient.get(getUserScopedPath(userId, "orders"), { params });
}

// 获取当前用户关注列表（我关注的人）
// 不传 userId 时走 /users/me/following，由后端根据 token 识别
export function getMyFollowing(params = {}, userId) {
  return apiClient.get(getUserScopedPath(userId, "following"), { params });
}

// 获取当前用户粉丝列表（关注我的人）
// 不传 userId 时走 /users/me/followers，由后端根据 token 识别
export function getMyFollowers(params = {}, userId) {
  return apiClient.get(getUserScopedPath(userId, "followers"), { params });
}

