package com.jtyu.backend.controller;


import com.jtyu.backend.model.Result;
import com.jtyu.backend.service.AIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AIController {
    @Autowired
    private AIService aiService;

    @GetMapping("/ai/search/suggestions")
    public Result getSearchSuggestions(@RequestParam String keyword) {
        List<String> suggestions = aiService.getSearchSuggestions(keyword);
        Map<String, Object> result = new HashMap<>();
        result.put("keyword", keyword);
        result.put("suggestions", suggestions);
        return Result.success(result);
    }

    @GetMapping("/ai/search/hot")
    public Result getHotSearchTerms() {
        List<String> hotTerms = aiService.getHotSearchTerms();
        return Result.success(hotTerms);
    }
}
