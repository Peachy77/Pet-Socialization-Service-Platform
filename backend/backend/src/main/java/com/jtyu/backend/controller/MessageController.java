package com.jtyu.backend.controller;

import com.jtyu.backend.model.Message;
import com.jtyu.backend.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MessageController {
    @Autowired
    private MessageService messageService;

    // GET /api/messages - 获取消息列表
    @GetMapping("/api/messages")
    public Result getMessages() {
        List<Message> messages = messageService.getAllMessages();
        return Result.success(messages);
    }
}
