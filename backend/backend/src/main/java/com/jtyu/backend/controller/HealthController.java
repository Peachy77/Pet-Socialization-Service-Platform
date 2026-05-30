package com.jtyu.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {
    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", System.currentTimeMillis());
        status.put("service", "pawhub-backend");

        // 检查数据库连接
        boolean dbHealthy = checkDatabase();
        status.put("database", dbHealthy ? "UP" : "DOWN");

        // 整体状态：数据库挂了则整体 DOWN
        if (!dbHealthy) {
            status.put("status", "DOWN");
        }

        return status;
    }

    private boolean checkDatabase() {
        if (jdbcTemplate == null) return true;
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
