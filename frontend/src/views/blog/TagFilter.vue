<template>
  <div class="tag-filter-page">
    <h2 class="page-title">标签：{{ tagName }}</h2>
    <ArticleCard v-for="article in articles" :key="article.id" :article="article" />
    <el-empty v-if="!loading && articles.length === 0" description="该标签下暂无文章" />
    <Pagination :total="total" :current="page" @change="loadArticles" />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getArticles } from '../../api/article'
import ArticleCard from '../../components/common/ArticleCard.vue'
import Pagination from '../../components/common/Pagination.vue'

const route = useRoute()
const articles = ref([]), total = ref(0), page = ref(1), loading = ref(false), tagName = ref('')

async function loadArticles(p = 1) {
  loading.value = true; page.value = p
  try {
    const result = await getArticles({ page: p, size: 10, tag: tagName.value })
    articles.value = result.records; total.value = result.total
  } finally { loading.value = false }
}

watch(() => route.params.tagName, (n) => { tagName.value = n; loadArticles(1) })
onMounted(() => { tagName.value = route.params.tagName; loadArticles() })
</script>

<style scoped>
.page-title { font-size: 22px; margin-bottom: 20px; color: #333; }
</style>
