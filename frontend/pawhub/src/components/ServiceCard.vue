<template>
  <div class="card" @click="openDetail">

    <!-- 图片 -->
    <img :src="service.image" class="cover" />

    <div class="info">

      <!-- 标题 -->
      <div class="title">
        {{ service.name }}
      </div>

      <!-- 地址 -->
      <div class="address">
        📍 {{ service.address }}
      </div>

      <!-- 评分 -->
      <div class="rating">
        ⭐ {{ service.rating }}
      </div>

      <!-- 标签 + 预约 -->
      <div class="row">

        <div class="tags">
          <span
            v-for="(tag,i) in service.tags"
            :key="tag"
          >
            {{ tag }}
            <span v-if="i < service.tags.length-1"> · </span>
          </span>
        </div>

        <button class="btn" @click.stop="goAppointment">
          预约
        </button>

      </div>

      <!-- 价格 -->
      <div class="price">
        {{ service.price }}
      </div>

    </div>

  </div>
</template>

<script>
export default {

  name: "ServiceCard",

  props: {
    service: Object
  },

  methods: {
    openDetail() {
      this.$router.push({
        name: "serviceDetail",
        query: {
          id: this.service.id,
          type: this.service.type,
          name: this.service.name,
          image: this.service.image,
          address: this.service.address,
          rating: this.service.rating,
          tags: JSON.stringify(this.service.tags || []),
          price: this.service.price
        }
      })
    },

    goAppointment() {
      this.$router.push({
        name: "serviceAppointment",
        query: {
          id: this.service.id,
          name: this.service.name,
          address: this.service.address,
          price: this.service.price,
        }
      })
    }
  }

}
</script>

<style scoped>

.card{
  display:flex;
  background:white;
  border-radius:14px;
  padding:12px;
  margin-bottom:15px;
  box-shadow:0 2px 8px rgba(0,0,0,0.05);
  cursor:pointer;
}

/* 图片 */

.cover{
  width:110px;
  height:110px;
  object-fit:cover;
  border-radius:10px;
}

/* 右侧信息 */

.info{
  flex:1;
  margin-left:12px;
  display:flex;
  flex-direction:column;
  align-items:flex-start; 
}

/* 标题 */

.title{
  font-size:17px;
  font-weight:600;
}

/* 地址 */

.address{
  font-size:13px;
  color:#777;
  margin:4px 0;
}

/* 评分 */

.rating{
  font-size:14px;
  margin:4px 0;
}

.distance{
  margin-left:10px;
  color:#888;
  font-size:12px;
}

/* 标签 + 按钮 */

.row{
  display:flex;
  align-items:center;
  justify-content:space-between;
  margin-top:4px;
  width: 100%;
}

/* 标签 */

.tags{
  display:flex;
  flex-wrap:wrap;
  font-size:13px;
  color:#555;
  margin-right:auto;  
}

/* 预约按钮 */

.btn{
  background:#9d8bdc;
  color:white;
  border:none;
  padding:4px 12px;
  border-radius:14px;
  cursor:pointer;
  font-size:12px;
}

/* 价格 */

.price{
  color:#9d8bdc;
  margin-top:6px;
  font-size:14px;
}

</style>