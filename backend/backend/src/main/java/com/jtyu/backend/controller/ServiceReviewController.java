package com.jtyu.backend.controller;

import com.jtyu.backend.model.Result;
import com.jtyu.backend.model.ServiceReview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ServiceReviewController {
    @Autowired
    private ServiceReviewService serviceReviewService;

    // GET /api/services/{id}/reviews - 获取商户评论
    @GetMapping("/api/services/{id}/reviews")
    public Result getServiceReviews(@PathVariable Integer id) {
        List<ServiceReview> reviews = serviceReviewService.getReviewsByServiceId(id);
        return Result.success(reviews);
    }

    // POST /api/services/{id}/reviews - 创建商户评论
    @PostMapping("/api/services/{id}/reviews")
    public Result createServiceReview(@PathVariable Integer id, @RequestBody ServiceReview review) {
        review.setServiceId(id);
        int result = serviceReviewService.createReview(review);
        if (result > 0) {
            return Result.success("评论成功");
        }
        return Result.error("评论失败");
    }
}
