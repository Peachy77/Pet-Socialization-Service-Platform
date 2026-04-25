package com.jtyu.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtyu.backend.mapper.FavoriteMapper;
import com.jtyu.backend.mapper.ServiceMerchantMapper;
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
public class ServiceMerchantServiceImplTest {
    @Mock private ServiceMerchantMapper serviceMerchantMapper;
    @Mock private FavoriteMapper favoriteMapper;

    @InjectMocks
    private ServiceMerchantServiceImpl serviceMerchantService;

    private Map<String, Object> mockService;

    @BeforeEach
    void setUp() {
        mockService = new HashMap<>();
        mockService.put("service_id", 1);
        mockService.put("name", "宠物店");
        mockService.put("images", "[\"img1.jpg\"]");
        mockService.put("business_hours", "{\"monday\":\"9-18\"}");
        mockService.put("services_offered", "[\"洗澡\",\"美容\"]");
    }

    // ========== getServiceList 测试 ==========
    @Test
    void testGetServiceList_Success() throws Exception {
        List<Map<String, Object>> mockList = new ArrayList<>();
        mockList.add(mockService);

        when(serviceMerchantMapper.selectList("关键词", "grooming", 0, 20)).thenReturn(mockList);
        when(serviceMerchantMapper.countList("关键词", "grooming")).thenReturn(1L);

        Map<String, Object> result = serviceMerchantService.getServiceList("关键词", "grooming", 1, 20);

        assertNotNull(result);
        assertEquals(1L, result.get("total"));
    }

    @Test
    void testGetServiceList_NoKeyword() {
        List<Map<String, Object>> mockList = new ArrayList<>();
        mockList.add(mockService);

        when(serviceMerchantMapper.selectList(null, null, 0, 20)).thenReturn(mockList);
        when(serviceMerchantMapper.countList(null, null)).thenReturn(1L);

        Map<String, Object> result = serviceMerchantService.getServiceList(null, null, 1, 20);

        assertNotNull(result);
    }

    // ========== getServiceDetail 测试 ==========
    @Test
    void testGetServiceDetail_Success_WithLogin() throws Exception {
        when(serviceMerchantMapper.selectById(1)).thenReturn(mockService);
        when(favoriteMapper.exists(1, 1)).thenReturn(1);

        Map<String, Object> result = serviceMerchantService.getServiceDetail(1, 1);

        assertNotNull(result);
        assertTrue((Boolean) result.get("is_favorited"));
    }

    @Test
    void testGetServiceDetail_Success_NoLogin() throws Exception {
        when(serviceMerchantMapper.selectById(1)).thenReturn(mockService);

        Map<String, Object> result = serviceMerchantService.getServiceDetail(1, null);

        assertNotNull(result);
        assertFalse((Boolean) result.get("is_favorited"));
    }

    @Test
    void testGetServiceDetail_NotFound() {
        when(serviceMerchantMapper.selectById(999)).thenReturn(null);

        Map<String, Object> result = serviceMerchantService.getServiceDetail(999, 1);

        assertNull(result);
    }

    // ========== isFavorited 测试 ==========
    @Test
    void testIsFavorited_True() {
        when(favoriteMapper.exists(1, 100)).thenReturn(1);

        boolean result = serviceMerchantService.isFavorited(1, 100);

        assertTrue(result);
    }

    @Test
    void testIsFavorited_False() {
        when(favoriteMapper.exists(1, 100)).thenReturn(0);

        boolean result = serviceMerchantService.isFavorited(1, 100);

        assertFalse(result);
    }
}
