package com.jtyu.backend.service;

import com.jtyu.backend.model.PrivateMessage;

import java.util.List;
import java.util.Map;

public interface PrivateMessageService {
    // 获取会话列表
    List<Map<String, Object>> getConversationList(Integer userId);

    // 获取与某个用户的聊天记录
    Map<String, Object> getConversation(Integer userId, Integer targetUserId, Integer page, Integer pageSize);

    // 发送私信
    Integer sendMessage(Integer senderId, Integer receiverId, String content, List<String> images);

    // 标记会话为已读
    boolean markConversationAsRead(Integer userId, Integer senderId);

    // 获取未读消息总数
    Long getUnreadCount(Integer userId);
}
