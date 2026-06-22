<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="login-title">管理员登录</h2>
      <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <!-- 人机验证 -->
        <el-form-item prop="captcha">
          <CaptchaBox v-model="captcha" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="login-btn" :loading="loading" native-type="submit">登 录</el-button>
        </el-form-item>
        <div class="register-link">
          没有账号？<router-link to="/register">立即注册</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../../stores/auth'
import CaptchaBox from '../../components/common/CaptchaBox.vue'
import request from '../../api/request'

const router = useRouter()
const authStore = useAuthStore()
const formRef = ref(null)
const loading = ref(false)
const captcha = ref({ token: '', code: '' })

const form = reactive({ username: '', password: '' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  if (!captcha.value.code) {
    ElMessage.warning('请完成人机验证')
    return
  }
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await request.post('/auth/login', {
      username: form.username,
      password: form.password,
      captchaToken: captcha.value.token,
      captchaCode: captcha.value.code
    })
    authStore.token = res.token
    authStore.user = res.user
    localStorage.setItem('token', res.token)
    localStorage.setItem('user', JSON.stringify(res.user))
    ElMessage.success('登录成功')
    router.push('/admin')
  } catch (e) { /* 错误已在拦截器中处理 */ }
  finally { loading.value = false }
}
</script>

<style scoped>
.login-container { display: flex; justify-content: center; align-items: center; min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.login-card { width: 400px; padding: 40px; background: white; border-radius: 12px; box-shadow: 0 20px 60px rgba(0,0,0,0.15); }
.login-title { text-align: center; margin-bottom: 30px; font-size: 24px; color: #333; }
.login-btn { width: 100%; }
.register-link { text-align: center; margin-top: 12px; font-size: 14px; color: #999; }
.register-link a { color: #1890ff; text-decoration: none; }
</style>
