package com.jtyu.backend.controller;

import com.jtyu.backend.service.AIService;
import com.jtyu.backend.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AIController.class)
public class AIControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AIService aiService;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void testGetSearchSuggestions_Success() throws Exception {
        List<String> suggestions = Arrays.asList("宠物美容", "宠物寄养");
        when(aiService.getSearchSuggestions(anyString())).thenReturn(suggestions);

        mockMvc.perform(get("/ai/search/suggestions")
                        .param("keyword", "宠物"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.keyword").value("宠物"))
                .andExpect(jsonPath("$.data.suggestions[0]").value("宠物美容"));
    }

    @Test
    void testGetSearchSuggestions_EmptyKeyword() throws Exception {
        when(aiService.getSearchSuggestions(anyString())).thenReturn(Arrays.asList());

        mockMvc.perform(get("/ai/search/suggestions")
                        .param("keyword", ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.suggestions").isArray());
    }

    @Test
    void testGetHotSearchTerms_Success() throws Exception {
        List<String> hotTerms = Arrays.asList("宠物美容", "宠物寄养", "宠物医院");
        when(aiService.getHotSearchTerms()).thenReturn(hotTerms);

        mockMvc.perform(get("/ai/search/hot"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data[0]").value("宠物美容"));
    }

    @Test
    void testGetHotSearchTerms_Empty() throws Exception {
        when(aiService.getHotSearchTerms()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/ai/search/hot"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").isArray());
    }
}
