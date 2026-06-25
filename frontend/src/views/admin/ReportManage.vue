<template>
  <div class="report-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>举报管理 <el-tag v-if="pendingCount > 0" type="danger" size="small">{{ pendingCount }} 条待处理</el-tag></span>
          <el-radio-group v-model="filterStatus" size="small" @change="loadReports">
            <el-radio-button value="">全部</el-radio-button>
            <el-radio-button value="PENDING">待处理</el-radio-button>
            <el-radio-button value="RESOLVED">已处理</el-radio-button>
            <el-radio-button value="DISMISSED">已驳回</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <el-table :data="reports" stripe v-loading="loading" empty-text="暂无举报">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="reporterNickname" label="举报人" width="100" />
        <el-table-column prop="articleTitle" label="被举报文章" min-width="200" show-overflow-tooltip />
        <el-table-column prop="reason" label="原因" width="120" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.status === 'PENDING' ? 'danger' : scope.row.status === 'RESOLVED' ? 'success' : 'info'" size="small">
              {{ scope.row.status === 'PENDING' ? '待处理' : scope.row.status === 'RESOLVED' ? '已处理' : '已驳回' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="时间" width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button v-if="scope.row.status === 'PENDING'" size="small" type="success" @click="handleReport(scope.row.id, 'RESOLVED')">处理</el-button>
            <el-button v-if="scope.row.status === 'PENDING'" size="small" type="warning" @click="handleReport(scope.row.id, 'DISMISSED')">驳回</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '../../api/request'

const reports = ref([])
const pendingCount = ref(0)
const loading = ref(false)
const filterStatus = ref('PENDING')

async function loadReports() {
  loading.value = true
  try {
    const params = { page: 1, size: 50 }
    if (filterStatus.value) params.status = filterStatus.value
    const result = await request.get('/reports', { params })
    reports.value = result.records || []
    // 获取待处理数
    const count = await request.get('/reports/pending-count')
    pendingCount.value = typeof count === 'number' ? count : count || 0
  } catch(e) {
  } finally {
    loading.value = false
  }
}

async function handleReport(id, action) {
  const actionText = action === 'RESOLVED' ? '处理' : '驳回'
  try {
    await ElMessageBox.confirm(`确定${actionText}该举报吗？`, '确认')
    await request.put(`/reports/${id}/handle`, { action })
    ElMessage.success(`举报已${actionText}`)
    loadReports()
  } catch(e) {}
}

onMounted(loadReports)
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
