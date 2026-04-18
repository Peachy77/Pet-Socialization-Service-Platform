package com.jtyu.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtyu.backend.mapper.ServiceMerchantMapper;
import com.jtyu.backend.mapper.ServiceReviewLikeMapper;
import com.jtyu.backend.mapper.ServiceReviewMapper;
import com.jtyu.backend.model.ServiceReview;
import com.jtyu.backend.service.ServiceReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServiceReviewServiceImpl implements ServiceReviewService {
    @Autowired
    private ServiceReviewMapper serviceReviewMapper;

    @Autowired
    private ServiceReviewLikeMapper serviceReviewLikeMapper;

    @Autowired
    private ServiceMerchantMapper serviceMerchantMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> getReviewsByServiceId(Integer serviceId, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> list = serviceReviewMapper.selectByServiceId(serviceId, offset, pageSize);
        Long total = serviceReviewMapper.countByServiceId(serviceId);
        // 解析 images JSON
        for (Map<String, Object> review : list) {
            String imagesStr = (String) review.get("images");
            if (imagesStr != null && !imagesStr.isEmpty()) {
                try {
                    review.put("images", objectMapper.readValue(imagesStr, List.class));
                } catch (Exception e) {
                    review.put("images", Collections.emptyList());
                }
            } else {
                review.put("images", Collections.emptyList());
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }

    @Override
    @Transactional
    public Integer createReview(Integer userId, Integer serviceId, Integer rating, List<String> images, String content) {
        ServiceReview review = new ServiceReview();
        review.setUserId(userId);
        review.setServiceId(serviceId);
        review.setRating(rating != null ? BigDecimal.valueOf(rating) : null);
        review.setContent(content);
        try {
            review.setImages(objectMapper.writeValueAsString(images != null ? images : Collections.emptyList()));
        } catch (Exception e) {
            review.setImages("[]");
        }

        int rows = serviceReviewMapper.insert(review);
        if (rows > 0) {
            // 更新商户评分
            Map<String, Object> stats = serviceReviewMapper.selectRatingStats(serviceId);
            BigDecimal avgRating = (BigDecimal) stats.get("avgRating");
            Long count = (Long) stats.get("count");
            serviceMerchantMapper.updateRating(serviceId, avgRating != null ? avgRating : BigDecimal.ZERO, count.intValue());
            return review.getReviewId();
        }
        return null;
    }

    @Override
    public boolean likeReview(Integer reviewId, Integer userId) {
        if (serviceReviewLikeMapper.exists(userId, reviewId) > 0) {
            return false;
        }
        return serviceReviewLikeMapper.insert(userId, reviewId) > 0;
    }

    @Override
    public boolean unlikeReview(Integer reviewId, Integer userId) {
        if (serviceReviewLikeMapper.exists(userId, reviewId) == 0) {
            return false;
        }
        return serviceReviewLikeMapper.delete(userId, reviewId) > 0;
    }

    @Override
    public List<Map<String, Object>> getRepliesByReviewId(Integer reviewId) {
        return serviceReviewMapper.selectRepliesByParentId(reviewId);
    }

    @Override
    @Transactional
    public Integer replyReview(Integer userId, Integer serviceId, Integer parentReviewId, String content) {
        // 检查父评论是否存在
        Map<String, Object> parentReview = serviceReviewMapper.selectById(parentReviewId);
        if (parentReview == null) {
            return null;
        }

        ServiceReview review = new ServiceReview();
        review.setUserId(userId);
        review.setServiceId(serviceId);
        review.setParentReviewId(parentReviewId);
        review.setContent(content);
        review.setRating(null);  // 回复不需要评分
        review.setImages("[]");

        int rows = serviceReviewMapper.insert(review);
        if (rows > 0) {
            return review.getReviewId();
        }
        return null;
    }
}
