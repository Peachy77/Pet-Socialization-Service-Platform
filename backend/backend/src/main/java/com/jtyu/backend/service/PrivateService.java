package com.jtyu.backend.service;

import com.jtyu.backend.model.PrivateMessage;

import java.util.List;

public interface PrivateService {
    List<PrivateMessage> getAllMessages();
    int sendMessage(PrivateMessage message);
}
