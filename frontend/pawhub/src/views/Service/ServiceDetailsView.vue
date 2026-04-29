<template>

<div class="page">

  <!-- 顶部 -->
  <div class="header">

    <div class="back" @click="goBack">
      ‹
    </div>

    <div class="title">
      宠物服务
    </div>

  </div>


  <!-- 服务筛选区域（白底卡片） -->
  <div class="service-panel">

    <!-- 分类 -->
    <div class="category">

      <div
        v-for="item in categories"
        :key="item"
        :class="['tag', active===item?'active':'']"
        @click="changeCategory(item)"
      >
        {{ item }}
      </div>

    </div>


    <!-- 排序 -->
    <div class="sort">

      <span
        :class="{on:sortType==='default'}"
        @click="changeSort('default')"
      >
        综合
      </span>


      <span
        :class="{on:sortType==='rating'}"
        @click="changeSort('rating')"
      >
        评分
      </span>

      <span
        class="filter"
        @click="changeCategory(active)"
      >
        筛选：{{ active }}
      </span>

    </div>

  </div>


  <!-- 商户列表 -->
  <div class="list">

    <ServiceCard
      v-for="item in services"
      :key="item.id"
      :service="item"
    />

    <!-- 加载更多 -->
    <div v-if="loading" class="loading-text">加载中...</div>
    <div v-if="!loading && services.length === 0" class="empty-text">暂无数据</div>

    <!-- 分页 -->
    <div class="pagination" v-if="total > pageSize">
      <button class="page-btn" :disabled="page === 1" @click="goPage(page - 1)">上一页</button>
      <span class="page-info">{{ page }} / {{ totalPages }}</span>
      <button class="page-btn" :disabled="page === totalPages" @click="goPage(page + 1)">下一页</button>
    </div>

  </div>

</div>

</template>


<script>

import ServiceCard from "@/components/ServiceCard.vue"
import { getServices } from "@/api/services"

