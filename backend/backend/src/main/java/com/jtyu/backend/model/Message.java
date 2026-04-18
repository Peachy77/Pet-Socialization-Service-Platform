package com.jtyu.backend.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Message {
    private Integer messageId;
    private Integer receiverId;
    private Integer senderId;
    private String type;        // like/comment/follow/system/order
    private String content;
    private Integer relatedId;
    private Boolean isRead;
    private LocalDateTime createTime;

}
