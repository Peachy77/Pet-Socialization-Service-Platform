package com.jtyu.backend.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtyu.backend.service.PostService;
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

@WebMvcTest(PostController.class)
public class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetPosts_Success() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", new ArrayList<>());
        mockResult.put("total", 10L);
        mockResult.put("page", 1);
        mockResult.put("pageSize", 20);

        when(postService.getPostList(isNull(), isNull(), eq(1), eq(20), any()))
                .thenReturn(mockResult);

        mockMvc.perform(get("/posts")
                        .param("page", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testGetPosts_WithKeywordAndTag() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", new ArrayList<>());
        mockResult.put("total", 5L);

        when(postService.getPostList(eq("宠物"), eq("萌宠"), eq(1), eq(10), any()))
                .thenReturn(mockResult);

        mockMvc.perform(get("/posts")
                        .param("page", "1")
                        .param("pageSize", "10")
                        .param("keyword", "宠物")
                        .param("tag", "萌宠"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testGetPostDetail_Success() throws Exception {
        Map<String, Object> mockPost = new HashMap<>();
        mockPost.put("post_id", 100);
        mockPost.put("content", "测试内容");

        when(postService.getPostDetail(100, 1)).thenReturn(mockPost);

        mockMvc.perform(get("/posts/100")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testGetPostDetail_NotFound() throws Exception {
        when(postService.getPostDetail(999, 1)).thenReturn(null);

        mockMvc.perform(get("/posts/999")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("动态不存在"));
    }

    @Test
    void testCreatePost_Success() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("content", "新动态");
        params.put("images", Arrays.asList("img1.jpg"));
        params.put("tags", Arrays.asList("宠物"));

        when(postService.createPost(anyInt(), anyString(), anyList(), anyList()))
                .thenReturn(100);

        mockMvc.perform(post("/posts")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(100));
    }

//    @Test
//    void testCreatePost_Failed() throws Exception {
//        Map<String, Object> params = new HashMap<>();
//        params.put("content", "新动态");
//
//        doReturn(null).when(postService.createPost(anyInt(), anyString(), anyList(), anyList()));
//
//        mockMvc.perform(post("/posts")
//                        .requestAttr("currentUserId", 1)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(params)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(0))
//                .andExpect(jsonPath("$.msg").value("发布失败"));
//    }

    @Test
    void testDeletePost_Success() throws Exception {
        when(postService.deletePost(100, 1)).thenReturn(true);

        mockMvc.perform(delete("/posts/100")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("删除成功"));
    }

    @Test
    void testDeletePost_Failed() throws Exception {
        when(postService.deletePost(100, 1)).thenReturn(false);

        mockMvc.perform(delete("/posts/100")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("删除失败"));
    }

    @Test
    void testLikePost_Success() throws Exception {
        when(postService.likePost(100, 1)).thenReturn(true);

        mockMvc.perform(post("/posts/100/like")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("点赞成功"));
    }

    @Test
    void testLikePost_Failed() throws Exception {
        when(postService.likePost(100, 1)).thenReturn(false);

        mockMvc.perform(post("/posts/100/like")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("点赞失败"));
    }

    @Test
    void testUnlikePost_Success() throws Exception {
        when(postService.unlikePost(100, 1)).thenReturn(true);

        mockMvc.perform(delete("/posts/100/like")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("取消点赞成功"));
    }

    @Test
    void testUnlikePost_Failed() throws Exception {
        when(postService.unlikePost(100, 1)).thenReturn(false);

        mockMvc.perform(delete("/posts/100/like")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("取消点赞失败"));
    }

}
