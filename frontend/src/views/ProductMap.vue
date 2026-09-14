<template>
  <el-card>
    <div id="map-container" class="map-container"></div>
  </el-card>
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import AMapLoader from '@amap/amap-jsapi-loader'
import { listMapPoints } from '@/api/product'

let map = null

function escapeHtml(str) {
  if (str === null || str === undefined) return ''
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}

function buildContent(p) {
  return `
    <div style="padding:4px 8px;max-width:220px">
      <div style="font-weight:bold;margin-bottom:4px">${escapeHtml(p.name)}</div>
      <div style="color:#666;font-size:12px">${escapeHtml(p.origin)}</div>
      <div style="color:#e6a23c;margin-top:4px">${p.price ?? '-'} 元/${escapeHtml(p.unit) || '件'}</div>
    </div>`
}

async function initMap() {
  window._AMapSecurityConfig = {
    securityJsCode: import.meta.env.VITE_AMAP_SECURITY_CODE
  }

  const AMap = await AMapLoader.load({
    key: import.meta.env.VITE_AMAP_KEY,
    version: '2.0'
  })

  map = new AMap.Map('map-container', {
    viewMode: '2D',
    zoom: 10,
    center: [115.86, 25.85]
  })

  const res = await listMapPoints()
  const points = res.data || []
  if (!points.length) {
    ElMessage.info('暂无带坐标的农产品，请先在农产品管理中设置经纬度')
    return
  }

  const markers = points.map((p) => {
    const marker = new AMap.Marker({
      position: [p.longitude, p.latitude],
      title: p.name
    })
    marker.on('click', () => {
      const infoWindow = new AMap.InfoWindow({
        content: buildContent(p),
        offset: new AMap.Pixel(0, -30)
      })
      infoWindow.open(map, marker.getPosition())
    })
    return marker
  })

  map.add(markers)
  map.setFitView(markers)
}

onMounted(initMap)

onUnmounted(() => {
  map?.destroy()
  map = null
})
</script>

<style scoped>
.map-container {
  width: 100%;
  height: 600px;
}
</style>
