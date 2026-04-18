package com.jtyu.backend.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class User {
    private Integer userId;
    private String username;
    private String password;
    private String email;
    private String avatar;//用户头像图片的 URL
    private String bio;
    private Integer followerCount;
    private Integer followingCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
