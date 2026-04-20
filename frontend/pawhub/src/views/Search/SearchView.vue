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
    <div v-if="suggestions.length > 0 && keyword.trim()" class="ai-suggestions">
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
      timer: null,
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
      this.onKeywordInput()
    }
    this.loadHotSearchTerms()
  },

  methods:{
    isBusinessSuccess(res) {
      const code = res?.code
      const normalizedCode = code === undefined || code === null ? null : String(code)
      return (
        normalizedCode === null ||
        normalizedCode === "0" ||
        normalizedCode === "1" ||
        normalizedCode === "200" ||
        res?.success === true
      )
    },

    extractList(payload) {
      if (Array.isArray(payload)) return payload
      if (Array.isArray(payload?.list)) return payload.list
      if (Array.isArray(payload?.records)) return payload.records
      if (Array.isArray(payload?.items)) return payload.items
      if (Array.isArray(payload?.rows)) return payload.rows
      if (Array.isArray(payload?.content)) return payload.content
      if (Array.isArray(payload?.data)) return payload.data
      return []
    },

    extractSuggestions(res) {
      const payload = res?.data
      if (Array.isArray(payload?.suggestions)) return payload.suggestions
      if (Array.isArray(payload?.keywords)) return payload.keywords
      if (Array.isArray(payload?.items)) return payload.items
      if (Array.isArray(payload)) return payload
      return this.extractList(payload)
    },

        // 输入时获取 AI 建议
    async onKeywordInput() {
      const text = String(this.keyword || "").trim()
      if (text.length < 1) {
        this.suggestions = []
        console.log("[SearchView] 输入为空，清空AI建议")
        return
      }
      
      clearTimeout(this.timer)
      this.timer = setTimeout(async () => {
        try {
          console.log("[SearchView] 开始请求AI搜索建议", { keyword: text })
          const res = await getSearchSuggestions(text)
          console.log("[SearchView] AI搜索建议原始响应", res)

          if (!this.isBusinessSuccess(res)) {
            console.warn("[SearchView] AI搜索建议响应未通过业务成功判定", {
              code: res?.code,
              success: res?.success,
              message: res?.message || res?.msg
            })
            this.suggestions = []
            return
          }

          const nextSuggestions = this.extractSuggestions(res).filter(Boolean)
          console.log("[SearchView] AI搜索建议解析结果", {
            keyword: text,
            count: nextSuggestions.length,
            suggestions: nextSuggestions
          })
          this.suggestions = nextSuggestions
        } catch (error) {
          console.error('获取建议失败:', error)
          this.suggestions = []
        }
      }, 500)
    },

    // 加载 AI 热门搜索
    async loadHotSearchTerms() {
      try {
        console.log("[SearchView] 开始请求AI热门搜索")
        const res = await getHotSearchTerms()
        console.log("[SearchView] AI热门搜索原始响应", res)

        if (!this.isBusinessSuccess(res)) {
          console.warn("[SearchView] AI热门搜索响应未通过业务成功判定", {
            code: res?.code,
            success: res?.success,
            message: res?.message || res?.msg
          })
          return
        }

        const list = this.extractSuggestions(res)
        console.log("[SearchView] AI热门搜索解析结果", {
          count: list.length,
          list
        })

        if (list.length) {
          this.hotTags = list
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