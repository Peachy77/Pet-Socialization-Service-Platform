package com.jtyu.backend.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtyu.backend.mapper.PrivateMessageMapper;
import com.jtyu.backend.model.PrivateMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PrivateMessageServiceImplTest {
    @Mock private PrivateMessageMapper privateMessageMapper;

    @InjectMocks
    private PrivateMessageServiceImpl privateMessageService;

    private List<String> mockImages;

    @BeforeEach
    void setUp() {
        mockImages = Arrays.asList("img1.jpg", "img2.jpg");
    }

    // ========== sendMessage 测试 ==========
    @Test
    void testSendMessage_Success() {
        when(privateMessageMapper.insert(any(PrivateMessage.class))).thenAnswer(invocation -> {
            PrivateMessage msg = invocation.getArgument(0);
            msg.setMessageId(100);
            return 1;
        });

        Integer messageId = privateMessageService.sendMessage(1, 2, "你好", mockImages);

        assertNotNull(messageId);
        assertEquals(100, messageId);
    }

    @Test
    void testSendMessage_NoImages() {
        when(privateMessageMapper.insert(any(PrivateMessage.class))).thenAnswer(invocation -> {
            PrivateMessage msg = invocation.getArgument(0);
            msg.setMessageId(101);
            return 1;
        });

        Integer messageId = privateMessageService.sendMessage(1, 2, "只有文字", null);

        assertNotNull(messageId);
    }

    @Test
    void testSendMessage_Failed() {
        when(privateMessageMapper.insert(any(PrivateMessage.class))).thenReturn(0);

        Integer messageId = privateMessageService.sendMessage(1, 2, "消息", null);

        assertNull(messageId);
    }

    // ========== getConversationList 测试 ==========
    @Test
    void testGetConversationList_Success() {
        List<Map<String, Object>> mockConversations = new ArrayList<>();
        Map<String, Object> conv = new HashMap<>();
        conv.put("userId", 2);
        conv.put("username", "other");
        conv.put("lastMessage", "hello");
        mockConversations.add(conv);

        List<Map<String, Object>> unreadGroup = new ArrayList<>();
        Map<String, Object> unread = new HashMap<>();
        unread.put("sender_id", 2);
        unread.put("unreadCount", 3L);
        unreadGroup.add(unread);

        when(privateMessageMapper.selectConversationList(1)).thenReturn(mockConversations);
        when(privateMessageMapper.selectUnreadGroupBySender(1)).thenReturn(unreadGroup);

        List<Map<String, Object>> result = privateMessageService.getConversationList(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(3L, result.get(0).get("unreadCount"));
    }

    @Test
    void testGetConversationList_Empty() {
        when(privateMessageMapper.selectConversationList(1)).thenReturn(new ArrayList<>());
        when(privateMessageMapper.selectUnreadGroupBySender(1)).thenReturn(new ArrayList<>());

        List<Map<String, Object>> result = privateMessageService.getConversationList(1);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ========== getConversation 测试 ==========
    @Test
    void testGetConversation_Success() throws Exception {
        List<Map<String, Object>> mockMessages = new ArrayList<>();
        Map<String, Object> msg = new HashMap<>();
        msg.put("message_id", 1);
        msg.put("content", "hello");
        msg.put("images", "[\"img1.jpg\"]");
        mockMessages.add(msg);

        when(privateMessageMapper.selectConversation(1, 2, 0, 20)).thenReturn(mockMessages);
        when(privateMessageMapper.countConversation(1, 2)).thenReturn(1L);

        Map<String, Object> result = privateMessageService.getConversation(1, 2, 1, 20);

        assertNotNull(result);
        assertEquals(1L, result.get("total"));
    }

    // ========== markConversationAsRead 测试 ==========
    @Test
    void testMarkConversationAsRead_Success() {
        when(privateMessageMapper.markAsRead(2, 1)).thenReturn(1);

        boolean result = privateMessageService.markConversationAsRead(1, 2);

        assertTrue(result);
    }

    @Test
    void testMarkConversationAsRead_Failed() {
        when(privateMessageMapper.markAsRead(2, 1)).thenReturn(0);

        boolean result = privateMessageService.markConversationAsRead(1, 2);

        assertFalse(result);
    }

    // ========== getUnreadCount 测试 ==========
    @Test
    void testGetUnreadCount() {
        when(privateMessageMapper.countUnread(1)).thenReturn(5L);

        Long result = privateMessageService.getUnreadCount(1);

        assertEquals(5L, result);
    }
}
