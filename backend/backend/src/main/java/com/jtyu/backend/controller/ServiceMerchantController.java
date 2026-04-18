package com.jtyu.backend.controller;

import com.jtyu.backend.model.Result;
import com.jtyu.backend.service.ServiceMerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ServiceMerchantController {
    @Autowired
    private ServiceMerchantService serviceMerchantService;

    // GET /services - 获取商户列表
    @GetMapping("/services")
    public Result getServices(@RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "20") Integer pageSize,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(required = false) String type,
                              @RequestAttribute(required = false) Integer currentUserId) {
        Map<String, Object> result = serviceMerchantService.getServiceList(keyword, type, page, pageSize);
        return Result.success(result);
    }

    // GET /services/{id} - 获取商户详情
    @GetMapping("/services/{id}")
    public Result getServiceDetail(@PathVariable Integer id,
                                   @RequestAttribute(required = false) Integer currentUserId) {
        Map<String, Object> service = serviceMerchantService.getServiceDetail(id, currentUserId);
        if (service == null) {
            return Result.error("商户不存在");
        }
        return Result.success(service);
    }
}
