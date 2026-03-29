package com.jtyu.backend.controller;


import com.jtyu.backend.model.Comment;
import com.jtyu.backend.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;

    // GET /api/posts/{id}/comments - 获取评论
    @GetMapping("/api/posts/{id}/comments")
    public Result getComments(@PathVariable Integer id) {
        List<Comment> comments = commentService.getCommentsByPostId(id);
        return Result.success(comments);
    }

    // POST /api/posts/{id}/comments - 发表评论
    @PostMapping("/api/posts/{id}/comments")
    public Result createComment(@PathVariable Integer id, @RequestBody Comment comment) {
        comment.setPostId(id);
        int result = commentService.createComment(comment);
        if (result > 0) {
            return Result.success("评论成功");
        }
        return Result.error("评论失败");
    }

    // DELETE /api/comments/{id} - 删除评论
    @DeleteMapping("/api/comments/{id}")
    public Result deleteComment(@PathVariable Integer id) {
        boolean success = commentService.deleteComment(id);
        if (success) {
            return Result.success("删除评论成功");
        }
        return Result.error("评论不存在或已被删除");
    }
}
