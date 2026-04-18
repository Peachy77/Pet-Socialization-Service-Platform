package com.jtyu.backend.controller;

import com.jtyu.backend.model.Message;
import com.jtyu.backend.model.Result;
import com.jtyu.backend.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class MessageController {
    @Autowired
    private MessageService messageService;

    // GET /messages - 获取通知列表（前端可能用 /messages 或 /notifications）
    @GetMapping("/messages")
    public Result getMessages(@RequestAttribute Integer currentUserId,
                              @RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "20") Integer pageSize) {
        Map<String, Object> result = messageService.getMessages(currentUserId, page, pageSize);
        return Result.success(result);
    }

    // GET /messages/unread-count - 获取未读通知数量
    @GetMapping("/messages/unread-count")
    public Result getUnreadNotificationCount(@RequestAttribute Integer currentUserId) {
        Long count = messageService.getUnreadCount(currentUserId);
        return Result.success(count);
    }

    // PATCH /messages/{messageId}/read - 标记通知为已读
    @PatchMapping("/messages/{messageId}/read")
    public Result markAsRead(@PathVariable Integer messageId,
                             @RequestAttribute Integer currentUserId) {
        boolean success = messageService.markAsRead(messageId, currentUserId);
        if (success) {
            return Result.success("标记已读成功");
        }
        return Result.error("标记已读失败");
    }

    // PATCH /messages/read-all - 标记所有通知为已读
    @PatchMapping("/messages/read-all")
    public Result markAllAsRead(@RequestAttribute Integer currentUserId) {
        boolean success = messageService.markAllAsRead(currentUserId);
        if (success) {
            return Result.success("标记已读成功");
        }
        return Result.error("标记已读失败");
    }
}
