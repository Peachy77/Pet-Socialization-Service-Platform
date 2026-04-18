package com.jtyu.backend.service;

import com.jtyu.backend.model.Message;

import java.util.List;
import java.util.Map;

public interface MessageService {
    // 获取通知列表
    Map<String, Object> getMessages(Integer userId, Integer page, Integer pageSize);

    // 获取未读通知数量
    Long getUnreadCount(Integer userId);

    // 标记通知为已读
    boolean markAsRead(Integer messageId, Integer userId);

    // 标记所有通知为已读
    boolean markAllAsRead(Integer userId);

    // 创建通知
    Integer createMessage(Integer receiverId, Integer senderId, String type, String content, Integer relatedId);

    // 删除通知
    boolean deleteMessage(Integer messageId, Integer userId);
}
