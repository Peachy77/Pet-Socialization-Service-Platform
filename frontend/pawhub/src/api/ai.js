import apiClient from "./client";

// 获取 AI 搜索建议
export function getSearchSuggestions(keyword) {
  return apiClient.get("/ai/search/suggestions", { 
    params: { keyword } 
  });
}

// 获取 AI 热门搜索词
export function getHotSearchTerms() {
  return apiClient.get("/ai/search/hot");
}