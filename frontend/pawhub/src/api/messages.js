import apiClient from "./client";

// 获取消息列表
export function getMessages() {
  return apiClient.get("/messages");
}

// 获取私信
export function getPrivateMessages() {
  return apiClient.get("/private_messages");
}

// 发送私信
export function sendPrivateMessage(data) {
  return apiClient.post("/private_messages", data);
}