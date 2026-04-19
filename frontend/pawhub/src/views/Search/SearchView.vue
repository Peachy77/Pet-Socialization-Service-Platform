<template>
  <div class="search-page">

    <!-- 顶部搜索栏 -->
    <div class="search-header">

      <!-- 返回按钮 -->
      <div class="back-btn" @click="goBack">
        ‹
      </div>

      <!-- 搜索输入框 -->
      <input
        v-model="keyword"
        class="search-input"
        placeholder="搜索宠物服务、动态、用户..."
        @keyup.enter="doSearch"
        @input="onKeywordInput"
      />

      <!-- 搜索按钮 -->
      <div class="search-btn" @click="doSearch">
        搜索
      </div>

    </div>

      <!-- AI 智能建议（放在搜索栏外面） -->
    <div v-if="suggestions.length > 0 && keyword" class="ai-suggestions">
      <div class="suggestion-title">
        <span>🤖 AI 智能搜索建议</span>
      </div>
      <div class="suggestion-list">
        <div 
          v-for="sug in suggestions" 
          :key="sug" 
          class="suggestion-item"
          @click="selectSuggestion(sug)"
        >
          🔍 {{ sug }}
        </div>
      </div>
    </div>


    <!-- 热门搜索 -->
    <div class="hot-search">

      <div class="title">热门搜索</div>

      <div class="tags">

        <span
          class="tag"
          v-for="tag in hotTags"
          :key="tag"
          @click="selectTag(tag)"
        >
          {{ tag }}
        </span>

      </div>

    </div>

  </div>
</template>

<script>
import { getSearchSuggestions, getHotSearchTerms } from "@/api/ai"

export default {

  name: "SearchView",
  data(){
    return{
      keyword: "",
      suggestions: [], 
      hotTags:[
        "宠物美容",
        "柴犬",
        "猫咪",
        "宠物医院",
        "寄养",
        "遛狗",
        "金毛",
        "宠物用品"
      ]
    }
  },

  mounted(){

    /* 从HomeView带来的搜索关键词 */
    if(this.$route.query.q){
      this.keyword = this.$route.query.q
    }
    this.loadHotSearchTerms()
  },

  methods:{

        // 输入时获取 AI 建议
    async onKeywordInput() {
      if (this.keyword.length < 2) {
        this.suggestions = []
        return
      }
      
      clearTimeout(this.timer)
      this.timer = setTimeout(async () => {
        try {
          const res = await getSearchSuggestions(this.keyword)
          if (res.code === 0 || res.code === 1) {
            this.suggestions = res.data?.suggestions || []
          }
        } catch (error) {
          console.error('获取建议失败:', error)
        }
      }, 500)
    },

    // 加载 AI 热门搜索
    async loadHotSearchTerms() {
      try {
        const res = await getHotSearchTerms()
        if (res.code === 0 || res.code === 1) {
          if (res.data && res.data.length) {
            this.hotTags = res.data
          }
        }
      } catch (error) {
        console.error('获取热门搜索失败:', error)
      }
    },

    selectSuggestion(suggestion) {
      this.keyword = suggestion
      this.suggestions = []
      // 不跳转，让用户自己点击搜索
    },

    goBack(){
      this.$router.push({ path: '/home' })
      
    },

    doSearch(){
      this.$router.push({ 
        path: '/search/results',
        query: { keyword: this.keyword }
      })

    },

    selectTag(tag){
      this.keyword = tag
      this.doSearch()
    }

  }

}
</script>

<style scoped>

.search-page{
  background:#f6f6f8;
  min-height:100vh;
}

/* 顶部搜索栏 */

.search-header{
  display:flex;
  align-items:center;
  padding:10px 12px;
  background:white;
  border-bottom:1px solid #eee;
}

.back-btn{
  font-size:20px;
  margin-right:10px;
  cursor:pointer;
}

.search-input{
  flex:1;
  border:none;
  background:#f1f1f3;
  border-radius:20px;
  padding:8px 14px;
  outline:none;
  font-size:14px;
}

.search-btn{
  margin-left:10px;
  color:#8a79d6;
  font-size:14px;
  cursor:pointer;
}

/* AI 建议样式 */
.ai-suggestions {
  background: white;
  margin: 12px;
  border-radius: 16px;
  padding: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.suggestion-title {
  font-size: 12px;
  color: #8e7ddc;
  margin-bottom: 8px;
  padding-bottom: 6px;
  border-bottom: 1px solid #eee;
}

.suggestion-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.suggestion-item {
  padding: 6px 12px;
  background: #f5f5f7;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: 0.2s;
}

.suggestion-item:hover {
  background: #e8e4f5;
  color: #8e7ddc;
}

/* 热门搜索 */

.hot-search{
  padding:15px;
}

.title{
  font-size:16px;
  color:#6b6b73;
  margin-bottom:12px;
}

/* 标签 */

.tags{
  display:flex;
  flex-wrap:wrap;
  gap:10px;
}

.tag{
  padding:6px 14px;
  background:#ececf1;
  border-radius:20px;
  font-size:14px;
  color:#444;
  cursor:pointer;
}

.tag:hover{
  background:#e1e1e8;
}

</style>