# AI 功能集成说明

## 功能名称
智能搜索建议

## 使用模型
DeepSeek Chat (deepseek-chat)

## 功能描述
在宠物社交平台的搜索页面集成 AI 智能搜索建议功能：

1. **实时搜索建议**：用户在搜索框输入关键词时，AI 实时分析并生成相关的搜索建议
2. **热门搜索词**：AI 根据平台内容分析，动态生成热门搜索关键词
3. **智能补全**：帮助用户快速找到感兴趣的内容

## 实现方式

### 后端实现
- **API 端点**：
  - `GET /ai/search/suggestions?keyword=xxx` - 获取搜索建议
  - `GET /ai/search/hot` - 获取热门搜索词
- **调用方式**：通过 OkHttp 调用 DeepSeek API
- **模型参数**：deepseek-chat

### 前端实现
- **页面**：`SearchView.vue`
- **交互**：用户输入时触发防抖请求，展示 AI 建议列表
- **用户体验**：点击建议词自动填入搜索框，用户确认后搜索

## 使用场景
- 用户不确定搜索什么时，AI 提供相关推荐
- 帮助用户发现热门内容
- 提升搜索效率和用户体验

## 环境配置
需要在 `application.properties` 中配置 DeepSeek API Key：

```properties
deepseek.api.key=your-api-key
deepseek.api.url=https://api.deepseek.com/v1
deepseek.model=deepseek-chat
```