package com.jtyu.backend.controller;

import com.jtyu.backend.model.Post;
import com.jtyu.backend.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PostController {
    @Autowired
    private PostService postService;

    // GET /api/posts - 获取帖子列表
    @GetMapping("/api/posts")
    public Result getPosts() {
        List<Post> posts = postService.getAllPosts();
        return Result.success(posts);
    }

    // POST /api/posts - 创建帖子
    @PostMapping("/api/posts")
    public Result createPost(@RequestBody Post post) {
        int result = postService.createPost(post);
        if (result > 0) {
            return Result.success("发布动态成功");
        }
        return Result.error("发布失败");
    }

    // DELETE /api/posts/{id} - 删除帖子
    @DeleteMapping("/api/posts/{id}")
    public Result deletePost(@PathVariable Integer id) {
        boolean success = postService.deletePost(id);
        if (success) {
            return Result.success("删除动态成功");
        }
        return Result.error("动态不存在或已被删除");
    }

//    // GET /api/posts - 获取帖子列表（支持分页和排序）
//    @GetMapping("/api/posts")
//    public Result getPosts(
//            @RequestParam(defaultValue = "1") Integer page,
//            @RequestParam(defaultValue = "10") Integer limit,
//            @RequestParam(defaultValue = "time") String sort) {
//        PageBean<Post> pageBean = postService.getPostsByPage(page, limit, sort);
//        return Result.success(pageBean);
//    }
}
