<template>
  <div>
    <div class="search-bar">
      <el-input
        v-model="query.name"
        placeholder="搜索农产品名称"
        clearable
        class="search-input"
        @keyup.enter="handleSearch"
      />
      <el-select
        v-model="query.categoryId"
        placeholder="按分类筛选"
        clearable
        class="search-select"
      >
        <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
      </el-select>
      <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
    </div>

    <el-row :gutter="16">
      <el-col :span="6" v-for="p in list" :key="p.id">
        <el-card class="product-card" shadow="hover" :body-style="{ padding: '0' }">
          <div class="product-img-wrap">
            <img v-if="p.coverUrl" :src="p.coverUrl" class="product-img" />
            <div v-else class="product-img product-img-empty">暂无图片</div>
          </div>
          <div class="product-info">
            <div class="product-name">{{ p.name }}</div>
            <div class="product-origin">{{ p.origin || '产地未知' }}</div>
            <div class="product-price">
              <span class="price">{{ p.price ?? '-' }}</span> 元/{{ p.unit || '件' }}
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-empty v-if="!loading && !list.length" description="暂无产品" />

    <el-pagination
      v-model:current-page="query.page"
      v-model:page-size="query.pageSize"
      :total="total"
      layout="total, prev, pager, next"
      class="pagination"
      @current-change="loadData"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { pageProduct } from '@/api/product'
import { listCategory } from '@/api/category'

const loading = ref(false)
const list = ref([])
const total = ref(0)
const categories = ref([])
const query = reactive({ name: '', categoryId: null, page: 1, pageSize: 12 })

async function loadData() {
  loading.value = true
  try {
    const res = await pageProduct(query)
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  query.page = 1
  loadData()
}

onMounted(() => {
  listCategory().then((res) => {
    categories.value = res.data
  })
  loadData()
})
</script>

<style scoped>
.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
}
.search-input {
  width: 240px;
}
.search-select {
  width: 160px;
}
.product-card {
  margin-bottom: 16px;
}
.product-img-wrap {
  height: 150px;
  overflow: hidden;
}
.product-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}
.product-img-empty {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f0f0;
  color: #c0c4cc;
}
.product-info {
  padding: 12px;
}
.product-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 6px;
}
.product-origin {
  color: #909399;
  font-size: 13px;
  margin-bottom: 6px;
}
.product-price .price {
  color: #e6a23c;
  font-size: 18px;
  font-weight: bold;
}
.pagination {
  margin-top: 8px;
  justify-content: center;
}
</style>
