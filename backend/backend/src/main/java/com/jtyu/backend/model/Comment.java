package com.jtyu.backend.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Comment {
    private Integer commentId;
    private Integer userId;
    private Integer postId;
    private String content;
    private String images;      // 评论图片URL数组 JSON
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

}
