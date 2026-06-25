<template>
  <div class="article-detail" v-loading="loading">
    <div v-if="article">
      <h1 class="detail-title">{{ article.title }}</h1>
      <div class="detail-meta">
        <span>👁️ {{ formatViewCount(article.viewCount) }} 次阅读</span>
        <span>📅 {{ formatDate(article.createTime) }}</span>
      </div>
      <div class="detail-tags" v-if="article.tags && article.tags.length">
        <TagChip v-for="tag in article.tags" :key="tag" :name="tag" />
      </div>
      <div class="detail-actions" v-if="article && article.id">
        <el-button type="danger" size="small" plain @click="showReportDialog = true">
          <el-icon><WarningFilled /></el-icon> 举报
        </el-button>
      </div>

      <!-- 举报对话框 -->
      <el-dialog v-model="showReportDialog" title="举报文章" width="450px">
        <el-form :model="reportForm" label-width="80px">
          <el-form-item label="举报人">
            <el-input v-model="reportForm.nickname" :placeholder="authStore.isLoggedIn ? authStore.user.nickname || '' : '请输入昵称'" />
          </el-form-item>
          <el-form-item label="举报原因">
            <el-select v-model="reportForm.reason" style="width:100%">
              <el-option label="垃圾广告" value="垃圾广告" />
              <el-option label="内容不实" value="内容不实" />
              <el-option label="侵权" value="侵权" />
              <el-option label="不友善内容" value="不友善内容" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          <el-form-item label="详细说明">
            <el-input v-model="reportForm.description" type="textarea" :rows="3" placeholder="请详细描述问题" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="showReportDialog = false">取消</el-button>
          <el-button type="danger" @click="submitReport" :loading="reportLoading">提交举报</el-button>
        </template>
      </el-dialog>

      <div class="detail-content">
        <MarkdownRenderer v-if="article.contentType === 'MARKDOWN'" :content="article.content" />
        <NotebookRenderer v-else-if="article.contentType === 'NOTEBOOK'" :content="article.content" />
        <MarkdownRenderer v-else :content="article.content" />
      </div>
      <CommentList :article-id="article.id" />
    </div>
    <el-empty v-else-if="!loading" description="文章不存在" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { WarningFilled } from '@element-plus/icons-vue'
import { getArticleDetail } from '../../api/article'
import { formatDate, formatViewCount } from '../../utils/format'
import MarkdownRenderer from '../../components/markdown/MarkdownRenderer.vue'
import NotebookRenderer from '../../components/notebook/NotebookRenderer.vue'
import TagChip from '../../components/common/TagChip.vue'
import CommentList from '../../components/common/CommentList.vue'
import { useAuthStore } from '../../stores/auth'
import request from '../../api/request'

const route = useRoute()
const authStore = useAuthStore()
const article = ref(null), loading = ref(false)

const showReportDialog = ref(false)
const reportLoading = ref(false)
const reportForm = reactive({
  nickname: '',
  reason: '垃圾广告',
  description: ''
})

async function submitReport() {
  if (!reportForm.nickname.trim() && !authStore.isLoggedIn) {
    ElMessage.warning('请输入昵称')
    return
  }
  reportLoading.value = true
  try {
    const nickname = reportForm.nickname || authStore.user.nickname || '匿名'
    await request.post('/reports', {
      articleId: article.value.id,
      nickname: nickname,
      reason: reportForm.reason,
      description: reportForm.description
    })
    ElMessage.success('举报已提交，管理员会尽快处理')
    showReportDialog.value = false
    reportForm.description = ''
  } catch(e) {} finally {
    reportLoading.value = false
  }
}

onMounted(async () => {
  loading.value = true
  try { article.value = await getArticleDetail(route.params.id) }
  finally { loading.value = false }
})
</script>

<style scoped>
.article-detail { background: #fff; border-radius: 8px; padding: 32px; }
.detail-title { font-size: 28px; color: #1a1a1a; margin-bottom: 12px; }
.detail-meta { display: flex; gap: 20px; color: #999; font-size: 14px; margin-bottom: 16px; }
.detail-tags { display: flex; gap: 6px; margin-bottom: 24px; }
.detail-content { line-height: 1.8; font-size: 15px; border-top: 1px solid #e8e8e8; padding-top: 24px; }
</style>
