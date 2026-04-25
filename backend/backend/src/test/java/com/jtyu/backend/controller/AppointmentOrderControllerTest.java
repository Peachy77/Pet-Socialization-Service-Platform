package com.jtyu.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtyu.backend.service.AppointmentOrderService;
import com.jtyu.backend.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentOrderController.class)
public class AppointmentOrderControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentOrderService appointmentOrderService;

    @MockBean
    private JwtUtil jwtUtil;  // Mock JWT工具类

    @Autowired
    private ObjectMapper objectMapper;

    // ========== 创建订单测试 ==========
    @Test
    void testCreateOrder_Success() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("service_id", 10);
        params.put("project_name", "宠物美容");
        params.put("appointment_date", "2026-05-01");
        params.put("appointment_time", "14:30:00");
        params.put("remark", "备注信息");
        params.put("price", 199.00);

        when(appointmentOrderService.createOrder(anyInt(), anyInt(), anyString(),
                any(LocalDateTime.class), anyString(), any(BigDecimal.class))).thenReturn(100);

        mockMvc.perform(post("/orders")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").value(100));
    }

    @Test
    void testCreateOrder_MissingRequiredFields() throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("service_id", 10);

        mockMvc.perform(post("/orders")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("服务ID、项目名称和预约日期不能为空"));
    }

//    @Test
//    void testCreateOrder_ServiceFailed() throws Exception {
//        Map<String, Object> params = new HashMap<>();
//        params.put("service_id", 10);
//        params.put("project_name", "宠物美容");
//        params.put("appointment_date", "2026-05-01");
//
//        when(appointmentOrderService.createOrder(anyInt(), anyInt(), anyString(),
//                any(LocalDateTime.class), anyString(), any(BigDecimal.class))).thenReturn(null);
//
//        mockMvc.perform(post("/orders")
//                        .requestAttr("currentUserId", 1)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(params)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(0))
//                .andExpect(jsonPath("$.msg").value("创建订单失败"));
//    }

    // ========== 获取订单列表测试 ==========
    @Test
    void testGetOrders_Success() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", new java.util.ArrayList<>());
        mockResult.put("total", 5L);
        mockResult.put("page", 1);
        mockResult.put("pageSize", 20);

        when(appointmentOrderService.getUserOrders(anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(mockResult);

        mockMvc.perform(get("/orders")
                        .requestAttr("currentUserId", 1)
                        .param("page", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testGetOrders_WithStatusFilter() throws Exception {
        Map<String, Object> mockResult = new HashMap<>();
        mockResult.put("list", new java.util.ArrayList<>());
        mockResult.put("total", 2L);

        when(appointmentOrderService.getUserOrders(eq(1), eq("pending"), eq(1), eq(20)))
                .thenReturn(mockResult);

        mockMvc.perform(get("/orders")
                        .requestAttr("currentUserId", 1)
                        .param("status", "pending")
                        .param("page", "1")
                        .param("pageSize", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    // ========== 获取订单详情测试 ==========
    @Test
    void testGetOrderDetail_Success() throws Exception {
        Map<String, Object> mockOrder = new HashMap<>();
        mockOrder.put("order_id", 100);
        mockOrder.put("status", "pending");

        when(appointmentOrderService.getOrderDetail(100, 1)).thenReturn(mockOrder);

        mockMvc.perform(get("/orders/100")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testGetOrderDetail_NotFound() throws Exception {
        when(appointmentOrderService.getOrderDetail(999, 1)).thenReturn(null);

        mockMvc.perform(get("/orders/999")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("订单不存在"));
    }

    // ========== 取消订单测试 ==========
    @Test
    void testCancelOrder_Success() throws Exception {
        when(appointmentOrderService.cancelOrder(100, 1)).thenReturn(true);

        mockMvc.perform(delete("/orders/100")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("取消订单成功"));
    }

    @Test
    void testCancelOrder_Failed() throws Exception {
        when(appointmentOrderService.cancelOrder(100, 1)).thenReturn(false);

        mockMvc.perform(delete("/orders/100")
                        .requestAttr("currentUserId", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("取消订单失败"));
    }

    // ========== 更新订单状态测试 ==========
    @Test
    void testUpdateOrderStatus_Success() throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("status", "confirmed");

        when(appointmentOrderService.updateOrderStatus(100, "confirmed")).thenReturn(true);

        mockMvc.perform(patch("/orders/100/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("更新成功"));
    }

    @Test
    void testUpdateOrderStatus_MissingStatus() throws Exception {
        Map<String, String> params = new HashMap<>();

        mockMvc.perform(patch("/orders/100/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("状态不能为空"));
    }
}
