package com.jtyu.backend.model;

import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;//用于处理高精度数值运算

@Data
public class Order {
    private Integer orderId;
    private Integer userId;
    private Integer serviceId;
    private LocalDateTime appointmentTime;
    private String remark;
    private String status;      // pending/confirmed/completed/cancelled
    private BigDecimal price;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
