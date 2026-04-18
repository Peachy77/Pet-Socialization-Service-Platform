package com.jtyu.backend.service;

import com.jtyu.backend.model.Favorite;

public interface FavoriteService {
    // 添加收藏
    boolean addFavorite(Integer userId, Integer serviceId);

    // 取消收藏
    boolean removeFavorite(Integer userId, Integer serviceId);

    // 检查是否已收藏
    boolean isFavorited(Integer userId, Integer serviceId);
}
