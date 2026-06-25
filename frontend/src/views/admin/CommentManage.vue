<template>
  <div class="comment-manage">
    <el-card>
      <template #header><span>评论管理</span></template>
      <el-table :data="comments" stripe v-loading="loading" empty-text="暂无评论">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="articleId" label="文章ID" width="80" />
        <el-table-column prop="nickname" label="昵称" width="100" />
        <el-table-column prop="content" label="评论内容" min-width="300" show-overflow-tooltip />
        <el-table-column prop="createTime" label="时间" width="160" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="scope">
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <Pagination :total="total" :current="page" @change="loadComments" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../../api/request'
import Pagination from '../../components/common/Pagination.vue'

const comments = ref([])
const total = ref(0)
const page = ref(1)
const loading = ref(false)

async function loadComments(p = 1) {
  loading.value = true
  page.value = p
  try {
    const result = await request.get('/admin/comments', { params: { page: p, size: 10 } })
    comments.value = result.records || []
    total.value = result.total || 0
  } catch(e) {
  } finally {
    loading.value = false
  }
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定删除该评论吗？', '确认')
    await request.delete(`/admin/comments/${id}`)
    ElMessage.success('评论已删除')
    loadComments()
  } catch(e) {}
}

onMounted(loadComments)
</script>
