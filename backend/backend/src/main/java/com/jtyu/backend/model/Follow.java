package com.jtyu.backend.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Follow {
    private Integer followId;
    private Integer followerId;
    private Integer followeeId;
    private LocalDateTime createTime;
}
