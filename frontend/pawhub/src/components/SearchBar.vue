<template>
  <div class="search-bar">
    <span class="icon">🔍</span>

<input
  type="text"
  :placeholder="placeholder"
  v-model="keyword"
  @focus="goSearchPage"
  @keyup.enter="handleSearch"
/>

  </div>
</template>

<script>
export default {

  name:"SearchBar",

  props:{
    placeholder:{
      type:String,
      default:"搜索宠物服务、动态、用户..."
    }
  },

  data(){
    return{
      keyword:""
    }
  },

  methods:{
    handleSearch(){
      this.$emit("search",this.keyword)
    },
    goSearchPage(){
      if(this.$route.path !== "/search"){
      this.$router.push("/search")
      }
    }
  }

}
</script>

<style scoped>

.search-bar{
  display:flex;
  align-items:center;
  background:#e9f0f4;
  border-radius:25px;
  padding:10px 15px;     
}

.icon{
  margin-right:8px;
  color:#999;
}

.search-bar input{
  border:none;
  outline:none;
  background:transparent;
  flex:1;
  font-size:14px;
}

</style>
