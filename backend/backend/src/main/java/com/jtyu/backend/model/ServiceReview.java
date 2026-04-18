package com.jtyu.backend.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServiceReview {
    private Integer reviewId;
    private Integer userId;
    private Integer serviceId;
    private Integer parentReviewId;  // 新增：父评论ID，0表示一级评论
    private BigDecimal rating;
    private String content;
    private String images;      // 评论图片URL数组 JSON
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
