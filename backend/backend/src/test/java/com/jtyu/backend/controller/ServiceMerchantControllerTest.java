package com.jtyu.backend.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtyu.backend.service.ServiceMerchantService;
import com.jtyu.backend.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ServiceMerchantController.class)
public class ServiceMerchantControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServiceMerchantService serviceMerchantService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void testGetServices_Success() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", new java.util.ArrayList<>());
        mockResult.put("total", 10L);
        mockResult.put("page", 1);
        mockResult.put("pageSize", 20);

        when(serviceMerchantService.getServiceList(any(), any(), anyInt(), anyInt()))
                .thenReturn(mockResult);

        mockMvc.perform(get("/services")
                        .param("page", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testGetServices_WithKeywordAndType() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", new java.util.ArrayList<>());
        mockResult.put("total", 5L);

        when(serviceMerchantService.getServiceList(eq("宠物"), eq("美容"), anyInt(), anyInt()))
                .thenReturn(mockResult);

        mockMvc.perform(get("/services")
                        .param("page", "1")
                        .param("pageSize", "10")
                        .param("keyword", "宠物")
                        .param("type", "美容"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testGetServiceDetail_Success() throws Exception {
        Map<String, Object> mockService = new HashMap<>();
        mockService.put("service_id", 100);
        mockService.put("name", "宠物店");

        when(serviceMerchantService.getServiceDetail(100, 1)).thenReturn(mockService);

        mockMvc.perform(get("/services/100")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testGetServiceDetail_NoLogin() throws Exception {
        Map<String, Object> mockService = new HashMap<>();
        mockService.put("service_id", 100);
        mockService.put("name", "宠物店");
        mockService.put("is_favorited", false);

        when(serviceMerchantService.getServiceDetail(eq(100), isNull())).thenReturn(mockService);

        mockMvc.perform(get("/services/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testGetServiceDetail_NotFound() throws Exception {
        when(serviceMerchantService.getServiceDetail(999, 1)).thenReturn(null);

        mockMvc.perform(get("/services/999")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("商户不存在"));
    }
}
