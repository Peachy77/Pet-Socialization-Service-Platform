package com.jtyu.backend.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Favorite {
    private Integer favoriteId;
    private Integer userId;
    private Integer serviceId;
    private LocalDateTime createTime;
}
