package com.jtyu.backend.controller;

import com.jtyu.backend.model.Order;
import com.jtyu.backend.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderController {
    @Autowired
    private OrderService orderService;

    // GET /api/orders - 获取订单列表
    @GetMapping("/api/orders")
    public Result getOrders() {
        List<Order> orders = orderService.getAllOrders();
        return Result.success(orders);
    }

    // POST /api/orders - 创建预约订单
    @PostMapping("/api/orders")
    public Result createOrder(@RequestBody Order order) {
        int result = orderService.createOrder(order);
        if (result > 0) {
            return Result.success("订单创建成功");
        }
        return Result.error("订单创建失败");
    }
}
