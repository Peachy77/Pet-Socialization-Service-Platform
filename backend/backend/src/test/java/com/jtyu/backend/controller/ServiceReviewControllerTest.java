package com.jtyu.backend.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtyu.backend.service.ServiceReviewService;
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

@WebMvcTest(ServiceReviewController.class)
public class ServiceReviewControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceReviewService serviceReviewService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetServiceReviews_Success() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", new ArrayList<>());
        mockResult.put("total", 10L);
        mockResult.put("page", 1);
        mockResult.put("pageSize", 20);

        when(serviceReviewService.getReviewsByServiceId(eq(100), eq(1), eq(20), anyInt()))
                .thenReturn(mockResult);

        mockMvc.perform(get("/services/100/reviews")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testGetReviewReplies_Success() throws Exception {
        List<Map<String, Object>> mockReplies = new ArrayList<>();
        Map<String, Object> reply = new HashMap<>();
        reply.put("review_id", 200);
        reply.put("content", "回复内容");
        mockReplies.add(reply);

        when(serviceReviewService.getRepliesByReviewId(eq(50), anyInt())).thenReturn(mockReplies);

        mockMvc.perform(get("/services/100/reviews/50/replies")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testCreateServiceReview_Success() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("rating", 5);
        params.put("content", "服务很好！");
        params.put("images", Arrays.asList("img1.jpg"));

        when(serviceReviewService.createReview(anyInt(), anyInt(), anyInt(), anyList(), anyString()))
                .thenReturn(100);

        mockMvc.perform(post("/services/100/reviews")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(100));
    }

//    @Test
//    void testCreateServiceReview_Failed() throws Exception {
//        Map<String, Object> params = new HashMap<>();
//        params.put("rating", 5);
//        params.put("content", "服务很好！");
//
//        doReturn(null).when(serviceReviewService.createReview(anyInt(), anyInt(), anyInt(), anyList(), anyString()));
//
//        mockMvc.perform(post("/services/100/reviews")
//                        .requestAttr("currentUserId", 1)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(params)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(0))
//                .andExpect(jsonPath("$.msg").value("评论失败"));
//    }

    @Test
    void testReplyServiceReview_Success() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("content", "回复评论");
        params.put("images", Arrays.asList("img1.jpg"));

        when(serviceReviewService.replyReview(anyInt(), anyInt(), anyInt(), anyString(), anyList()))
                .thenReturn(200);

        mockMvc.perform(post("/services/100/reviews/50/replies")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(200));
    }

    @Test
    void testReplyServiceReview_EmptyContent() throws Exception {
        Map<String, Object> params = new HashMap<>();

        mockMvc.perform(post("/services/100/reviews/50/replies")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("回复内容不能为空"));
    }

//    @Test
//    void testReplyServiceReview_Failed() throws Exception {
//        Map<String, Object> params = new HashMap<>();
//        params.put("content", "回复");
//
//        when(serviceReviewService.replyReview(anyInt(), anyInt(), anyInt(), anyString(), anyList()))
//                .thenReturn(null);
//
//        mockMvc.perform(post("/services/100/reviews/50/replies")
//                        .requestAttr("currentUserId", 1)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(params)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(0))
//                .andExpect(jsonPath("$.msg").value("回复失败"));
//    }

    @Test
    void testLikeServiceReview_Success() throws Exception {
        when(serviceReviewService.likeReview(100, 1)).thenReturn(true);

        mockMvc.perform(post("/services/100/reviews/100/like")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("点赞成功"));
    }

    @Test
    void testUnlikeServiceReview_Success() throws Exception {
        when(serviceReviewService.unlikeReview(100, 1)).thenReturn(true);

        mockMvc.perform(delete("/services/100/reviews/100/like")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("取消点赞成功"));
    }

    @Test
    void testDeleteServiceReview_Success() throws Exception {
        when(serviceReviewService.deleteReview(100, 1)).thenReturn(true);

        mockMvc.perform(delete("/services/100/reviews/100")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("删除成功"));
    }

    @Test
    void testDeleteServiceReview_Failed() throws Exception {
        when(serviceReviewService.deleteReview(100, 1)).thenReturn(false);

        mockMvc.perform(delete("/services/100/reviews/100")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("删除失败"));
    }
}
