package com.jtyu.backend.controller;

import com.jtyu.backend.model.Favorite;
import com.jtyu.backend.model.Result;
import com.jtyu.backend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    // POST /favorites - 添加收藏
    @PostMapping("/favorites")
    public Result addFavorite(@RequestAttribute Integer currentUserId,
                              @RequestBody Map<String, Integer> params) {
        Integer serviceId = params.get("service_id");
        if (serviceId == null) {
            serviceId = params.get("serviceId");
        }
        if (serviceId == null) {
            return Result.error("service_id 不能为空");
        }

        boolean success = favoriteService.addFavorite(currentUserId, serviceId);
        if (success) {
            return Result.success("收藏成功");
        }
        return Result.error("收藏失败");
    }

    // DELETE /favorites - 取消收藏
    @DeleteMapping("/favorites")
    public Result removeFavorite(@RequestAttribute Integer currentUserId,
                                 @RequestBody Map<String, Integer> params) {
        Integer serviceId = params.get("service_id");
        if (serviceId == null) {
            serviceId = params.get("serviceId");
        }
        if (serviceId == null) {
            return Result.error("service_id 不能为空");
        }

        boolean success = favoriteService.removeFavorite(currentUserId, serviceId);
        if (success) {
            return Result.success("取消收藏成功");
        }
        return Result.error("取消收藏失败");
    }
}
