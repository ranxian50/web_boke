<template>
  <div class="register-container">
    <div class="register-card">
      <h2 class="register-title">用户注册</h2>
      <el-form ref="formRef" :model="form" :rules="rules" @submit.prevent="handleRegister">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="nickname">
          <el-input v-model="form.nickname" placeholder="昵称（选填）" prefix-icon="Edit" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码（至少6位）" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="确认密码" prefix-icon="Lock" size="large" show-password />
        </el-form-item>
        <!-- 人机验证 -->
        <el-form-item prop="captcha">
          <CaptchaBox v-model="captcha" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" class="register-btn" :loading="loading" native-type="submit">注 册</el-button>
        </el-form-item>
        <div class="login-link">
          已有账号？<router-link to="/login">立即登录</router-link>
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

const form = reactive({
  username: '',
  nickname: '',
  password: '',
  confirmPassword: ''
})

const validatePass = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度3-50个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validatePass, trigger: 'blur' }
  ]
}

async function handleRegister() {
  if (!captcha.value.code) {
    ElMessage.warning('请完成人机验证')
    return
  }
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res = await request.post('/auth/register', {
      username: form.username,
      password: form.password,
      nickname: form.nickname || form.username,
      captchaToken: captcha.value.token,
      captchaCode: captcha.value.code
    })
    // 注册成功后自动登录
    authStore.token = res.token
    authStore.user = res.user
    localStorage.setItem('token', res.token)
    localStorage.setItem('user', JSON.stringify(res.user))
    ElMessage.success('注册成功！')
    router.push('/')
  } catch (e) {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container { display: flex; justify-content: center; align-items: center; min-height: 100vh; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); }
.register-card { width: 420px; padding: 40px; background: white; border-radius: 12px; box-shadow: 0 20px 60px rgba(0,0,0,0.15); }
.register-title { text-align: center; margin-bottom: 30px; font-size: 24px; color: #333; }
.register-btn { width: 100%; }
.login-link { text-align: center; margin-top: 16px; font-size: 14px; color: #999; }
.login-link a { color: #1890ff; text-decoration: none; }
</style>
