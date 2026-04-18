package com.jtyu.backend.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PrivateMessage {
    private Integer messageId;
    private Integer senderId;
    private Integer receiverId;
    private String content;
    private String images;      // 消息图片URL数组 JSON
    private Boolean isRead;
    private LocalDateTime createTime;

}

