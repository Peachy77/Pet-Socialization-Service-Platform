package com.jtyu.backend.service;

import com.jtyu.backend.model.Order;

import java.util.List;

public interface OrderService {
    List<Order> getAllOrders();
    int createOrder(Order order);
}
