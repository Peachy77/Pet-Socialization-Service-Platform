package com.jtyu.backend.service.impl;

import com.jtyu.backend.mapper.FavoriteMapper;
import com.jtyu.backend.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FavoriteServiceImpl implements FavoriteService {
    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public boolean addFavorite(Integer userId, Integer serviceId) {
        if (favoriteMapper.exists(userId, serviceId) > 0) {
            return false;
        }
        return favoriteMapper.insert(userId, serviceId) > 0;
    }

    @Override
    public boolean removeFavorite(Integer userId, Integer serviceId) {
        if (favoriteMapper.exists(userId, serviceId) == 0) {
            return false;
        }
        return favoriteMapper.delete(userId, serviceId) > 0;
    }

    @Override
    public boolean isFavorited(Integer userId, Integer serviceId) {
        return favoriteMapper.exists(userId, serviceId) > 0;
    }
}
