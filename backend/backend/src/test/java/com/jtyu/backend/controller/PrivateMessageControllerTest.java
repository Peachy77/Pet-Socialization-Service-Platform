package com.jtyu.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtyu.backend.service.PrivateMessageService;
import com.jtyu.backend.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PrivateMessageController.class)
public class PrivateMessageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PrivateMessageService privateMessageService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetConversationList_Success() throws Exception {
        List<Map<String, Object>> mockList = new ArrayList<>();
        Map<String, Object> conv = new HashMap<>();
        conv.put("userId", 2);
        conv.put("username", "对方");
        conv.put("lastMessage", "你好");
        mockList.add(conv);

        when(privateMessageService.getConversationList(1)).thenReturn(mockList);

        mockMvc.perform(get("/messages/conversations")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testGetConversationMessages_Success() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", new ArrayList<>());
        mockResult.put("total", 10L);
        mockResult.put("page", 1);
        mockResult.put("pageSize", 20);

        when(privateMessageService.getConversation(eq(1), eq(2), eq(1), eq(20)))
                .thenReturn(mockResult);

        mockMvc.perform(get("/messages/conversations/2")
                        .requestAttr("currentUserId", 1)
                        .param("page", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testSendMessage_Success() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("receiver_id", 2);
        params.put("content", "你好");
        params.put("images", Arrays.asList("img1.jpg"));

        when(privateMessageService.sendMessage(eq(1), eq(2), eq("你好"), anyList()))
                .thenReturn(100);

        mockMvc.perform(post("/messages")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(100));
    }

    @Test
    void testSendMessage_ContentOnly() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("receiver_id", 2);
        params.put("content", "你好");

        when(privateMessageService.sendMessage(eq(1), eq(2), eq("你好"), anyList()))
                .thenReturn(101);

        mockMvc.perform(post("/messages")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testSendMessage_MissingReceiver() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("content", "你好");

        mockMvc.perform(post("/messages")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("接收者ID不能为空"));
    }

    @Test
    void testSendMessage_EmptyContentAndImages() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("receiver_id", 2);

        mockMvc.perform(post("/messages")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("内容或图片不能为空"));
    }

//    @Test
//    void testSendMessage_Failed() throws Exception {
//        Map<String, Object> params = new HashMap<>();
//        params.put("receiver_id", 2);
//        params.put("content", "你好");
//
//        doReturn(null).when(privateMessageService.sendMessage(anyInt(), anyInt(), anyString(), anyList()));
//
//        mockMvc.perform(post("/messages")
//                        .requestAttr("currentUserId", 1)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(params)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(0))
//                .andExpect(jsonPath("$.msg").value("发送失败"));
//    }

    @Test
    void testMarkConversationAsRead_Success() throws Exception {
        when(privateMessageService.markConversationAsRead(1, 2)).thenReturn(true);

        mockMvc.perform(patch("/messages/conversations/2/read")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("标记已读成功"));
    }

    @Test
    void testMarkConversationAsRead_Failed() throws Exception {
        when(privateMessageService.markConversationAsRead(1, 2)).thenReturn(false);

        mockMvc.perform(patch("/messages/conversations/2/read")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("标记已读失败"));
    }

    @Test
    void testGetUnreadMessageCount_Success() throws Exception {
        when(privateMessageService.getUnreadCount(1)).thenReturn(5L);

        mockMvc.perform(get("/messages/unread-count")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(5));
    }
}
