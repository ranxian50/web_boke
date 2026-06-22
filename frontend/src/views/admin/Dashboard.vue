<template>
  <div class="dashboard">
    <h2>仪表盘</h2>
    <el-row :gutter="20" style="margin-top:20px">
      <el-col :span="6"><el-card shadow="hover"><div class="stat-card"><div class="stat-value">{{ stats.articleCount }}</div><div class="stat-label">文章总数</div></div></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover"><div class="stat-card"><div class="stat-value">{{ stats.tagCount }}</div><div class="stat-label">标签数</div></div></el-card></el-col>
      <el-col :span="6"><el-card shadow="hover"><div class="stat-card"><div class="stat-value" style="color:#f56c6c">{{ stats.pendingReports }}</div><div class="stat-label">待处理举报</div></div></el-card></el-col>
    </el-row>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue'
import { getArticles } from '../../api/article'
import { getTags } from '../../api/tag'
import request from '../../api/request'

const stats = reactive({ articleCount: 0, tagCount: 0, pendingReports: 0 })

onMounted(async () => {
  try { const r = await getArticles({ page: 1, size: 1 }); stats.articleCount = r.total } catch(e) {}
  try { const t = await getTags(); stats.tagCount = t.length } catch(e) {}
  try { const count = await request.get('/reports/pending-count'); stats.pendingReports = typeof count === 'number' ? count : count || 0 } catch(e) { stats.pendingReports = 0 }
})
</script>

<style scoped>
.stat-card { text-align: center; padding: 10px 0; }
.stat-value { font-size: 36px; font-weight: bold; color: #1890ff; }
.stat-label { font-size: 14px; color: #999; margin-top: 8px; }
</style>