export default {

    name:"ServiceDetailsView",

    components:{
    ServiceCard
    },
    data(){

        return{

            active:"全部",

            sortType:"default",

            categories:[
            "全部","美容","遛狗","寄养","托管","医院","救助"
            ],

            services:[

            // {
            // id:1,
            // type:"美容",
            // name:"爱宠美容工作室",
            // image:"https://images.unsplash.com/photo-1516734212186-a967f81ad0d7",
            // address:"朝阳区建国路88号",
            // rating:4.8,
            // distance:1.2,
            // tags:["洗澡","美容","造型"],
            // price:"¥88起"
            // },

            // {
            // id:2,
            // type:"寄养",
            // name:"萌宠乐园寄养中心",
            // image:"https://images.unsplash.com/photo-1583337130417-3346a1be7dee",
            // address:"海淀区中关村大街123号",
            // rating:4.9,
            // distance:2.5,
            // tags:["寄养","托管","训练"],
            // price:"¥150/天"
            // },

            // {
            // id:3,
            // type:"医院",
            // name:"宠物之家医院",
            // image:"https://images.unsplash.com/photo-1517849845537-4d257902454a",
            // address:"西城区西单北大街45号",
            // rating:4.7,
            // distance:3.1,
            // tags:["诊疗","疫苗","体检"],
            // price:"¥200起"
            // },

            // {
            // id:4,
            // type:"美容",
            // name:"毛孩子SPA会所",
            // image:"https://images.unsplash.com/photo-1507149833265-60c372daea22",
            // address:"东城区王府井大街67号",
            // rating:4.6,
            // distance:1.8,
            // tags:["SPA","美容","按摩"],
            // price:"¥128起"
            // }

            ],
            loading: false,
            page: 1,
            pageSize: 10,
            total: 0
        }
    },
    mounted(){

        // const type = this.$route.query.type

        // if(type){
        // this.active = type
        // }
        const typeParam = this.$route.query.type
        if (typeParam) {
        this.active = this.getCategoryZh(typeParam)
        }
        this.fetchServices()
    },
    computed:{

        // /* 分类过滤 */

        // filteredServices(){

        // if(this.active==="全部") return this.services

        // return this.services.filter(
        // s=>s.type===this.active
        // )

        // },

        // /* 排序 */

        // sortedServices(){

        // let arr=[...this.filteredServices]

        // if(this.sortType==="distance"){
        // arr.sort((a,b)=>a.distance-b.distance)
        // }

        // if(this.sortType==="rating"){
        // arr.sort((a,b)=>b.rating-a.rating)
        // }

        // return arr

        // }

          totalPages() {
      return Math.ceil(this.total / this.pageSize)
    }

    },
    methods:{

      getCategoryEn(chinese) {
      const map = {
        "全部": null,
        "美容": "grooming",
        "遛狗": "walking",
        "寄养": "boarding",
        "托管": "sitting",
        "医院": "vet",
        "救助": "emergency"
      }
      return map[chinese]
    },

    // 英文 → 中文 转换（显示用）
    getCategoryZh(en) {
      const map = {
        "grooming": "美容",
        "walking": "遛狗",
        "boarding": "寄养",
        "sitting": "托管",
        "vet": "医院",
        "emergency": "救助"
      }
      return map[en] || en
    },

        // 获取服务列表
    async fetchServices() {
      this.loading = true
      try {
        const params = {
          page: this.page,
          pageSize: this.pageSize,
          type: this.getCategoryEn(this.active)
        }
        console.log("请求参数:", params)
        const res = await getServices(params)
        console.log("完整响应:", res)
        console.log("res.code:", res.code)
        console.log("res.data:", res.data)
        if (res.code === 1) {
          let list = res.data?.list || []
           console.log("解析后的 list:", list)

          this.services = list.map(item => ({
            id: item.service_id,
            name: item.name,
            category: item.category,
            image: item.images?.[0] || "",
            address: item.address,
            rating: item.rating,
            tags: item.tags || [],
            price: item.price || "¥0起"
          }))
          this.total = res.data?.total || 0
          console.log("最终 services:", this.services)
        }else {
          console.log("接口返回失败:", res.msg)
        }
      } catch (error) {
        console.error("获取服务列表失败", error)
      } finally {
        this.loading = false
      }
    },

    // 排序（综合和评分）
    sortServices() {
      if (this.sortType === "default") {
        // 综合排序：按价格降序
        this.services.sort((a, b) => (b.price || 0) - (a.price || 0))
      } else if (this.sortType === "rating") {
        this.services.sort((a, b) => (b.rating || 0) - (a.rating || 0))
      }
    },

        goBack(){
          this.$router.push({ path: '/home' })         
        },

        goPage(page) {
          this.page = page
          this.fetchServices()
      },

        changeCategory(item){

        this.active=item

        this.$router.replace({
          query: { type: this.getCategoryEn(item) }
        })
        this.fetchServices()
      },

        changeSort(type){

        this.sortType=type
        this.sortServices()

        }

    }

}

</script>


<style scoped>

.page{
  background:#f6f6f8;
  min-height:100vh;
  padding:15px;
}

/* 顶部 */

.header{
  display:flex;
  align-items:center;
  background:white;
  padding:18px;
  border-radius:7px;
  margin-bottom:12px;
}

.back{
  font-size:22px;
  margin-right:10px;
  cursor:pointer;
}

.title{
  font-size:18px;
  font-weight:600;
}


/* 白色卡片区域 */

.service-panel{
  border-radius:14px;
  padding:12px;
  margin-bottom:15px;
}


/* 分类 */

.category{
  display:flex;
  gap:10px;
  overflow-x:auto;
  margin-bottom:10px;
}

.tag{
  padding:6px 14px;
  background:#eee;
  border-radius:20px;
  font-size:13px;
  cursor:pointer;
}

.active{
  background:#a79be6;
  color:white;
}


/* 排序 */

.sort{
  display:flex;
  align-items:center;
  font-size:14px;
  color:#666;
  gap:18px;
}

.sort span{
  cursor:pointer;
}

.on{
  color:#8d7fe3;
  font-weight:600;
}

.filter{
  margin-left:auto;
  color:#444;
}


/* 列表 */

.list{
  margin-top:5px;
}

</style>