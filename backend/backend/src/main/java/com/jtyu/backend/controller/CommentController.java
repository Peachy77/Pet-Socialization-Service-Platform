package com.jtyu.backend.controller;


import com.jtyu.backend.model.Comment;
import com.jtyu.backend.model.Result;
import com.jtyu.backend.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;

    // GET /posts/{postId}/comments - 获取评论列表
    @GetMapping("/posts/{postId}/comments")
    public Result getComments(@PathVariable Integer postId,
                              @RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "20") Integer pageSize) {
        Map<String, Object> result = commentService.getCommentsByPostId(postId, page, pageSize);
        return Result.success(result);
    }

    // POST /posts/{postId}/comments - 发表评论
    @PostMapping("/posts/{postId}/comments")
    public Result createComment(@PathVariable Integer postId,
                                @RequestAttribute Integer currentUserId,
                                @RequestBody Map<String, Object> params) {
        String content = (String) params.get("content");
        List<String> images = (List<String>) params.get("images");
        Integer parentCommentId = (Integer) params.get("parentCommentId");

        Integer commentId = commentService.createComment(currentUserId, postId, parentCommentId, content, images);
        if (commentId != null) {
            return Result.success(commentId);
        }
        return Result.error("评论失败");
    }

    // DELETE /posts/{postId}/comments/{commentId} - 删除评论（前端未直接使用，但保留）
    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public Result deleteComment(@PathVariable Integer commentId,
                                @RequestAttribute Integer currentUserId) {
        boolean success = commentService.deleteComment(commentId, currentUserId);
        if (success) {
            return Result.success("删除成功");
        }
        return Result.error("删除失败");
    }

    // POST /posts/{postId}/comments/{commentId}/like - 点赞评论
    @PostMapping("/posts/{postId}/comments/{commentId}/like")
    public Result likeComment(@PathVariable Integer commentId,
                              @RequestAttribute Integer currentUserId) {
        boolean success = commentService.likeComment(commentId, currentUserId);
        if (success) {
            return Result.success("点赞成功");
        }
        return Result.error("点赞失败");
    }

    // DELETE /posts/{postId}/comments/{commentId}/like - 取消点赞评论
    @DeleteMapping("/posts/{postId}/comments/{commentId}/like")
    public Result unlikeComment(@PathVariable Integer commentId,
                                @RequestAttribute Integer currentUserId) {
        boolean success = commentService.unlikeComment(commentId, currentUserId);
        if (success) {
            return Result.success("取消点赞成功");
        }
        return Result.error("取消点赞失败");
    }

    // POST /posts/{postId}/comments/{commentId}/replies - 回复评论
    @PostMapping("/posts/{postId}/comments/{commentId}/replies")
    public Result replyComment(@PathVariable Integer postId,
                               @PathVariable Integer commentId,
                               @RequestAttribute Integer currentUserId,
                               @RequestBody Map<String, String> params) {
        String content = params.get("content");
        Integer newCommentId = commentService.createComment(currentUserId, postId, commentId, content, null);
        if (newCommentId != null) {
            return Result.success(newCommentId);
        }
        return Result.error("回复失败");
    }
}
