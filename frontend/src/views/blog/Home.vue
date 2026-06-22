<template>
  <div class="home-page">
    <!-- 标签筛选栏 -->
    <div class="tag-bar" v-if="tags.length">
      <el-tag :type="activeTag === '' ? 'primary' : 'info'" @click="filterByTag('')" style="cursor:pointer">全部</el-tag>
      <el-tag v-for="tag in tags" :key="tag.name" :type="activeTag === tag.name ? 'primary' : 'info'" @click="filterByTag(tag.name)" style="cursor:pointer">{{ tag.name }}</el-tag>
    </div>
    <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
    <el-skeleton v-if="loading" :rows="5" animated />
    <el-empty v-if="!loading && articles.length === 0" description="暂无文章" />
    <Pagination :total="total" :current="page" @change="loadArticles" />
    <div class="recommend-section" v-if="randomArticles.length">
      <h3 class="section-title">🎯 猜你喜欢</h3>
      <ArticleCard v-for="article in randomArticles" :key="'r'+article.id" :article="article" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getArticles, getRandomArticles } from '../../api/article'
import { getTags } from '../../api/tag'
import ArticleCard from '../../components/common/ArticleCard.vue'
import Pagination from '../../components/common/Pagination.vue'

const articles = ref([]), randomArticles = ref([]), tags = ref([])
const total = ref(0), page = ref(1), activeTag = ref(''), loading = ref(false)

async function loadArticles(p = 1) {
  loading.value = true; page.value = p
  try {
    const params = { page: p, size: 10 }
    if (activeTag.value) params.tag = activeTag.value
    const result = await getArticles(params)
    articles.value = result.records; total.value = result.total
  } finally { loading.value = false }
}

function filterByTag(tag) { activeTag.value = tag; loadArticles(1) }

onMounted(async () => {
  loadArticles()
  tags.value = await getTags()
  randomArticles.value = await getRandomArticles(3)
})
</script>

<style scoped>
.tag-bar { display: flex; gap: 8px; flex-wrap: wrap; margin-bottom: 20px; padding: 12px 0; }
.recommend-section { margin-top: 40px; }
.section-title { font-size: 18px; color: #333; margin-bottom: 16px; padding-bottom: 8px; border-bottom: 2px solid #1890ff; }
</style>
