import apiClient from "./client";

function getMessageScopedPath(userId, suffix) {
  if (!suffix) {
    return userId ? `/users/${userId}/messages` : "/messages";
  }
  return userId ? `/users/${userId}/messages/${suffix}` : `/messages/${suffix}`;
}

// 获取主消息页会话列表（直接调用 /messages 接口）
// params 常用字段：page、pageSize、keyword
export function getMessages(params = {}) {
  return apiClient.get("/messages", { params });
}

// 获取主消息页会话列表（最近联系人 + 最后一条消息 + 未读数）
// params 常用字段：page、pageSize、keyword
// 不传 userId：后端根据 token 返回当前用户会话列表
export function getConversationList(params = {}, userId) {
  return apiClient.get(getMessageScopedPath(userId, "conversations"), { params });
}

// 获取与某个用户的私信记录（详情页）
// targetUserId: 对方用户 ID（必传）
// params 常用字段：before（时间游标）、page、pageSize
export function getConversationMessages(targetUserId, params = {}, userId) {
  return apiClient.get(getMessageScopedPath(userId, `conversations/${targetUserId}`), { params });
}

// 发送私信（创建消息）
// data 建议字段：
// receiver_id: number（必填）
// content: string（文本消息，和 images 至少有一个）
// images: string[]（图片 URL 数组，可选）
export function createPrivateMessage(data, userId) {
  return apiClient.post(getMessageScopedPath(userId, ""), data);
}

// 将与某个用户的会话标记为已读
export function markConversationAsRead(targetUserId, userId) {
  return apiClient.patch(getMessageScopedPath(userId, `conversations/${targetUserId}/read`));
}

// 获取当前用户未读私信总数（消息角标）
export function getUnreadMessageCount(userId) {
  return apiClient.get(getMessageScopedPath(userId, "unread-count"));
}

// 兼容旧命名：获取某会话消息
// 可传 targetUserId；不传时走后端默认逻辑
export function getPrivateMessages(targetUserId, params = {}, userId) {
  if (!targetUserId) {
    return apiClient.get(getMessageScopedPath(userId, "private"), { params });
  }
  return getConversationMessages(targetUserId, params, userId);
}

// 兼容旧命名：发送私信
export function sendPrivateMessage(data, userId) {
  return createPrivateMessage(data, userId);
}