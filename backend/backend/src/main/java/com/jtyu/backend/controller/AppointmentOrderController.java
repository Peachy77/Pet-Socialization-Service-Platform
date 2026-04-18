package com.jtyu.backend.controller;

import com.jtyu.backend.model.Result;
import com.jtyu.backend.service.AppointmentOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class AppointmentOrderController {
    @Autowired
    private AppointmentOrderService appointmentOrderService;

    // GET /orders - 获取订单列表
    @GetMapping("/orders")
    public Result getOrders(@RequestAttribute Integer currentUserId,
                            @RequestParam(required = false) String status,
                            @RequestParam(defaultValue = "1") Integer page,
                            @RequestParam(defaultValue = "20") Integer pageSize) {
        Map<String, Object> result = appointmentOrderService.getUserOrders(currentUserId, status, page, pageSize);
        return Result.success(result);
    }

    // POST /orders - 创建订单
    @PostMapping("/orders")
    public Result createOrder(@RequestAttribute Integer currentUserId,
                              @RequestBody Map<String, Object> params) {
        Integer serviceId = (Integer) params.get("service_id");
        String projectName = (String) params.get("project_name");
        String appointmentDate = (String) params.get("appointment_date");
        String appointmentTime = (String) params.get("appointment_time");
        String remark = (String) params.get("remark");
        BigDecimal price = params.get("price") != null ? new BigDecimal(params.get("price").toString()) : null;

        if (serviceId == null || projectName == null || appointmentDate == null) {
            return Result.error("服务ID、项目名称和预约日期不能为空");
        }

        // 拼接日期和时间
        LocalDateTime appointmentDateTime = LocalDateTime.parse(
                appointmentDate + "T" + (appointmentTime != null ? appointmentTime : "00:00:00")
        );

        Integer orderId = appointmentOrderService.createOrder(
                currentUserId, serviceId, projectName, appointmentDateTime, remark, price
        );

        if (orderId != null) {
            return Result.success(orderId);
        }
        return Result.error("创建订单失败");
    }

    // GET /orders/{orderId} - 获取订单详情
    @GetMapping("/orders/{orderId}")
    public Result getOrderDetail(@PathVariable Integer orderId,
                                 @RequestAttribute Integer currentUserId) {
        Map<String, Object> order = appointmentOrderService.getOrderDetail(orderId, currentUserId);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    // DELETE /orders/{orderId} - 取消订单
    @DeleteMapping("/orders/{orderId}")
    public Result cancelOrder(@PathVariable Integer orderId,
                              @RequestAttribute Integer currentUserId) {
        boolean success = appointmentOrderService.cancelOrder(orderId, currentUserId);
        if (success) {
            return Result.success("取消订单成功");
        }
        return Result.error("取消订单失败");
    }

    // PATCH /orders/{orderId}/status - 更新订单状态
    @PatchMapping("/orders/{orderId}/status")
    public Result updateOrderStatus(@PathVariable Integer orderId,
                                    @RequestBody Map<String, String> params) {
        String status = params.get("status");
        if (status == null) {
            return Result.error("状态不能为空");
        }

        boolean success = appointmentOrderService.updateOrderStatus(orderId, status);
        if (success) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }
}
