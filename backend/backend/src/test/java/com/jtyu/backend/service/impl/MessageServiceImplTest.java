package com.jtyu.backend.service.impl;
import com.jtyu.backend.mapper.MessageMapper;
import com.jtyu.backend.model.Message;
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
public class MessageServiceImplTest {

    @Mock private MessageMapper messageMapper;

    @InjectMocks
    private MessageServiceImpl messageService;

    private Map<String, Object> mockMessage;

    @BeforeEach
    void setUp() {
        mockMessage = new HashMap<>();
        mockMessage.put("message_id", 1);
        mockMessage.put("receiver_id", 1);
        mockMessage.put("sender_id", 2);
        mockMessage.put("content", "测试消息");
        mockMessage.put("is_read", false);
    }

    // ========== getMessages 测试 ==========
    @Test
    void testGetMessages_Success() {
        List<Map<String, Object>> mockList = new ArrayList<>();
        mockList.add(mockMessage);

        when(messageMapper.selectByReceiverId(eq(1), eq(0), eq(20))).thenReturn(mockList);

        Map<String, Object> result = messageService.getMessages(1, 1, 20);

        assertNotNull(result);
        assertEquals(1, result.get("total"));
        assertEquals(1, result.get("page"));
        assertEquals(20, result.get("pageSize"));
    }

    @Test
    void testGetMessages_EmptyList() {
        when(messageMapper.selectByReceiverId(eq(1), eq(0), eq(20))).thenReturn(new ArrayList<>());

        Map<String, Object> result = messageService.getMessages(1, 1, 20);

        assertNotNull(result);
        assertEquals(0, result.get("total"));
        assertTrue(((List) result.get("list")).isEmpty());
    }

    // ========== getUnreadCount 测试 ==========
    @Test
    void testGetUnreadCount() {
        when(messageMapper.countUnread(1)).thenReturn(5L);

        Long result = messageService.getUnreadCount(1);

        assertEquals(5L, result);
    }

    @Test
    void testGetUnreadCount_Zero() {
        when(messageMapper.countUnread(1)).thenReturn(0L);

        Long result = messageService.getUnreadCount(1);

        assertEquals(0L, result);
    }

    // ========== markAsRead 测试 ==========
    @Test
    void testMarkAsRead_Success() {
        when(messageMapper.markAsRead(100)).thenReturn(1);

        boolean result = messageService.markAsRead(100, 1);

        assertTrue(result);
    }

    @Test
    void testMarkAsRead_Failed() {
        when(messageMapper.markAsRead(100)).thenReturn(0);

        boolean result = messageService.markAsRead(100, 1);

        assertFalse(result);
    }

    // ========== markAllAsRead 测试 ==========
    @Test
    void testMarkAllAsRead_Success() {
        when(messageMapper.markAllAsRead(1)).thenReturn(3);

        boolean result = messageService.markAllAsRead(1);

        assertTrue(result);
    }

    @Test
    void testMarkAllAsRead_Failed() {
        when(messageMapper.markAllAsRead(1)).thenReturn(0);

        boolean result = messageService.markAllAsRead(1);

        assertFalse(result);
    }

    // ========== createMessage 测试 ==========
    @Test
    void testCreateMessage_Success() {
        when(messageMapper.insert(any(Message.class))).thenAnswer(invocation -> {
            Message msg = invocation.getArgument(0);
            msg.setMessageId(100);
            return 1;
        });

        Integer messageId = messageService.createMessage(2, 1, "system", "系统消息", 123);

        assertNotNull(messageId);
        assertEquals(100, messageId);
        verify(messageMapper, times(1)).insert(any(Message.class));
    }

    @Test
    void testCreateMessage_WithNullRelatedId() {
        when(messageMapper.insert(any(Message.class))).thenAnswer(invocation -> {
            Message msg = invocation.getArgument(0);
            msg.setMessageId(101);
            return 1;
        });

        Integer messageId = messageService.createMessage(2, 1, "text", "普通消息", null);

        assertNotNull(messageId);
    }

    @Test
    void testCreateMessage_Failed() {
        when(messageMapper.insert(any(Message.class))).thenReturn(0);

        Integer messageId = messageService.createMessage(2, 1, "text", "消息", null);

        assertNull(messageId);
    }

    // ========== deleteMessage 测试 ==========
    @Test
    void testDeleteMessage_Success() {
        when(messageMapper.deleteById(100)).thenReturn(1);

        boolean result = messageService.deleteMessage(100, 1);

        assertTrue(result);
    }

    @Test
    void testDeleteMessage_Failed() {
        when(messageMapper.deleteById(100)).thenReturn(0);

        boolean result = messageService.deleteMessage(100, 1);

        assertFalse(result);
    }
}
