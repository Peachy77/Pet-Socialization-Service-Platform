package com.jtyu.backend.controller;

import com.jtyu.backend.model.Post;
import com.jtyu.backend.model.Result;
import com.jtyu.backend.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class PostController {
    @Autowired
    private PostService postService;

    // GET /posts - 获取动态列表
    @GetMapping("/posts")
    public Result getPosts(@RequestParam(defaultValue = "1") Integer page,
                           @RequestParam(defaultValue = "20") Integer pageSize,
                           @RequestParam(required = false) String keyword,
                           @RequestParam(required = false) String tag,
                           @RequestAttribute(required = false) Integer currentUserId) {
        Map<String, Object> result = postService.getPostList(keyword, tag, page, pageSize, currentUserId);
        return Result.success(result);
    }

    // GET /posts/{id} - 获取动态详情
    @GetMapping("/posts/{id}")
    public Result getPostDetail(@PathVariable Integer id,
                                @RequestAttribute(required = false) Integer currentUserId) {
        Map<String, Object> post = postService.getPostDetail(id, currentUserId);
        if (post == null) {
            return Result.error("动态不存在");
        }
        return Result.success(post);
    }

    // POST /posts - 发布动态
    @PostMapping("/posts")
    public Result createPost(@RequestAttribute Integer currentUserId,
                             @RequestBody Map<String, Object> params) {
        String content = (String) params.get("content");
        List<String> images = (List<String>) params.get("images");
        List<String> tags = (List<String>) params.get("tags");

        Integer postId = postService.createPost(currentUserId, content, images, tags);
        if (postId != null) {
            return Result.success(postId);
        }
        return Result.error("发布失败");
    }

    // DELETE /posts/{id} - 删除动态
    @DeleteMapping("/posts/{id}")
    public Result deletePost(@PathVariable Integer id,
                             @RequestAttribute Integer currentUserId) {
        boolean success = postService.deletePost(id, currentUserId);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    // POST /posts/{id}/like - 点赞动态
    @PostMapping("/posts/{id}/like")
    public Result likePost(@PathVariable Integer id,
                           @RequestAttribute Integer currentUserId) {
        boolean success = postService.likePost(id, currentUserId);
        if (success) {
            return Result.success("点赞成功");
        }
        return Result.error("点赞失败");
    }

    // DELETE /posts/{id}/like - 取消点赞
    @DeleteMapping("/posts/{id}/like")
    public Result unlikePost(@PathVariable Integer id,
                             @RequestAttribute Integer currentUserId) {
        boolean success = postService.unlikePost(id, currentUserId);
        if (success) {
            return Result.success("取消点赞成功");
        }
        return Result.error("取消点赞失败");
    }
}
