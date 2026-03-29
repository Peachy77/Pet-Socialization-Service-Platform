package com.jtyu.backend.controller;


import com.jtyu.backend.model.Follow;
import com.jtyu.backend.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class FollowController {

    @Autowired
    private FollowService followService;

    // POST /api/follow - 关注用户
    @PostMapping("/api/follow")
    public Result followUser(@RequestBody Follow follow) {
        int result = followService.followUser(follow);
        if (result > 0) {
            return Result.success("关注成功");
        }
        return Result.error("关注失败");
    }

    // DELETE /api/follow/{id} - 取消关注
    @DeleteMapping("/api/follow/{id}")
    public Result unfollowUser(@PathVariable Integer id) {
        boolean success = followService.unfollowUser(id);
        if (success) {
            return Result.success("取消关注成功");
        }
        return Result.error("关注记录不存在");
    }
}
