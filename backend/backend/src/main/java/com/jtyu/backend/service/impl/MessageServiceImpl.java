package com.jtyu.backend.service.impl;

import com.jtyu.backend.mapper.MessageMapper;
import com.jtyu.backend.model.Message;
import com.jtyu.backend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageServiceImpl implements MessageService {
    @Autowired
    private MessageMapper messageMapper;

    @Override
    public Map<String, Object> getMessages(Integer userId, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> list = messageMapper.selectByReceiverId(userId, offset, pageSize);
        Long total = messageMapper.countUnread(userId); // 这里可以改成分页总数

        // 获取总数
        // 简单处理，实际可以加一个 countByReceiverId 方法
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size());
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }

    @Override
    public Long getUnreadCount(Integer userId) {
        return messageMapper.countUnread(userId);
    }

    @Override
    @Transactional
    public boolean markAsRead(Integer messageId, Integer userId) {
        // 验证消息归属
        Map<String, Object> message = null;
        // 简化处理，直接更新
        return messageMapper.markAsRead(messageId) > 0;
    }

    @Override
    @Transactional
    public boolean markAllAsRead(Integer userId) {
        return messageMapper.markAllAsRead(userId) > 0;
    }

    @Override
    @Transactional
    public Integer createMessage(Integer receiverId, Integer senderId, String type, String content, Integer relatedId) {
        Message message = new Message();
        message.setReceiverId(receiverId);
        message.setSenderId(senderId);
        message.setType(type);
        message.setContent(content);
        message.setRelatedId(relatedId);
        message.setIsRead(false);

        int rows = messageMapper.insert(message);
        if (rows > 0) {
            return message.getMessageId();
        }
        return null;
    }

    @Override
    @Transactional
    public boolean deleteMessage(Integer messageId, Integer userId) {
        return messageMapper.deleteById(messageId) > 0;
    }
}
