import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { loginApi } from '../api/auth'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || '')
  const user = ref(JSON.parse(localStorage.getItem('user') || 'null') || {})
  const isLoggedIn = computed(() => !!token.value)

  async function login(username, password, captchaToken, captchaCode) {
    const result = await loginApi({ username, password, captchaToken, captchaCode })
    token.value = result.token
    user.value = result.user
    localStorage.setItem('token', result.token)
    localStorage.setItem('user', JSON.stringify(result.user))
  }

  function logout() {
    token.value = ''
    user.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  function setUser(userData) {
    user.value = userData
    localStorage.setItem('user', JSON.stringify(userData))
  }

  return { token, user, isLoggedIn, login, logout, setUser }
})
