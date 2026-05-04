package com.jtyu.backend.controller;

import com.jtyu.backend.model.Result;
import com.jtyu.backend.model.ServiceReview;
import com.jtyu.backend.service.ServiceReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ServiceReviewController {
    @Autowired
    private ServiceReviewService serviceReviewService;

    // GET /services/{serviceId}/reviews - 获取商户评论列表
    @GetMapping("/services/{serviceId}/reviews")
    public Result getServiceReviews(@PathVariable Integer serviceId,
                                    @RequestParam(defaultValue = "1") Integer page,
                                    @RequestParam(defaultValue = "20") Integer pageSize,
                                    @RequestAttribute(required = false) Integer currentUserId) {
        Map<String, Object> result = serviceReviewService.getReviewsByServiceId(serviceId, page, pageSize, currentUserId);
        return Result.success(result);
    }

    // GET /services/{serviceId}/reviews/{reviewId}/replies - 获取商户评论的回复列表
    @GetMapping("/services/{serviceId}/reviews/{reviewId}/replies")
    public Result getReviewReplies(@PathVariable Integer serviceId,
                                   @PathVariable Integer reviewId,
                                   @RequestAttribute(required = false) Integer currentUserId
    ) {
        List<Map<String, Object>> replies = serviceReviewService.getRepliesByReviewId(reviewId,currentUserId);
        return Result.success(replies);
    }

    // POST /services/{serviceId}/reviews - 发表商户评论
    @PostMapping("/services/{serviceId}/reviews")
    public Result createServiceReview(@PathVariable Integer serviceId,
                                      @RequestAttribute Integer currentUserId,
                                      @RequestBody Map<String, Object> params) {
        Integer rating = (Integer) params.get("rating");
        String content = (String) params.get("content");
        List<String> images = (List<String>) params.get("images");

        Integer reviewId = serviceReviewService.createReview(currentUserId, serviceId, rating, images, content);
        if (reviewId != null) {
            return Result.success(reviewId);
        }
        return Result.error("评论失败");
    }

    // POST /services/{serviceId}/reviews/{reviewId}/replies - 回复商户评论（新增）
    @PostMapping("/services/{serviceId}/reviews/{reviewId}/replies")
    public Result replyServiceReview(@PathVariable Integer serviceId,
                                     @PathVariable Integer reviewId,
                                     @RequestAttribute Integer currentUserId,
                                     @RequestBody Map<String, Object> params) {
        String content = (String) params.get("content");
        List<String> images = (List<String>) params.get("images");
        if (content == null || content.isEmpty()) {
            return Result.error("回复内容不能为空");
        }

        Integer replyId = serviceReviewService.replyReview(currentUserId, serviceId, reviewId, content,images);
        if (replyId != null) {
            return Result.success(replyId);
        }
        return Result.error("回复失败");
    }

    // POST /services/{serviceId}/reviews/{reviewId}/like - 点赞商户评论
    @PostMapping("/services/{serviceId}/reviews/{reviewId}/like")
    public Result likeServiceReview(@PathVariable Integer reviewId,
                                    @RequestAttribute Integer currentUserId) {
        boolean success = serviceReviewService.likeReview(reviewId, currentUserId);
        if (success) {
            return Result.success("点赞成功");
        }
        return Result.error("点赞失败");
    }

    // DELETE /services/{serviceId}/reviews/{reviewId}/like - 取消点赞商户评论
    @DeleteMapping("/services/{serviceId}/reviews/{reviewId}/like")
    public Result unlikeServiceReview(@PathVariable Integer reviewId,
                                      @RequestAttribute Integer currentUserId) {
        boolean success = serviceReviewService.unlikeReview(reviewId, currentUserId);
        if (success) {
            return Result.success("取消点赞成功");
        }
        return Result.error("取消点赞失败");
    }
    // DELETE /services/{serviceId}/reviews/{reviewId} - 删除商户评论
    @DeleteMapping("/services/{serviceId}/reviews/{reviewId}")
    public Result deleteServiceReview(@PathVariable Integer serviceId,
                                      @PathVariable Integer reviewId,
                                      @RequestAttribute Integer currentUserId) {
        boolean success = serviceReviewService.deleteReview(reviewId, currentUserId);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }
}
