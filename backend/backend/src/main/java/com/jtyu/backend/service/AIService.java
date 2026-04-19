package com.jtyu.backend.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jtyu.backend.config.AIConfig;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class AIService {
    @Autowired
    private AIConfig aiConfig;

    private final OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取搜索建议
     */
    public List<String> getSearchSuggestions(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String prompt = "你是一个宠物社交平台的搜索助手。用户正在搜索：'" + keyword + "'。\n" +
                "请推荐5个相关的搜索词，帮助用户找到更多内容。\n" +
                "要求：每个词2-6个字，只返回推荐词，每行一个，不要有其他内容。\n" +
                "示例：宠物美容";

        String response = callDeepSeek(prompt);
        return parseResponse(response);
    }

    /**
     * 获取热门搜索词
     */
    public List<String> getHotSearchTerms() {
        String prompt = "请推荐8个宠物社交平台的热门搜索关键词。\n" +
                "要求：每个词2-6个字，只返回关键词，每行一个。\n" +
                "示例：柴犬\n宠物美容\n猫咪\n宠物医院\n寄养\n遛狗\n金毛\n宠物用品";

        String response = callDeepSeek(prompt);
        List<String> result = parseResponse(response);
        return result.isEmpty() ? getDefaultHotTerms() : result;
    }

    private String callDeepSeek(String prompt) {
        // 没有 API Key 时返回模拟数据（用于测试）
        if (aiConfig.getApiKey() == null || aiConfig.getApiKey().isEmpty()) {
            System.out.println("未配置 API Key，使用模拟数据");
            return getMockResponse(prompt);
        }

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", aiConfig.getModel());

            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);
            requestBody.put("messages", messages);

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            Request request = new Request.Builder()
                    .url(aiConfig.getApiUrl() + "/chat/completions")
                    .addHeader("Authorization", "Bearer " + aiConfig.getApiKey())
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                    .build();

            try (Response response = httpClient.newCall(request).execute()) {
                String responseBody = response.body().string();
                System.out.println("AI响应: " + responseBody);
                JsonNode jsonNode = objectMapper.readTree(responseBody);
                return jsonNode.path("choices").path(0).path("message").path("content").asText();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    private List<String> parseResponse(String response) {
        if (response == null || response.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>();
        String[] lines = response.split("\n");
        for (String line : lines) {
            String trimmed = line.trim();
            if (!trimmed.isEmpty() && !trimmed.startsWith("#") && !trimmed.startsWith("-")) {
                trimmed = trimmed.replaceAll("^\\d+[\\.\\、\\s]*", "");
                if (trimmed.length() <= 15 && trimmed.length() >= 2) {
                    result.add(trimmed);
                }
            }
        }
        return result.size() > 8 ? result.subList(0, 8) : result;
    }

    private String getMockResponse(String prompt) {
        if (prompt.contains("热门搜索")) {
            return "宠物美容\n柴犬\n猫咪\n宠物医院\n寄养\n遛狗\n金毛\n宠物用品";
        }
        return "宠物美容\n柴犬\n猫咪领养\n宠物医院\n遛狗";
    }

    private List<String> getDefaultHotTerms() {
        return Arrays.asList("宠物美容", "柴犬", "猫咪", "宠物医院", "寄养", "遛狗", "金毛", "宠物用品");
    }
}
