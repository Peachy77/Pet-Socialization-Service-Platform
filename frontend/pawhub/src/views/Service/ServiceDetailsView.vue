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
        :class="{on:sortType==='distance'}"
        @click="changeSort('distance')"
      >
        距离
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
      v-for="item in sortedServices"
      :key="item.id"
      :service="item"
    />

  </div>

</div>

</template>


<script>

import ServiceCard from "@/components/ServiceCard.vue"

export default {

    name:"ServiceDetailsView",

    components:{
    ServiceCard
    },
    data(){

        return{

            active:"美容",

            sortType:"default",

            categories:[
            "全部","美容","遛狗","寄养","托管","医院","救助"
            ],

            services:[

            {
            id:1,
            type:"美容",
            name:"爱宠美容工作室",
            image:"https://images.unsplash.com/photo-1516734212186-a967f81ad0d7",
            address:"朝阳区建国路88号",
            rating:4.8,
            distance:1.2,
            tags:["洗澡","美容","造型"],
            price:"¥88起"
            },

            {
            id:2,
            type:"寄养",
            name:"萌宠乐园寄养中心",
            image:"https://images.unsplash.com/photo-1583337130417-3346a1be7dee",
            address:"海淀区中关村大街123号",
            rating:4.9,
            distance:2.5,
            tags:["寄养","托管","训练"],
            price:"¥150/天"
            },

            {
            id:3,
            type:"医院",
            name:"宠物之家医院",
            image:"https://images.unsplash.com/photo-1517849845537-4d257902454a",
            address:"西城区西单北大街45号",
            rating:4.7,
            distance:3.1,
            tags:["诊疗","疫苗","体检"],
            price:"¥200起"
            },

            {
            id:4,
            type:"美容",
            name:"毛孩子SPA会所",
            image:"https://images.unsplash.com/photo-1507149833265-60c372daea22",
            address:"东城区王府井大街67号",
            rating:4.6,
            distance:1.8,
            tags:["SPA","美容","按摩"],
            price:"¥128起"
            }

            ]

        }

    },
    mounted(){

        const type = this.$route.query.type

        if(type){
        this.active = type
        }
    },
    computed:{

        /* 分类过滤 */

        filteredServices(){

        if(this.active==="全部") return this.services

        return this.services.filter(
        s=>s.type===this.active
        )

        },

        /* 排序 */

        sortedServices(){

        let arr=[...this.filteredServices]

        if(this.sortType==="distance"){
        arr.sort((a,b)=>a.distance-b.distance)
        }

        if(this.sortType==="rating"){
        arr.sort((a,b)=>b.rating-a.rating)
        }

        return arr

        }

    },
    methods:{
        goBack(){
          this.$router.push({ path: '/home' })         
        },

        changeCategory(item){

        this.active=item

        this.$router.replace({
        query:{ type:item }
        })

        },

        changeSort(type){

        this.sortType=type

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