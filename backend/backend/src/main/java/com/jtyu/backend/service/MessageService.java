package com.jtyu.backend.service;

import com.jtyu.backend.model.Message;

import java.util.List;

public interface MessageService {
    List<Message> getAllMessages();
}
