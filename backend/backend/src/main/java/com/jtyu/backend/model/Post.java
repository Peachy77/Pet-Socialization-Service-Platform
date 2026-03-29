package com.jtyu.backend.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Post {
    private Integer postId;
    private Integer userId;
    private String content;
    private String images;      // JSON格式
    private String tags;        // JSON格式
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
