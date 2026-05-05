package com.jtyu.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtyu.backend.service.MessageService;
import com.jtyu.backend.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetMessages_Success() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", new java.util.ArrayList<>());
        mockResult.put("total", 5L);
        mockResult.put("page", 1);
        mockResult.put("pageSize", 20);

        when(messageService.getMessages(anyInt(), anyInt(), anyInt())).thenReturn(mockResult);

        mockMvc.perform(get("/messages")
                        .requestAttr("currentUserId", 1)
                        .param("page", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testMarkAsRead_Success() throws Exception {
        when(messageService.markAsRead(100, 1)).thenReturn(true);

        mockMvc.perform(patch("/messages/100/read")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("标记已读成功"));
    }

    @Test
    void testMarkAsRead_Failed() throws Exception {
        when(messageService.markAsRead(100, 1)).thenReturn(false);

        mockMvc.perform(patch("/messages/100/read")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("标记已读失败"));
    }

    @Test
    void testMarkAllAsRead_Success() throws Exception {
        when(messageService.markAllAsRead(1)).thenReturn(true);

        mockMvc.perform(patch("/messages/read-all")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("标记已读成功"));
    }

    @Test
    void testMarkAllAsRead_Failed() throws Exception {
        when(messageService.markAllAsRead(1)).thenReturn(false);

        mockMvc.perform(patch("/messages/read-all")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("标记已读失败"));
    }
}
