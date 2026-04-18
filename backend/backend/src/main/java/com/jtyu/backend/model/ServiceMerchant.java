package com.jtyu.backend.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ServiceMerchant {
    private Integer serviceId;
    private String name;
    private String category;           // grooming/walking/boarding/sitting/vet/emergency
    private String address;
    private String images;              // 商户图片URL数组 JSON
    private String phone;
    private Double rating;
    private Integer reviewCount;
    private String businessHours;       // 营业时间 JSON
    private String description;
    private String servicesOffered;     // 服务项目及价格 JSON
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
