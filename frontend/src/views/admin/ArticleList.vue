<template>
  <div class="article-list">
    <el-card>
      <template #header>
        <div class="card-header"><span>文章管理</span><el-button type="primary" size="small" @click="$router.push('/admin/articles/new')">发布文章</el-button></div>
      </template>
      <el-table :data="articles" stripe v-loading="loading">
        <el-table-column prop="title" label="标题" min-width="250" show-overflow-tooltip />
        <el-table-column prop="contentType" label="类型" width="100">
          <template #default="scope"><el-tag :type="scope.row.contentType === 'MARKDOWN' ? 'success' : 'warning'" size="small">{{ scope.row.contentType }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="viewCount" label="阅读" width="70" />
        <el-table-column prop="createTime" label="发布时间" width="160">
          <template #default="scope">{{ formatDate(scope.row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="scope">
            <el-button size="small" @click="$router.push(`/admin/articles/${scope.row.id}/edit`)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <Pagination :total="total" :current="page" @change="loadArticles" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getArticles, deleteArticle } from '../../api/article'
import { formatDate } from '../../utils/format'
import Pagination from '../../components/common/Pagination.vue'

const articles = ref([]), total = ref(0), page = ref(1), loading = ref(false)

async function loadArticles(p = 1) {
  loading.value = true; page.value = p
  try { const r = await getArticles({ page: p, size: 10 }); articles.value = r.records; total.value = r.total }
  finally { loading.value = false }
}

async function handleDelete(id) {
  try { await ElMessageBox.confirm('确定删除该文章吗？', '确认'); await deleteArticle(id); ElMessage.success('已删除'); loadArticles() }
  catch(e) {}
}

onMounted(() => loadArticles())
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
