<template>
  <div class="blog-layout">
    <header class="blog-header">
      <div class="header-inner">
        <router-link to="/" class="blog-logo">📝 技术博客</router-link>
        <nav class="blog-nav">
          <router-link to="/">首页</router-link>
          <router-link v-if="authStore.isLoggedIn" to="/my-articles">我的文章</router-link>
          <router-link v-if="authStore.isLoggedIn" to="/admin">管理后台</router-link>
          <template v-else>
            <router-link to="/login">登录</router-link>
            <router-link to="/register">注册</router-link>
          </template>
        </nav>
      </div>
    </header>
    <main class="blog-main"><router-view /></main>
    <footer class="blog-footer">
      <p>© {{ new Date().getFullYear() }} 个人技术博客</p>
    </footer>
  </div>
</template>

<script setup>
import { useAuthStore } from '../stores/auth'
const authStore = useAuthStore()
</script>

<style scoped>
.blog-layout { min-height: 100vh; display: flex; flex-direction: column; }
.blog-header { background: #fff; border-bottom: 1px solid #e8e8e8; position: sticky; top: 0; z-index: 100; }
.header-inner { max-width: 900px; margin: 0 auto; padding: 0 20px; height: 60px; display: flex; justify-content: space-between; align-items: center; }
.blog-logo { font-size: 20px; font-weight: bold; text-decoration: none; color: #333; }
.blog-nav { display: flex; gap: 20px; }
.blog-nav a { text-decoration: none; color: #666; font-size: 14px; }
.blog-nav a:hover { color: #1890ff; }
.blog-main { flex: 1; max-width: 900px; width: 100%; margin: 24px auto; padding: 0 20px; }
.blog-footer { text-align: center; padding: 24px; color: #999; font-size: 13px; border-top: 1px solid #e8e8e8; }
</style>
