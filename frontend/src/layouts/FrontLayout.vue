<template>
  <div class="front-layout">
    <header class="front-header">
      <div class="container header-inner">
        <div class="logo" @click="$router.push('/home')">乡镇农产品平台</div>
        <nav class="nav">
          <router-link to="/home" class="nav-item">首页</router-link>
          <router-link to="/map" class="nav-item">产地地图</router-link>
          <router-link to="/notice" class="nav-item">通知公告</router-link>
          <router-link v-if="userStore.isLoggedIn" to="/publish" class="nav-item">发布产品</router-link>
        </nav>
        <div class="actions">
          <el-button type="primary" plain @click="handleAction">
            {{ actionText }}
          </el-button>
        </div>
      </div>
    </header>
    <main class="container front-main">
      <router-view />
    </main>
    <footer class="front-footer">乡镇农产品信息发布平台</footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store'

const router = useRouter()
const userStore = useUserStore()

const actionText = computed(() => {
  if (!userStore.isLoggedIn) return '登录'
  return userStore.user?.role === 'ADMIN' ? '管理后台' : '退出登录'
})

function handleAction() {
  if (!userStore.isLoggedIn) {
    router.push('/login')
  } else if (userStore.user?.role === 'ADMIN') {
    router.push('/admin/dashboard')
  } else {
    userStore.logout()
    ElMessage.success('已退出登录')
  }
}
</script>

<style scoped>
.front-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}
.front-header {
  background: #2c6e49;
  color: #fff;
}
.container {
  width: 1100px;
  margin: 0 auto;
}
.header-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 60px;
}
.logo {
  font-size: 20px;
  font-weight: bold;
  cursor: pointer;
}
.nav {
  display: flex;
  gap: 28px;
}
.nav-item {
  color: #fff;
  text-decoration: none;
  font-size: 15px;
}
.nav-item.router-link-active {
  color: #ffd166;
  font-weight: bold;
}
.front-main {
  flex: 1;
  padding: 20px 0;
}
.front-footer {
  text-align: center;
  padding: 16px;
  color: #909399;
  font-size: 13px;
}
</style>
