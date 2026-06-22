import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('../layouts/BlogLayout.vue'),
    children: [
      { path: '', name: 'Home', component: () => import('../views/blog/Home.vue') },
      { path: 'article/:id', name: 'ArticleDetail', component: () => import('../views/blog/ArticleDetail.vue') },
      { path: 'tag/:tagName', name: 'TagFilter', component: () => import('../views/blog/TagFilter.vue') },
      { path: 'my-articles', name: 'MyArticles', component: () => import('../views/blog/MyArticles.vue'), meta: { requiresAuth: true } },
    ]
  },
  { path: '/login', name: 'Login', component: () => import('../views/blog/Login.vue') },
  { path: '/register', name: 'Register', component: () => import('../views/blog/Register.vue') },
  {
    path: '/admin',
    component: () => import('../layouts/AdminLayout.vue'),
    meta: { requiresAuth: true },
    children: [
      { path: '', name: 'Dashboard', component: () => import('../views/admin/Dashboard.vue') },
      { path: 'articles', name: 'ArticleList', component: () => import('../views/admin/ArticleList.vue') },
      { path: 'articles/new', name: 'ArticleNew', component: () => import('../views/admin/ArticleEditor.vue') },
      { path: 'articles/:id/edit', name: 'ArticleEdit', component: () => import('../views/admin/ArticleEditor.vue') },
      { path: 'comments', name: 'CommentManage', component: () => import('../views/admin/CommentManage.vue') },
      { path: 'reports', name: 'ReportManage', component: () => import('../views/admin/ReportManage.vue') },
      { path: 'tags', name: 'TagManage', component: () => import('../views/admin/TagManage.vue') },
      { path: 'profile', name: 'Profile', component: () => import('../views/admin/Profile.vue') },
    ]
  }
]

const router = createRouter({ history: createWebHistory(), routes })

// 路由守卫 — 未登录访问后台时跳转登录页
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('token')
    if (!token) { next('/login'); return }
  }
  next()
})

export default router
