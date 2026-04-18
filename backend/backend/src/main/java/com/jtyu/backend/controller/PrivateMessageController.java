package com.jtyu.backend.controller;

import com.jtyu.backend.model.PrivateMessage;
import com.jtyu.backend.model.Result;
import com.jtyu.backend.service.PrivateMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class PrivateMessageController {
    @Autowired
    private PrivateMessageService privateMessageService;

    // GET /messages/conversations - 获取会话列表
    @GetMapping("/messages/conversations")
    public Result getConversationList(@RequestAttribute Integer currentUserId) {
        List<Map<String, Object>> conversations = privateMessageService.getConversationList(currentUserId);
        return Result.success(conversations);
    }

    // GET /messages/conversations/{targetUserId} - 获取与某用户的聊天记录
    @GetMapping("/messages/conversations/{targetUserId}")
    public Result getConversationMessages(@PathVariable Integer targetUserId,
                                          @RequestAttribute Integer currentUserId,
                                          @RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "20") Integer pageSize) {
        Map<String, Object> result = privateMessageService.getConversation(currentUserId, targetUserId, page, pageSize);
        return Result.success(result);
    }

    // POST /messages - 发送私信
    @PostMapping("/messages")
    public Result sendMessage(@RequestAttribute Integer currentUserId,
                              @RequestBody Map<String, Object> params) {
        Integer receiverId = (Integer) params.get("receiver_id");
        String content = (String) params.get("content");
        List<String> images = (List<String>) params.get("images");

        if (receiverId == null) {
            return Result.error("接收者ID不能为空");
        }
        if ((content == null || content.isEmpty()) && (images == null || images.isEmpty())) {
            return Result.error("内容或图片不能为空");
        }

        Integer messageId = privateMessageService.sendMessage(currentUserId, receiverId, content, images);
        if (messageId != null) {
            return Result.success(messageId);
        }
        return Result.error("发送失败");
    }

    // PATCH /messages/conversations/{targetUserId}/read - 标记会话为已读
    @PatchMapping("/messages/conversations/{targetUserId}/read")
    public Result markConversationAsRead(@PathVariable Integer targetUserId,
                                         @RequestAttribute Integer currentUserId) {
        boolean success = privateMessageService.markConversationAsRead(currentUserId, targetUserId);
        if (success) {
            return Result.success("标记已读成功");
        }
        return Result.error("标记已读失败");
    }

    // GET /messages/unread-count - 获取未读消息总数
    @GetMapping("/messages/unread-count")
    public Result getUnreadMessageCount(@RequestAttribute Integer currentUserId) {
        Long count = privateMessageService.getUnreadCount(currentUserId);
        return Result.success(count);
    }
}
