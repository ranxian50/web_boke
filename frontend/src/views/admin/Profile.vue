<template>
  <div class="profile">
    <el-card><template #header><span>个人设置</span></template>
      <el-form label-width="100px">
        <el-form-item label="用户名"><el-input :model-value="authStore.user.username" disabled /></el-form-item>
        <el-form-item label="昵称"><el-input :model-value="authStore.user.nickname" disabled /></el-form-item>
        <el-form-item label="角色"><el-tag type="danger">{{ authStore.user.role }}</el-tag></el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { useAuthStore } from '../../stores/auth'
const authStore = useAuthStore()
</script>
