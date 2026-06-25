<template>
  <div class="profile">
    <el-card>
      <template #header><span>个人设置</span></template>
      <el-form label-width="100px" :model="form">
        <el-form-item label="用户名">
          <el-input :model-value="authStore.user.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="角色">
          <el-tag :type="authStore.user.role === 'ADMIN' ? 'danger' : 'info'">{{ authStore.user.role }}</el-tag>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdate" :loading="saving">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../../stores/auth'
import request from '../../api/request'

const authStore = useAuthStore()
const saving = ref(false)

const form = reactive({
  nickname: ''
})

async function handleUpdate() {
  if (!form.nickname.trim()) {
    ElMessage.warning('昵称不能为空')
    return
  }
  saving.value = true
  try {
    const userInfo = await request.put('/auth/profile', { nickname: form.nickname })
    authStore.setUser(userInfo)
    ElMessage.success('信息已更新')
  } catch(e) {
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  form.nickname = authStore.user.nickname || ''
})
</script>
