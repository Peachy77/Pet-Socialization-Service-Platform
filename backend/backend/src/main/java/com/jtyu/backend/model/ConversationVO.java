package com.jtyu.backend.model;

import lombok.Data;

@Data
public class ConversationVO {
    private Integer userId;           // 对方用户ID
    private String username;          // 对方用户名
    private String avatar;            // 对方头像
    private String lastMessage;       // 最后一条消息内容
    private String lastMessageTime;   // 最后一条消息时间
    private Integer unreadCount;      // 未读消息数
}
