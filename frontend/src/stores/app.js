import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
  const siteName = ref('技术博客')
  const loading = ref(false)
  return { siteName, loading }
})
