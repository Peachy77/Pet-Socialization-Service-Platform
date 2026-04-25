package com.jtyu.backend.service.impl;
import com.jtyu.backend.mapper.FavoriteMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FavoriteServiceImplTest {
    @Mock private FavoriteMapper favoriteMapper;

    @InjectMocks
    private FavoriteServiceImpl favoriteService;

    @Test
    void testAddFavorite_Success() {
        when(favoriteMapper.exists(1, 100)).thenReturn(0);
        when(favoriteMapper.insert(1, 100)).thenReturn(1);

        boolean result = favoriteService.addFavorite(1, 100);

        assertTrue(result);
        verify(favoriteMapper, times(1)).insert(1, 100);
    }

    @Test
    void testAddFavorite_AlreadyExists() {
        when(favoriteMapper.exists(1, 100)).thenReturn(1);

        boolean result = favoriteService.addFavorite(1, 100);

        assertFalse(result);
        verify(favoriteMapper, never()).insert(anyInt(), anyInt());
    }

    @Test
    void testRemoveFavorite_Success() {
        when(favoriteMapper.exists(1, 100)).thenReturn(1);
        when(favoriteMapper.delete(1, 100)).thenReturn(1);

        boolean result = favoriteService.removeFavorite(1, 100);

        assertTrue(result);
        verify(favoriteMapper, times(1)).delete(1, 100);
    }

    @Test
    void testRemoveFavorite_NotExists() {
        when(favoriteMapper.exists(1, 100)).thenReturn(0);

        boolean result = favoriteService.removeFavorite(1, 100);

        assertFalse(result);
        verify(favoriteMapper, never()).delete(anyInt(), anyInt());
    }

    @Test
    void testIsFavorited_True() {
        when(favoriteMapper.exists(1, 100)).thenReturn(1);

        boolean result = favoriteService.isFavorited(1, 100);

        assertTrue(result);
    }

    @Test
    void testIsFavorited_False() {
        when(favoriteMapper.exists(1, 100)).thenReturn(0);

        boolean result = favoriteService.isFavorited(1, 100);

        assertFalse(result);
    }
}
