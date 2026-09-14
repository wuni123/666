<template>
  <div>
    <el-row :gutter="16">
      <el-col :span="8" v-for="card in cards" :key="card.label">
        <el-card class="stat-card">
          <div class="stat-value">{{ card.value }}</div>
          <div class="stat-label">{{ card.label }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="chart-card">
      <template #header>各品类数量统计</template>
      <div ref="chartRef" class="chart"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getDashboardStats } from '@/api/dashboard'

const chartRef = ref(null)
let chart = null

const cards = reactive([
  { label: '农产品总数', value: 0 },
  { label: '已上架', value: 0 },
  { label: '品类数量', value: 0 }
])

async function loadData() {
  const res = await getDashboardStats()
  const data = res.data
  cards[0].value = data.productTotal ?? 0
  cards[1].value = data.onSaleTotal ?? 0
  cards[2].value = data.categoryTotal ?? 0
  renderChart(data.categoryStats || [])
}

function renderChart(stats) {
  if (!chartRef.value) return
  if (!chart) {
    chart = echarts.init(chartRef.value)
  }
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [
      {
        type: 'pie',
        radius: ['40%', '65%'],
        data: stats.map((s) => ({ name: s.name, value: s.value })),
        label: { formatter: '{b}: {c}' },
        emphasis: {
          itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.3)' }
        }
      }
    ]
  })
}

function handleResize() {
  chart?.resize()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  chart?.dispose()
  chart = null
})
</script>

<style scoped>
.stat-card {
  text-align: center;
}
.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #2c6e49;
}
.stat-label {
  margin-top: 8px;
  color: #909399;
}
.chart-card {
  margin-top: 16px;
}
.chart {
  height: 360px;
}
</style>
