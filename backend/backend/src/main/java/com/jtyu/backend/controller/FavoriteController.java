package com.jtyu.backend.controller;

import com.jtyu.backend.model.Favorite;
import com.jtyu.backend.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    // POST /api/favorites - 收藏商户
    @PostMapping("/api/favorites")
    public Result addFavorite(@RequestBody Favorite favorite) {
        int result = favoriteService.addFavorite(favorite);
        if (result > 0) {
            return Result.success("收藏成功");
        }
        return Result.error("收藏失败");
    }
}
