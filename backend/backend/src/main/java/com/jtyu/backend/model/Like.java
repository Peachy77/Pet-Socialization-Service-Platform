package com.jtyu.backend.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Like {
    private Integer likeId;
    private Integer userId;
    private Integer postId;
    private LocalDateTime createTime;
}
