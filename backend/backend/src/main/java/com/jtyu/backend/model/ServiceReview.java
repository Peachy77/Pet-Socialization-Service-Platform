package com.jtyu.backend.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ServiceReview {
    private Integer reviewId;
    private Integer userId;
    private Integer serviceId;
    private Double rating;
    private String content;
    private String images;      // 评论图片URL数组 JSON
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
