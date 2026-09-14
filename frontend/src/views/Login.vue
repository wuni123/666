<template>
  <div class="login-page">
    <el-card class="login-card">
      <h2 class="title">乡镇农产品信息发布平台</h2>
      <el-tabs v-model="activeTab" stretch>
        <el-tab-pane label="登录" name="login">
          <el-form :model="loginForm" @keyup.enter="handleLogin">
            <el-form-item>
              <el-input v-model="loginForm.username" placeholder="用户名" :prefix-icon="User" />
            </el-form-item>
            <el-form-item>
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="密码"
                show-password
                :prefix-icon="Lock"
              />
            </el-form-item>
            <el-button type="primary" class="submit" :loading="loading" @click="handleLogin">
              登录
            </el-button>
          </el-form>
        </el-tab-pane>

        <el-tab-pane label="注册" name="register">
          <el-form :model="registerForm">
            <el-form-item>
              <el-input v-model="registerForm.username" placeholder="用户名（3-20位）" />
            </el-form-item>
            <el-form-item>
              <el-input
                v-model="registerForm.password"
                type="password"
                placeholder="密码（6-32位）"
                show-password
              />
            </el-form-item>
            <el-form-item>
              <el-input v-model="registerForm.realName" placeholder="姓名（选填）" />
            </el-form-item>
            <el-form-item>
              <el-input v-model="registerForm.phone" placeholder="手机号（选填）" />
            </el-form-item>
            <el-button type="primary" class="submit" :loading="loading" @click="handleRegister">
              注册
            </el-button>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login, register } from '@/api/auth'
import { useUserStore } from '@/store'

const router = useRouter()
const userStore = useUserStore()

const activeTab = ref('login')
const loading = ref(false)

const loginForm = reactive({ username: '', password: '' })
const registerForm = reactive({ username: '', password: '', realName: '', phone: '' })

async function handleLogin() {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await login(loginForm)
    userStore.setLogin(res.data.token, res.data.user)
    ElMessage.success('登录成功')
    const path = res.data.user?.role === 'ADMIN' ? '/admin/dashboard' : '/home'
    router.push(path)
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  if (!registerForm.username || !registerForm.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    await register(registerForm)
    ElMessage.success('注册成功，请登录')
    activeTab.value = 'login'
    loginForm.username = registerForm.username
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #2c6e49 0%, #4c956c 100%);
}
.login-card {
  width: 400px;
  padding: 12px 8px;
}
.title {
  text-align: center;
  color: #2c6e49;
  margin: 8px 0 20px;
}
.submit {
  width: 100%;
}
</style>
