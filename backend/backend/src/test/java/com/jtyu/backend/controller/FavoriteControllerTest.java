package com.jtyu.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtyu.backend.service.FavoriteService;
import com.jtyu.backend.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FavoriteController.class)
public class FavoriteControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FavoriteService favoriteService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddFavorite_Success() throws Exception {
        Map<String, Integer> params = new HashMap<>();
        params.put("service_id", 100);

        when(favoriteService.addFavorite(anyInt(), anyInt())).thenReturn(true);

        mockMvc.perform(post("/favorites")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("收藏成功"));
    }

    @Test
    void testAddFavorite_WithServiceIdKey() throws Exception {
        Map<String, Integer> params = new HashMap<>();
        params.put("serviceId", 100);

        when(favoriteService.addFavorite(anyInt(), anyInt())).thenReturn(true);

        mockMvc.perform(post("/favorites")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testAddFavorite_MissingServiceId() throws Exception {
        Map<String, Integer> params = new HashMap<>();

        mockMvc.perform(post("/favorites")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("service_id 不能为空"));
    }

    @Test
    void testAddFavorite_Failed() throws Exception {
        Map<String, Integer> params = new HashMap<>();
        params.put("service_id", 100);

        when(favoriteService.addFavorite(anyInt(), anyInt())).thenReturn(false);

        mockMvc.perform(post("/favorites")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("收藏失败"));
    }

    @Test
    void testRemoveFavorite_Success() throws Exception {
        Map<String, Integer> params = new HashMap<>();
        params.put("service_id", 100);

        when(favoriteService.removeFavorite(anyInt(), anyInt())).thenReturn(true);

        mockMvc.perform(delete("/favorites")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.msg").value("取消收藏成功"));
    }

    @Test
    void testRemoveFavorite_WithServiceIdKey() throws Exception {
        Map<String, Integer> params = new HashMap<>();
        params.put("serviceId", 100);

        when(favoriteService.removeFavorite(anyInt(), anyInt())).thenReturn(true);

        mockMvc.perform(delete("/favorites")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    @Test
    void testRemoveFavorite_MissingServiceId() throws Exception {
        Map<String, Integer> params = new HashMap<>();

        mockMvc.perform(delete("/favorites")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("service_id 不能为空"));
    }

    @Test
    void testRemoveFavorite_Failed() throws Exception {
        Map<String, Integer> params = new HashMap<>();
        params.put("service_id", 100);

        when(favoriteService.removeFavorite(anyInt(), anyInt())).thenReturn(false);

        mockMvc.perform(delete("/favorites")
                        .requestAttr("currentUserId", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(params)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.msg").value("取消收藏失败"));
    }
}
