package com.jtyu.backend.service.impl;

import com.jtyu.backend.mapper.AppointmentOrderMapper;
import com.jtyu.backend.model.AppointmentOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentOrderServiceImplTest {
    @Mock private AppointmentOrderMapper appointmentOrderMapper;

    @InjectMocks
    private AppointmentOrderServiceImpl orderService;

    @Test
    void testCreateOrder_Success() {
        when(appointmentOrderMapper.insert(any(AppointmentOrder.class))).thenAnswer(invocation -> {
            AppointmentOrder order = invocation.getArgument(0);
            order.setOrderId(100);
            return 1;
        });

        Integer orderId = orderService.createOrder(1, 10, "宠物美容",
                LocalDateTime.now().plusDays(1), "备注", new BigDecimal("199.00"));

        assertNotNull(orderId);
        assertEquals(100, orderId);
        verify(appointmentOrderMapper, times(1)).insert(any(AppointmentOrder.class));
    }

    @Test
    void testCreateOrder_Failed() {
        when(appointmentOrderMapper.insert(any(AppointmentOrder.class))).thenReturn(0);

        Integer orderId = orderService.createOrder(1, 10, "宠物美容",
                LocalDateTime.now().plusDays(1), "备注", new BigDecimal("199.00"));

        assertNull(orderId);
    }

    @Test
    void testGetOrderDetail_Success() {
        Map<String, Object> mockOrder = new HashMap<>();
        mockOrder.put("orderId", 100);
        mockOrder.put("userId", 1);
        mockOrder.put("price", new BigDecimal("199.00"));

        when(appointmentOrderMapper.selectById(100)).thenReturn(mockOrder);

        Map<String, Object> result = orderService.getOrderDetail(100, 1);

        assertNotNull(result);
        assertEquals(100, result.get("orderId"));
    }

    @Test
    void testGetOrderDetail_NotBelongToUser() {
        Map<String, Object> mockOrder = new HashMap<>();
        mockOrder.put("orderId", 100);
        mockOrder.put("userId", 2); // 订单属于用户2

        when(appointmentOrderMapper.selectById(100)).thenReturn(mockOrder);

        Map<String, Object> result = orderService.getOrderDetail(100, 1); // 当前用户1

        assertNull(result);
    }

    @Test
    void testGetOrderDetail_NotFound() {
        when(appointmentOrderMapper.selectById(999)).thenReturn(null);

        Map<String, Object> result = orderService.getOrderDetail(999, 1);

        assertNull(result);
    }

    @Test
    void testCancelOrder_Success() {
        when(appointmentOrderMapper.deletePending(100, 1)).thenReturn(1);

        boolean result = orderService.cancelOrder(100, 1);

        assertTrue(result);
        verify(appointmentOrderMapper, times(1)).deletePending(100, 1);
    }

    @Test
    void testCancelOrder_Failed() {
        when(appointmentOrderMapper.deletePending(100, 1)).thenReturn(0);

        boolean result = orderService.cancelOrder(100, 1);

        assertFalse(result);
    }

    @Test
    void testUpdateOrderStatus_Success() {
        when(appointmentOrderMapper.updateStatus(100, "confirmed")).thenReturn(1);

        boolean result = orderService.updateOrderStatus(100, "confirmed");

        assertTrue(result);
    }

    @Test
    void testUpdateOrderStatus_InvalidStatus() {
        boolean result = orderService.updateOrderStatus(100, "invalid");

        assertFalse(result);
        verify(appointmentOrderMapper, never()).updateStatus(anyInt(), anyString());
    }

    @Test
    void testUpdateOrderStatus_Failed() {
        when(appointmentOrderMapper.updateStatus(100, "completed")).thenReturn(0);

        boolean result = orderService.updateOrderStatus(100, "completed");

        assertFalse(result);
    }
}
