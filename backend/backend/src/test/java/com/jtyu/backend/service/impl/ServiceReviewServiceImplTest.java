package com.jtyu.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtyu.backend.mapper.ServiceMerchantMapper;
import com.jtyu.backend.mapper.ServiceReviewLikeMapper;
import com.jtyu.backend.mapper.ServiceReviewMapper;
import com.jtyu.backend.model.ServiceReview;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceReviewServiceImplTest {

    @Mock private ServiceReviewMapper serviceReviewMapper;
    @Mock private ServiceReviewLikeMapper serviceReviewLikeMapper;
    @Mock private ServiceMerchantMapper serviceMerchantMapper;

    @InjectMocks
    private ServiceReviewServiceImpl serviceReviewService;

    private Map<String, Object> mockReview;
    private List<String> mockImages;

    @BeforeEach
    void setUp() {
        mockReview = new HashMap<>();
        mockReview.put("review_id", 1);
        mockReview.put("user_id", 1);
        mockReview.put("service_id", 100);
        mockReview.put("content", "好评！");
        mockReview.put("rating", 5);
        mockReview.put("images", "[\"img1.jpg\"]");

        mockImages = Arrays.asList("img1.jpg", "img2.jpg");
    }

    // ========== getReviewsByServiceId 测试 ==========
    @Test
    void testGetReviewsByServiceId_Success() throws Exception {
        List<Map<String, Object>> mockList = new ArrayList<>();
        mockList.add(mockReview);

        when(serviceReviewMapper.selectRootByServiceId(eq(100), eq(0), eq(20), anyInt()))
                .thenReturn(mockList);
        when(serviceReviewMapper.countRootByServiceId(100)).thenReturn(1L);
        when(serviceReviewMapper.selectRepliesByParentId(eq(1), anyInt()))
                .thenReturn(new ArrayList<>());

        Map<String, Object> result = serviceReviewService.getReviewsByServiceId(100, 1, 20, 1);

        assertNotNull(result);
        assertEquals(1L, result.get("total"));
    }

    @Test
    void testGetReviewsByServiceId_NoLogin() {
        List<Map<String, Object>> mockList = new ArrayList<>();
        mockList.add(mockReview);

        when(serviceReviewMapper.selectRootByServiceId(eq(100), eq(0), eq(20), isNull()))
                .thenReturn(mockList);
        when(serviceReviewMapper.countRootByServiceId(100)).thenReturn(1L);
        when(serviceReviewMapper.selectRepliesByParentId(eq(1), isNull()))
                .thenReturn(new ArrayList<>());

        Map<String, Object> result = serviceReviewService.getReviewsByServiceId(100, 1, 20, null);

        assertNotNull(result);
        assertEquals(1L, result.get("total"));
        verify(serviceReviewMapper, times(1)).selectRootByServiceId(eq(100), eq(0), eq(20), isNull());
    }

    // ========== createReview 测试 ==========
    @Test
    void testCreateReview_Success() {
        when(serviceReviewMapper.insert(any(ServiceReview.class))).thenAnswer(invocation -> {
            ServiceReview r = invocation.getArgument(0);
            r.setReviewId(100);
            return 1;
        });

        Map<String, Object> stats = new HashMap<>();
        stats.put("avgRating", new BigDecimal("4.5"));
        stats.put("count", 10L);
        when(serviceReviewMapper.selectRatingStats(100)).thenReturn(stats);
        when(serviceMerchantMapper.updateRating(eq(100), any(BigDecimal.class), eq(10))).thenReturn(1);

        Integer reviewId = serviceReviewService.createReview(1, 100, 5, mockImages, "服务很好");

        assertNotNull(reviewId);
        assertEquals(100, reviewId);
        verify(serviceReviewMapper, times(1)).insert(any(ServiceReview.class));
    }

    @Test
    void testCreateReview_NoImages() {
        when(serviceReviewMapper.insert(any(ServiceReview.class))).thenAnswer(invocation -> {
            ServiceReview r = invocation.getArgument(0);
            r.setReviewId(101);
            return 1;
        });

        Map<String, Object> stats = new HashMap<>();
        stats.put("avgRating", new BigDecimal("4.0"));
        stats.put("count", 5L);
        when(serviceReviewMapper.selectRatingStats(100)).thenReturn(stats);
        when(serviceMerchantMapper.updateRating(eq(100), any(BigDecimal.class), eq(5))).thenReturn(1);

        Integer reviewId = serviceReviewService.createReview(1, 100, 4, null, "无图片评论");

        assertNotNull(reviewId);
    }

    @Test
    void testCreateReview_Failed() {
        when(serviceReviewMapper.insert(any(ServiceReview.class))).thenReturn(0);

        Integer reviewId = serviceReviewService.createReview(1, 100, 5, null, "评论");

        assertNull(reviewId);
        verify(serviceMerchantMapper, never()).updateRating(anyInt(), any(), anyInt());
    }

    // ========== likeReview 测试 ==========
    @Test
    void testLikeReview_Success() {
        when(serviceReviewLikeMapper.exists(1, 100)).thenReturn(0);
        when(serviceReviewLikeMapper.insert(1, 100)).thenReturn(1);

        boolean result = serviceReviewService.likeReview(100, 1);

        assertTrue(result);
    }

    @Test
    void testLikeReview_AlreadyLiked() {
        when(serviceReviewLikeMapper.exists(1, 100)).thenReturn(1);

        boolean result = serviceReviewService.likeReview(100, 1);

        assertFalse(result);
        verify(serviceReviewLikeMapper, never()).insert(anyInt(), anyInt());
    }

    // ========== unlikeReview 测试 ==========
    @Test
    void testUnlikeReview_Success() {
        when(serviceReviewLikeMapper.exists(1, 100)).thenReturn(1);
        when(serviceReviewLikeMapper.delete(1, 100)).thenReturn(1);

        boolean result = serviceReviewService.unlikeReview(100, 1);

        assertTrue(result);
    }

    @Test
    void testUnlikeReview_NotLiked() {
        when(serviceReviewLikeMapper.exists(1, 100)).thenReturn(0);

        boolean result = serviceReviewService.unlikeReview(100, 1);

        assertFalse(result);
        verify(serviceReviewLikeMapper, never()).delete(anyInt(), anyInt());
    }

    // ========== getRepliesByReviewId 测试 ==========
    @Test
    void testGetRepliesByReviewId_Success() {
        List<Map<String, Object>> mockReplies = new ArrayList<>();
        mockReplies.add(mockReview);

        when(serviceReviewMapper.selectRepliesByParentId(eq(1), eq(1))).thenReturn(mockReplies);

        List<Map<String, Object>> result = serviceReviewService.getRepliesByReviewId(1, 1);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ========== replyReview 测试 ==========
    @Test
    void testReplyReview_Success() {
        when(serviceReviewMapper.selectById(1)).thenReturn(mockReview);
        when(serviceReviewMapper.insert(any(ServiceReview.class))).thenAnswer(invocation -> {
            ServiceReview r = invocation.getArgument(0);
            r.setReviewId(200);
            return 1;
        });

        Integer result = serviceReviewService.replyReview(2, 100, 1, "回复评论", null);

        assertNotNull(result);
        assertEquals(200, result);
    }

    @Test
    void testReplyReview_ParentNotFound() {
        when(serviceReviewMapper.selectById(999)).thenReturn(null);

        Integer result = serviceReviewService.replyReview(2, 100, 999, "回复", null);

        assertNull(result);
        verify(serviceReviewMapper, never()).insert(any());
    }

    @Test
    void testReplyReview_InsertFailed() {
        when(serviceReviewMapper.selectById(1)).thenReturn(mockReview);
        when(serviceReviewMapper.insert(any(ServiceReview.class))).thenReturn(0);

        Integer result = serviceReviewService.replyReview(2, 100, 1, "回复", null);

        assertNull(result);
    }

    // ========== deleteReview 测试 ==========
    @Test
    void testDeleteReview_Success() {
        when(serviceReviewMapper.selectUserIdByReviewId(100)).thenReturn(1);
        when(serviceReviewMapper.selectById(100)).thenReturn(mockReview);
        when(serviceReviewMapper.deleteById(100)).thenReturn(1);

        Map<String, Object> stats = new HashMap<>();
        stats.put("avgRating", new BigDecimal("4.2"));
        stats.put("count", 9L);
        when(serviceReviewMapper.selectRatingStats(100)).thenReturn(stats);
        when(serviceMerchantMapper.updateRating(eq(100), any(BigDecimal.class), eq(9))).thenReturn(1);

        boolean result = serviceReviewService.deleteReview(100, 1);

        assertTrue(result);
    }

    @Test
    void testDeleteReview_NotOwner() {
        when(serviceReviewMapper.selectUserIdByReviewId(100)).thenReturn(2);

        boolean result = serviceReviewService.deleteReview(100, 1);

        assertFalse(result);
        verify(serviceReviewMapper, never()).deleteById(anyInt());
    }

    @Test
    void testDeleteReview_NotFound() {
        when(serviceReviewMapper.selectUserIdByReviewId(999)).thenReturn(null);

        boolean result = serviceReviewService.deleteReview(999, 1);

        assertFalse(result);
    }
}
