<template>
  <div class="my-articles">
    <h2 class="page-title">我的文章</h2>
    <el-tabs v-model="activeTab" @tab-change="loadArticles">
      <el-tab-pane label="已发布" name="published">
        <ArticleCard v-for="article in publishedArticles" :key="article.id" :article="article" />
      </el-tab-pane>
      <el-tab-pane label="草稿" name="draft">
        <ArticleCard v-for="article in draftArticles" :key="article.id" :article="article" />
      </el-tab-pane>
    </el-tabs>
    <el-empty v-if="!loading && publishedArticles.length === 0 && draftArticles.length === 0" description="暂无文章，去发布第一篇吧！" />
    <div style="text-align:center;margin-top:20px">
      <el-button type="primary" @click="$router.push('/admin/articles/new')">发布新文章</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '../../api/request'
import ArticleCard from '../../components/common/ArticleCard.vue'

const publishedArticles = ref([])
const draftArticles = ref([])
const loading = ref(false)
const activeTab = ref('published')

async function loadArticles() {
  loading.value = true
  try {
    const result = await request.get('/articles/my', { params: { page: 1, size: 50 } })
    publishedArticles.value = (result.records || result).filter(a => a.status === 'PUBLISHED')
    draftArticles.value = (result.records || result).filter(a => a.status === 'DRAFT')
  } catch(e) {
  } finally {
    loading.value = false
  }
}

onMounted(loadArticles)
</script>

<style scoped>
.page-title { font-size: 22px; margin-bottom: 20px; }
</style>
