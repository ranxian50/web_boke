<template>
  <div class="article-card" @click="$router.push(`/article/${article.id}`)">
    <h3 class="article-title">{{ article.title }}</h3>
    <div class="article-badges" v-if="article.isPublic === false">
      <el-tag type="warning" size="small">私人</el-tag>
    </div>
    <p class="article-summary">{{ article.summary }}</p>
    <div class="article-meta">
      <div class="article-tags">
        <TagChip v-for="tag in article.tags" :key="tag" :name="tag" />
      </div>
      <div class="article-stats">
        <span><el-icon><View /></el-icon> {{ formatViewCount(article.viewCount) }}</span>
        <span><el-icon><Clock /></el-icon> {{ formatDate(article.createTime) }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { View, Clock } from '@element-plus/icons-vue'
import TagChip from './TagChip.vue'
import { formatDate, formatViewCount } from '../../utils/format'
defineProps({ article: { type: Object, required: true } })
</script>

<style scoped>
.article-card { background: #fff; border-radius: 8px; padding: 24px; margin-bottom: 16px; cursor: pointer; transition: transform 0.2s, box-shadow 0.2s; }
.article-card:hover { transform: translateY(-2px); box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
.article-title { font-size: 20px; color: #1a1a1a; margin-bottom: 8px; }
.article-title:hover { color: #1890ff; }
.article-summary { color: #666; font-size: 14px; line-height: 1.6; margin-bottom: 12px; }
.article-meta { display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 8px; }
.article-tags { display: flex; gap: 6px; flex-wrap: wrap; }
.article-stats { display: flex; gap: 16px; color: #999; font-size: 13px; }
.article-stats span { display: flex; align-items: center; gap: 4px; }
</style>
