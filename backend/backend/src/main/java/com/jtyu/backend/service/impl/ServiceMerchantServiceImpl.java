package com.jtyu.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtyu.backend.mapper.FavoriteMapper;
import com.jtyu.backend.mapper.ServiceMerchantMapper;
import com.jtyu.backend.model.ServiceMerchant;
import com.jtyu.backend.service.ServiceMerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ServiceMerchantServiceImpl implements ServiceMerchantService {
    @Autowired
    private ServiceMerchantMapper serviceMerchantMapper;

    @Autowired
    private FavoriteMapper favoriteMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> getServiceList(String keyword, String category, Integer page, Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<Map<String, Object>> list = serviceMerchantMapper.selectList(keyword, category, offset, pageSize);
        Long total = serviceMerchantMapper.countList(keyword, category);

        // 解析 images JSON
        for (Map<String, Object> service : list) {
            String imagesStr = (String) service.get("images");
            if (imagesStr != null && !imagesStr.isEmpty()) {
                try {
                    service.put("images", objectMapper.readValue(imagesStr, List.class));
                } catch (Exception e) {
                    service.put("images", Collections.emptyList());
                }
            } else {
                service.put("images", Collections.emptyList());
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
    public Map<String, Object> getServiceDetail(Integer serviceId, Integer currentUserId) {
        Map<String, Object> service = serviceMerchantMapper.selectById(serviceId);
        if (service == null) {
            return null;
        }

        // 解析 JSON 字段
        try {
            String imagesStr = (String) service.get("images");
            service.put("images", imagesStr != null ? objectMapper.readValue(imagesStr, List.class) : Collections.emptyList());

            String businessHoursStr = (String) service.get("business_hours");
            service.put("business_hours", businessHoursStr != null ? objectMapper.readValue(businessHoursStr, Map.class) : null);

            String servicesOfferedStr = (String) service.get("services_offered");
            service.put("services_offered", servicesOfferedStr != null ? objectMapper.readValue(servicesOfferedStr, List.class) : Collections.emptyList());
        } catch (Exception e) {
            service.put("images", Collections.emptyList());
            service.put("business_hours", null);
            service.put("services_offered", Collections.emptyList());
        }

        // 检查是否已收藏
        if (currentUserId != null) {
            boolean favorited = favoriteMapper.exists(currentUserId, serviceId) > 0;
            service.put("is_favorited", favorited);
        } else {
            service.put("is_favorited", false);
        }

        return service;
    }

    @Override
    public boolean isFavorited(Integer userId, Integer serviceId) {
        return favoriteMapper.exists(userId, serviceId) > 0;
    }
}
