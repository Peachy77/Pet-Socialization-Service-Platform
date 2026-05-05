package com.jtyu.backend.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtyu.backend.service.CommentService;
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

@WebMvcTest(CommentController.class)
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetComments_Success() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", new ArrayList<>());
        mockResult.put("total", 5L);
        mockResult.put("page", 1);
        mockResult.put("pageSize", 20);

        when(commentService.getCommentsByPostId(eq(100), eq(1), eq(20), anyInt()))
                .thenReturn(mockResult);

        mockMvc.perform(get("/posts/100/comments")
                        .requestAttr("currentUserId", 1)
                        .param("page", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testGetComments_NoLogin() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", new ArrayList<>());
        mockResult.put("total", 3L);

        when(commentService.getCommentsByPostId(eq(100), eq(1), eq(20), isNull()))
                .thenReturn(mockResult);

        mockMvc.perform(get("/posts/100/comments")
                        .param("page", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testCreateComment_Success() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("content", "这是一条评论");
        params.put("images", Arrays.asList("img1.jpg"));

        when(commentService.createComment(anyInt(), anyInt(), any(), anyString(), anyList()))
                .thenReturn(100);

        mockMvc.perform(post("/posts/100/comments")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(100));
    }

    @Test
    void testCreateComment_WithParentId() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("content", "回复评论");
        params.put("parentCommentId", 50);

        when(commentService.createComment(anyInt(), anyInt(), eq(50), anyString(), anyList()))
                .thenReturn(101);

        mockMvc.perform(post("/posts/100/comments")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

//    @Test
//    void testCreateComment_Failed() throws Exception {
//        Map<String, Object> params = new HashMap<>();
//        params.put("content", "评论");
//
//        doReturn(null).when(commentService.createComment(anyInt(), anyInt(), any(), anyString(), anyList()));
//
//        mockMvc.perform(post("/posts/100/comments")
//                        .requestAttr("currentUserId", 1)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(params)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(0))
//                .andExpect(jsonPath("$.msg").value("评论失败"));
//    }

    @Test
    void testDeleteComment_Success() throws Exception {
        when(commentService.deleteComment(100, 1)).thenReturn(true);

        mockMvc.perform(delete("/posts/1/comments/100")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("删除成功"));
    }

    @Test
    void testDeleteComment_Failed() throws Exception {
        when(commentService.deleteComment(100, 1)).thenReturn(false);

        mockMvc.perform(delete("/posts/1/comments/100")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("删除失败"));
    }

    @Test
    void testLikeComment_Success() throws Exception {
        when(commentService.likeComment(100, 1)).thenReturn(true);

        mockMvc.perform(post("/posts/1/comments/100/like")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("点赞成功"));
    }

    @Test
    void testLikeComment_Failed() throws Exception {
        when(commentService.likeComment(100, 1)).thenReturn(false);

        mockMvc.perform(post("/posts/1/comments/100/like")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("点赞失败"));
    }

    @Test
    void testUnlikeComment_Success() throws Exception {
        when(commentService.unlikeComment(100, 1)).thenReturn(true);

        mockMvc.perform(delete("/posts/1/comments/100/like")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("取消点赞成功"));
    }

    @Test
    void testReplyComment_Success() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("content", "回复内容");

        when(commentService.createComment(anyInt(), anyInt(), eq(100), eq("回复内容"), isNull()))
                .thenReturn(200);

        mockMvc.perform(post("/posts/1/comments/100/replies")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(200));
    }

    @Test
    void testReplyComment_Failed() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("content", "回复内容");

        when(commentService.createComment(anyInt(), anyInt(), eq(100), eq("回复内容"), isNull()))
                .thenReturn(null);

        mockMvc.perform(post("/posts/1/comments/100/replies")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("回复失败"));
    }
}
