package com.jtyu.backend.controller;

import com.jtyu.backend.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LikeController {
    @Autowired
    private LikeService likeService;

    // POST /api/posts/{id}/like - 点赞帖子
    @PostMapping("/api/posts/{id}/like")
    public Result likePost(@PathVariable Integer id) {
        int result = likeService.likePost(id);
        if (result > 0) {
            return Result.success("点赞成功");
        }
        return Result.error("点赞失败");
    }
}
