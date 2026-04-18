package com.jtyu.backend.service;

import com.jtyu.backend.model.ServiceMerchant;

import java.util.List;
import java.util.Map;

public interface ServiceMerchantService {
    // 获取商户列表
    Map<String, Object> getServiceList(String keyword, String category, Integer page, Integer pageSize);

    // 获取商户详情
    Map<String, Object> getServiceDetail(Integer serviceId, Integer currentUserId);

    // 检查是否已收藏
    boolean isFavorited(Integer userId, Integer serviceId);
}
