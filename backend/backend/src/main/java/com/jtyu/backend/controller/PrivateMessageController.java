package com.jtyu.backend.controller;

import com.jtyu.backend.model.PrivateMessage;
import com.jtyu.backend.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PrivateMessageController {
    @Autowired
    private PrivateMessageService privateMessageService;

    // GET /api/private_messages - 获取私信记录
    @GetMapping("/api/private_messages")
    public Result getPrivateMessages() {
        List<PrivateMessage> messages = privateMessageService.getAllMessages();
        return Result.success(messages);
    }

    // POST /api/private_messages - 发送私信
    @PostMapping("/api/private_messages")
    public Result sendPrivateMessage(@RequestBody PrivateMessage message) {
        int result = privateMessageService.sendMessage(message);
        if (result > 0) {
            return Result.success("发送成功");
        }
        return Result.error("发送失败");
    }
}
