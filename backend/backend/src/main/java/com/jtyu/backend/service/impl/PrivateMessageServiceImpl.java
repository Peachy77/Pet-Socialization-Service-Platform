package com.jtyu.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtyu.backend.mapper.PrivateMessageMapper;
import com.jtyu.backend.model.PrivateMessage;
import com.jtyu.backend.service.PrivateMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrivateMessageServiceImpl implements PrivateMessageService {
    @Autowired
    private PrivateMessageMapper privateMessageMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<Map<String, Object>> getConversationList(Integer userId) {
        List<Map<String, Object>> conversations = privateMessageMapper.selectConversationList(userId);

        // 获取每个会话的未读数量
        List<Map<String, Object>> unreadGroup = privateMessageMapper.selectUnreadGroupBySender(userId);
        Map<Integer, Long> unreadMap = new HashMap<>();
        for (Map<String, Object> item : unreadGroup) {
            Integer senderId = (Integer) item.get("sender_id");
            Long count = (Long) item.get("unreadCount");
            unreadMap.put(senderId, count);
        }

        for (Map<String, Object> conv : conversations) {
            Integer otherUserId = (Integer) conv.get("userId");
            Long unreadCount = unreadMap.getOrDefault(otherUserId, 0L);
            conv.put("unreadCount", unreadCount);
        }

        return conversations;
    }

    @Override
    public Map<String, Object> getConversation(Integer userId, Integer targetUserId, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> list = privateMessageMapper.selectConversation(userId, targetUserId, offset, pageSize);
        Long total = privateMessageMapper.countConversation(userId, targetUserId);

        // 解析 images JSON
        for (Map<String, Object> msg : list) {
            String imagesStr = (String) msg.get("images");
            if (imagesStr != null && !imagesStr.isEmpty()) {
                try {
                    msg.put("images", objectMapper.readValue(imagesStr, List.class));
                } catch (Exception e) {
                    msg.put("images", Collections.emptyList());
                }
            } else {
                msg.put("images", Collections.emptyList());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }

    @Override
    @Transactional
    public Integer sendMessage(Integer senderId, Integer receiverId, String content, List<String> images) {
        PrivateMessage message = new PrivateMessage();
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setContent(content);
        message.setIsRead(false);
        try {
            message.setImages(objectMapper.writeValueAsString(images != null ? images : Collections.emptyList()));
        } catch (Exception e) {
            message.setImages("[]");
        }

        int rows = privateMessageMapper.insert(message);
        if (rows > 0) {
            return message.getMessageId();
        }
        return null;
    }

    @Override
    @Transactional
    public boolean markConversationAsRead(Integer userId, Integer senderId) {
        // userId 是当前用户（接收方），senderId 是发送方
        return privateMessageMapper.markAsRead(senderId, userId) > 0;
    }

    @Override
    public Long getUnreadCount(Integer userId) {
        return privateMessageMapper.countUnread(userId);
    }
}
