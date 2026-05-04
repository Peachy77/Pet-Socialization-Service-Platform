package com.jtyu.backend.service;

import com.jtyu.backend.model.ServiceReview;

import java.util.List;
import java.util.Map;

public interface ServiceReviewService {

    // 获取商户评论列表
    Map<String, Object> getReviewsByServiceId(Integer serviceId, Integer page, Integer pageSize, Integer currentUserId);
    // 获取商户评论的回复列表（新增）
    List<Map<String, Object>> getRepliesByReviewId(Integer reviewId, Integer currentUserId);
    // 发表评论（参数顺序：userId, serviceId, rating, content, images）
    Integer createReview(Integer userId, Integer serviceId, Integer rating, List<String> images,String content);
    // 回复商户评论（新增）
    Integer replyReview(Integer userId, Integer serviceId, Integer parentReviewId, String content,List<String> images);
    // 点赞评论
    boolean likeReview(Integer reviewId, Integer userId);

    // 取消点赞评论
    boolean unlikeReview(Integer reviewId, Integer userId);

    // 删除评论
    boolean deleteReview(Integer reviewId, Integer currentUserId);
}
