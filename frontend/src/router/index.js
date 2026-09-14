import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  // 前台游客页
  {
    path: '/',
    component: () => import('@/layouts/FrontLayout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'map',
        name: 'FrontMap',
        component: () => import('@/views/ProductMap.vue'),
        meta: { title: '产地地图' }
      },
      {
        path: 'notice',
        name: 'NoticeList',
        component: () => import('@/views/NoticeList.vue'),
        meta: { title: '通知公告' }
      },
      {
        path: 'publish',
        name: 'UserPublish',
        component: () => import('@/views/UserPublish.vue'),
        meta: { title: '发布产品', requiresAuth: true }
      }
    ]
  },
  // 后台管理（需管理员角色）
  {
    path: '/admin',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/admin/dashboard',
    meta: { requiresAdmin: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '数据看板' }
      },
      {
        path: 'products',
        name: 'ProductList',
        component: () => import('@/views/ProductList.vue'),
        meta: { title: '农产品管理' }
      },
      {
        path: 'notices',
        name: 'NoticeManage',
        component: () => import('@/views/NoticeManage.vue'),
        meta: { title: '通知管理' }
      },
      {
        path: 'map',
        name: 'AdminMap',
        component: () => import('@/views/ProductMap.vue'),
        meta: { title: '产地地图' }
      }
    ]
  },
  { path: '/:pathMatch(.*)*', redirect: '/home' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：后台需管理员；发布产品需登录
router.beforeEach((to) => {
  const userStore = useUserStore()
  if (to.matched.some((r) => r.meta.requiresAdmin)) {
    if (!userStore.isLoggedIn) {
      ElMessage.warning('请先登录')
      return '/login'
    }
    if (userStore.user?.role !== 'ADMIN') {
      ElMessage.warning('无权限访问后台')
      return '/home'
    }
  }
  if (to.matched.some((r) => r.meta.requiresAuth) && !userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    return '/login'
  }
})

export default router
