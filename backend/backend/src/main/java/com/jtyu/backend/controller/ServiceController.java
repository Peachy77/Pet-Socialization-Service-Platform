package com.jtyu.backend.controller;

import com.jtyu.backend.model.Result;
import com.jtyu.backend.model.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ServiceController {
    @Autowired
    private ServiceService serviceService;

    // GET /api/services - 获取服务商户列表
    @GetMapping("/api/services")
    public Result getServices() {
        List<Service> services = serviceService.getAllServices();
        return Result.success(services);
    }
}
