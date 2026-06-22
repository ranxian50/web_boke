<template>
  <el-container class="admin-layout">
    <el-aside width="220px" class="admin-sidebar">
      <div class="sidebar-header">📝 博客管理</div>
      <el-menu :default-active="route.path" router background-color="#001529" text-color="#fff" active-text-color="#1890ff">
        <el-menu-item index="/admin"><el-icon><DataAnalysis /></el-icon><span>仪表盘</span></el-menu-item>
        <el-menu-item index="/admin/articles"><el-icon><Document /></el-icon><span>文章管理</span></el-menu-item>
        <el-menu-item index="/admin/articles/new"><el-icon><Edit /></el-icon><span>发布文章</span></el-menu-item>
        <el-menu-item index="/admin/tags"><el-icon><CollectionTag /></el-icon><span>标签管理</span></el-menu-item>
        <el-menu-item index="/admin/comments"><el-icon><ChatDotSquare /></el-icon><span>评论管理</span></el-menu-item>
        <el-menu-item index="/admin/reports"><el-icon><WarningFilled /></el-icon><span>举报管理</span></el-menu-item>
        <el-menu-item index="/admin/profile"><el-icon><User /></el-icon><span>个人设置</span></el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="admin-header">
        <span>欢迎回来，{{ authStore.user.nickname || '管理员' }}</span>
        <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
      </el-header>
      <el-main class="admin-main"><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { DataAnalysis, Document, Edit, CollectionTag, ChatDotSquare, User, WarningFilled } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout { min-height: 100vh; }
.admin-sidebar { background: #001529; }
.sidebar-header { height: 60px; line-height: 60px; text-align: center; color: #fff; font-size: 16px; font-weight: bold; border-bottom: 1px solid #0d2137; }
.admin-header { background: #fff; display: flex; justify-content: flex-end; align-items: center; gap: 16px; border-bottom: 1px solid #e8e8e8; font-size: 14px; color: #666; }
.admin-main { background: #f0f2f5; min-height: calc(100vh - 60px); }
</style>
