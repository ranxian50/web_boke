<template>
  <div class="comment-section">
    <h3 class="comment-title">💬 评论（{{ comments.length }}）</h3>
    <div class="comment-input">
      <el-form :model="form" inline>
        <el-form-item><el-input v-model="form.nickname" placeholder="昵称 *" size="small" style="width:150px" /></el-form-item>
        <el-form-item><el-input v-model="form.email" placeholder="邮箱（选填）" size="small" style="width:200px" /></el-form-item>
        <el-form-item style="width:100%"><el-input v-model="form.content" type="textarea" :rows="3" placeholder="写下你的评论..." /></el-form-item>
        <el-form-item><el-button type="primary" size="small" @click="submitComment">发表评论</el-button></el-form-item>
      </el-form>
    </div>
    <div v-if="comments.length" class="comments-list">
      <div v-for="comment in comments" :key="comment.id" class="comment-item" :style="{ marginLeft: (comment.depth || 0) * 24 + 'px' }">
        <div class="comment-body">
          <strong>{{ comment.nickname }}</strong>
          <span class="comment-time">{{ formatDate(comment.createTime) }}</span>
          <p class="comment-content">{{ comment.content }}</p>
          <el-button type="text" size="small" @click="replyTo(comment)">回复</el-button>
          <el-button v-if="authStore.isLoggedIn" type="text" size="small" style="color:#f56c6c" @click="handleDelete(comment.id)">删除</el-button>
          <!-- 递归渲染子回复 -->
          <div v-if="comment.replies && comment.replies.length" class="comment-replies">
            <div v-for="reply in comment.replies" :key="reply.id" class="reply-item" :style="{ marginLeft: ((reply.depth || 0) - (comment.depth || 0)) * 24 + 'px' }">
              <div class="comment-body">
                <strong>{{ reply.nickname }}</strong>
                <span class="comment-time">{{ formatDate(reply.createTime) }}</span>
                <p class="comment-content">{{ reply.content }}</p>
                <el-button type="text" size="small" @click="replyTo(reply)">回复</el-button>
                <el-button v-if="authStore.isLoggedIn" type="text" size="small" style="color:#f56c6c" @click="handleDelete(reply.id)">删除</el-button>
              </div>
              <!-- 更深层嵌套递归展示 -->
              <template v-if="reply.replies && reply.replies.length">
                <div v-for="r2 in reply.replies" :key="r2.id" class="reply-item" :style="{ marginLeft: '24px' }">
                  <div class="comment-body">
                    <strong>{{ r2.nickname }}</strong>
                    <span class="comment-time">{{ formatDate(r2.createTime) }}</span>
                    <p class="comment-content">{{ r2.content }}</p>
                    <el-button type="text" size="small" @click="replyTo(r2)">回复</el-button>
                    <el-button v-if="authStore.isLoggedIn" type="text" size="small" style="color:#f56c6c" @click="handleDelete(r2.id)">删除</el-button>
                  </div>
                  <!-- 更深层...理论上可以继续嵌套，但我们用深度标记控制 -->
                  <div v-if="r2.replies && r2.replies.length" style="margin-left:24px;padding-left:12px;border-left:2px solid #e8e8e8">
                    <div v-for="r3 in r2.replies" :key="r3.id" class="reply-item">
                      <div class="comment-body">
                        <strong>{{ r3.nickname }}</strong>
                        <span class="comment-time">{{ formatDate(r3.createTime) }}</span>
                        <p class="comment-content">{{ r3.content }}</p>
                        <el-button type="text" size="small" @click="replyTo(r3)">回复</el-button>
                        <el-button v-if="authStore.isLoggedIn" type="text" size="small" style="color:#f56c6c" @click="handleDelete(r3.id)">删除</el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </template>
            </div>
          </div>
        </div>
      </div>
    </div>
    <el-empty v-else description="暂无评论，快来抢沙发吧！" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getComments, createComment, deleteComment } from '../../api/comment'
import { useAuthStore } from '../../stores/auth'
import { formatDate } from '../../utils/format'

const props = defineProps({ articleId: { type: [String, Number], required: true } })
const authStore = useAuthStore()
const comments = ref([])
const replyToId = ref(null)
const form = reactive({ nickname: '', email: '', content: '', parentId: null })

async function loadComments() {
  try { comments.value = await getComments(props.articleId) }
  catch(e) { comments.value = [] }
}

async function submitComment() {
  if (!form.nickname.trim()) { ElMessage.warning('请输入昵称'); return }
  if (!form.content.trim()) { ElMessage.warning('请输入评论内容'); return }
  try {
    form.parentId = replyToId.value
    await createComment(props.articleId, form)
    ElMessage.success('评论发表成功')
    form.content = ''; replyToId.value = null
    loadComments()
  } catch(e) {}
}

function replyTo(comment) { replyToId.value = comment.id; form.content = '' }

async function handleDelete(id) {
  try { await deleteComment(id); ElMessage.success('评论已删除'); loadComments() }
  catch(e) {}
}

onMounted(loadComments)
</script>

<style scoped>
.comment-section { margin-top: 40px; padding-top: 24px; border-top: 1px solid #e8e8e8; }
.comment-title { font-size: 18px; margin-bottom: 20px; }
.comment-input { margin-bottom: 24px; padding: 16px; background: #f9f9f9; border-radius: 8px; }
.comment-item { margin-bottom: 16px; }
.comment-time { color: #999; font-size: 12px; margin-left: 8px; }
.comment-content { margin: 8px 0; line-height: 1.6; }
.replies { margin-left: 24px; padding-left: 16px; border-left: 2px solid #e8e8e8; margin-top: 8px; }
.reply-item { margin-bottom: 12px; }
</style>
