<template>
  <div class="captcha-box">
    <div class="captcha-row">
      <!-- 验证码图片 -->
      <img
        v-if="captchaImage"
        :src="captchaImage"
        class="captcha-img"
        alt="验证码"
        @click="refreshCaptcha"
        title="点击刷新验证码"
      />
      <div v-else class="captcha-loading" @click="refreshCaptcha">
        <el-icon><Refresh /></el-icon> 加载验证码
      </div>
      <!-- 输入框 -->
      <el-input
        v-model="inputCode"
        placeholder="验证码"
        maxlength="4"
        size="large"
        class="captcha-input"
        @input="$emit('update:modelValue', { token: captchaToken, code: inputCode })"
      />
      <!-- 刷新按钮 -->
      <el-button
        :icon="Refresh"
        size="large"
        @click="refreshCaptcha"
        :loading="loading"
        title="刷新验证码"
      />
    </div>
    <div class="captcha-tip">点击图片可刷新验证码</div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import request from '../../api/request'

const emit = defineEmits(['update:modelValue'])

const captchaImage = ref('')
const captchaToken = ref('')
const inputCode = ref('')
const loading = ref(false)

/** 加载验证码 */
async function refreshCaptcha() {
  loading.value = true
  try {
    const data = await request.get('/captcha/generate')
    captchaToken.value = data.token
    captchaImage.value = data.image
    inputCode.value = ''
    emit('update:modelValue', { token: captchaToken.value, code: '' })
  } catch (e) {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}

onMounted(refreshCaptcha)
</script>

<style scoped>
.captcha-box {
  margin-bottom: 18px;
}
.captcha-row {
  display: flex;
  align-items: center;
  gap: 8px;
}
.captcha-img {
  width: 120px;
  height: 40px;
  border-radius: 4px;
  cursor: pointer;
  border: 1px solid #dcdfe6;
  flex-shrink: 0;
}
.captcha-loading {
  width: 120px;
  height: 40px;
  border-radius: 4px;
  border: 1px dashed #dcdfe6;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: #999;
  cursor: pointer;
  flex-shrink: 0;
}
.captcha-input {
  flex: 1;
}
.captcha-tip {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}
</style>
