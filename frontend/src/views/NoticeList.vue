<template>
  <div>
    <el-card v-for="n in list" :key="n.id" class="notice-item" shadow="hover">
      <div class="notice-title">{{ n.title }}</div>
      <div class="notice-time">{{ n.createTime }}</div>
      <div class="notice-content">{{ n.content }}</div>
    </el-card>

    <el-empty v-if="!loading && !list.length" description="暂无通知" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { listNotice } from '@/api/notice'

const loading = ref(false)
const list = ref([])

async function loadData() {
  loading.value = true
  try {
    const res = await listNotice()
    list.value = res.data
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.notice-item {
  margin-bottom: 16px;
}
.notice-title {
  font-size: 17px;
  font-weight: bold;
  margin-bottom: 6px;
}
.notice-time {
  color: #c0c4cc;
  font-size: 12px;
  margin-bottom: 10px;
}
.notice-content {
  color: #606266;
  white-space: pre-wrap;
  line-height: 1.6;
}
</style>
