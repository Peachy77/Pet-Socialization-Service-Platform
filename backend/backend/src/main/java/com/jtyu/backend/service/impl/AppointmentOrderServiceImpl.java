package com.jtyu.backend.service.impl;

import com.jtyu.backend.mapper.AppointmentOrderMapper;
import com.jtyu.backend.model.AppointmentOrder;
import com.jtyu.backend.service.AppointmentOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AppointmentOrderServiceImpl implements AppointmentOrderService {
    @Autowired
    private AppointmentOrderMapper appointmentOrderMapper;

    @Override
    @Transactional
    public Integer createOrder(Integer userId, Integer serviceId, String projectName,
                               LocalDateTime appointmentTime, String remark, BigDecimal price) {
        AppointmentOrder order = new AppointmentOrder();
        order.setUserId(userId);
        order.setServiceId(serviceId);
        order.setProjectName(projectName);
        order.setAppointmentTime(appointmentTime);
        order.setRemark(remark);
        order.setPrice(price);
        order.setStatus("pending");

        int rows = appointmentOrderMapper.insert(order);
        if (rows > 0) {
            return order.getOrderId();
        }
        return null;
    }

    @Override
    public Map<String, Object> getOrderDetail(Integer orderId, Integer userId) {
        Map<String, Object> order = appointmentOrderMapper.selectById(orderId);
        if (order == null) {
            return null;
        }
        // 验证订单归属
        Integer orderUserId = (Integer) order.get("user_id");
        if (!orderUserId.equals(userId)) {
            return null;
        }
        return order;
    }

    @Override
    public Map<String, Object> getUserOrders(Integer userId, String status, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> list = appointmentOrderMapper.selectByUserId(userId, status, offset, pageSize);
        Long total = appointmentOrderMapper.countByUserId(userId, status);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }

    @Override
    @Transactional
    public boolean cancelOrder(Integer orderId, Integer userId) {
        return appointmentOrderMapper.deletePending(orderId, userId) > 0;
    }

    @Override
    @Transactional
    public boolean updateOrderStatus(Integer orderId, String status) {
        // 只允许更新为 confirmed, completed, cancelled
        if (!"confirmed".equals(status) && !"completed".equals(status) && !"cancelled".equals(status)) {
            return false;
        }
        return appointmentOrderMapper.updateStatus(orderId, status) > 0;
    }
}
