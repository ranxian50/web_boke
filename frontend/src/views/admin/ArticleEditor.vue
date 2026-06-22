<template>
  <div class="article-editor">
    <el-card>
      <template #header><span>{{ isEdit ? '编辑文章' : '发布新文章' }}</span></template>
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" placeholder="文章标题" /></el-form-item>
        <el-form-item label="内容">
          <div class="upload-area">
            <el-upload :auto-upload="false" accept=".md,.ipynb" :on-change="handleFileChange" :show-file-list="false" :limit="1">
              <el-button type="info" size="small">上传 .md / .ipynb 文件</el-button>
            </el-upload>
            <span v-if="fileName" class="file-name">已选择: {{ fileName }}</span>
          </div>
          <el-input v-model="form.content" type="textarea" :rows="20" placeholder="在此编写 Markdown 内容，或上传文件自动填充" />
        </el-form-item>
        <el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" :rows="2" placeholder="留空则自动取前80字" /></el-form-item>
        <el-form-item label="类型">
          <el-radio-group v-model="form.contentType">
            <el-radio value="MARKDOWN">Markdown</el-radio>
            <el-radio value="NOTEBOOK">Jupyter Notebook</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio value="PUBLISHED">直接发布</el-radio>
            <el-radio value="DRAFT">存为草稿</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="公开权限">
          <el-radio-group v-model="form.isPublic">
            <el-radio :value="true">🌍 公开（所有人可见）</el-radio>
            <el-radio :value="false">🔒 私人（仅自己可见）</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="定时发布">
          <el-date-picker
            v-model="form.scheduledPublishTime"
            type="datetime"
            placeholder="选择定时发布时间（留空立即发布）"
            :disabled-date="d => d < new Date()"
            style="width: 100%"
          />
          <div class="form-tip">设置后文章将先保存为草稿，到时间自动发布</div>
        </el-form-item>
        <el-form-item label="自动转私人">
          <el-select v-model="form.autoPrivateDays" placeholder="永不自动转为私人" clearable style="width: 100%">
            <el-option :value="1" label="1天后自动转为私人" />
            <el-option :value="3" label="3天后自动转为私人" />
            <el-option :value="7" label="7天后自动转为私人" />
            <el-option :value="30" label="30天后自动转为私人" />
            <el-option :value="90" label="90天后自动转为私人" />
            <el-option :value="365" label="365天后自动转为私人" />
          </el-select>
          <div class="form-tip">文章发布指定天数后自动转为私人模式</div>
        </el-form-item>
        <el-form-item label="AI 标签">
          <div class="ai-tags">
            <el-tag v-for="tag in aiTags" :key="tag" type="success" style="margin-right:6px">{{ tag }}</el-tag>
            <span v-if="!aiTags.length && !aiLoading" class="tag-hint">发布后将自动生成标签</span>
            <el-tag v-if="aiLoading" type="warning">AI 正在分析中...</el-tag>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">{{ isEdit ? '保存修改' : '发布文章' }}</el-button>
          <el-button @click="$router.push('/admin/articles')">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createArticle, updateArticle, getArticleDetail } from '../../api/article'

const route = useRoute(), router = useRouter()
const isEdit = computed(() => !!route.params.id)
const submitting = ref(false), fileName = ref(''), aiTags = ref([]), aiLoading = ref(false)
const form = reactive({ title: '', content: '', summary: '', contentType: 'MARKDOWN', status: 'PUBLISHED', sourceFile: '', isPublic: true, scheduledPublishTime: null, autoPrivateDays: null })

function handleFileChange(f) {
  fileName.value = f.name
  form.contentType = f.name.endsWith('.ipynb') ? 'NOTEBOOK' : 'MARKDOWN'
  const reader = new FileReader()
  reader.onload = (e) => { form.content = e.target.result }
  reader.readAsText(f.raw)
}

async function handleSubmit() {
  if (!form.title.trim()) { ElMessage.warning('请输入标题'); return }
  if (!form.content.trim()) { ElMessage.warning('请输入内容'); return }
  submitting.value = true
  // 如果有定时发布时间，先存为草稿
  if (form.scheduledPublishTime) {
    form.status = 'DRAFT'
  }
  // 处理定时发布时间格式
  const submitData = { ...form }
  if (submitData.scheduledPublishTime) {
    submitData.scheduledPublishTime = new Date(submitData.scheduledPublishTime).toISOString()
  }
  try {
    if (isEdit.value) { await updateArticle(route.params.id, submitData); ElMessage.success('已更新') }
    else { await createArticle(submitData); ElMessage.success('已发布') }
    router.push('/admin/articles')
  } catch(e) {} finally { submitting.value = false }
}

onMounted(async () => {
  if (isEdit.value) {
    const a = await getArticleDetail(route.params.id)
    form.title = a.title; form.content = a.content; form.summary = a.summary
    form.contentType = a.contentType; form.status = a.status; aiTags.value = a.tags || []
  }
})
</script>

<style scoped>
.upload-area { margin-bottom: 12px; display: flex; align-items: center; gap: 12px; }
.file-name { color: #1890ff; font-size: 13px; }
.tag-hint { color: #999; font-size: 13px; }
.form-tip { font-size: 12px; color: #999; margin-top: 4px; }
</style>
