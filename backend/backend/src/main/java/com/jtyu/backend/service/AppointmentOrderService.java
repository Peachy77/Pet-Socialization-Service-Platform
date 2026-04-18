package com.jtyu.backend.service;

import com.jtyu.backend.model.AppointmentOrder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AppointmentOrderService {
    // 创建订单
    Integer createOrder(Integer userId, Integer serviceId, String projectName,
                        LocalDateTime appointmentTime, String remark, java.math.BigDecimal price);

    // 获取订单详情
    Map<String, Object> getOrderDetail(Integer orderId, Integer userId);

    // 获取用户订单列表
    Map<String, Object> getUserOrders(Integer userId, String status, Integer page, Integer pageSize);

    // 取消订单（仅pending状态可取消）
    boolean cancelOrder(Integer orderId, Integer userId);

    // 更新订单状态（商户端）
    boolean updateOrderStatus(Integer orderId, String status);
}
