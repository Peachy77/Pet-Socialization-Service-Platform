package com.jtyu.backend.service;

import com.jtyu.backend.model.ServiceReview;

import java.util.List;

public interface ServiceReviewService {
    List<ServiceReview> getReviewsByServiceId(Integer serviceId);
    int createReview(ServiceReview review);
}
