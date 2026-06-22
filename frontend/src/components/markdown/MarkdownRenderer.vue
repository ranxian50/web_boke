<template>
  <div class="markdown-body" v-html="renderedContent"></div>
</template>

<script setup>
import { computed } from 'vue'
import { marked } from 'marked'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'

const props = defineProps({ content: { type: String, default: '' } })

marked.setOptions({
  breaks: true, gfm: true,
  highlight(code, lang) {
    if (lang && hljs.getLanguage(lang)) return hljs.highlight(code, { language: lang }).value
    return hljs.highlightAuto(code).value
  }
})

const renderedContent = computed(() => {
  if (!props.content) return ''
  return marked.parse(props.content)
})
</script>

<style scoped>
.markdown-body { line-height: 1.8; font-size: 15px; }
.markdown-body :deep(h1) { font-size: 2em; margin: 0.67em 0; }
.markdown-body :deep(h2) { font-size: 1.5em; margin: 0.83em 0; }
.markdown-body :deep(h3) { font-size: 1.17em; margin: 1em 0; }
.markdown-body :deep(p) { margin: 1em 0; }
.markdown-body :deep(code) { background: #f0f0f0; padding: 2px 6px; border-radius: 3px; font-size: 0.9em; }
.markdown-body :deep(pre) { background: #f6f8fa; padding: 16px; border-radius: 6px; overflow-x: auto; }
.markdown-body :deep(pre code) { background: none; padding: 0; }
.markdown-body :deep(img) { max-width: 100%; }
.markdown-body :deep(blockquote) { border-left: 4px solid #1890ff; padding-left: 16px; color: #666; margin: 1em 0; }
.markdown-body :deep(table) { border-collapse: collapse; width: 100%; }
.markdown-body :deep(th), .markdown-body :deep(td) { border: 1px solid #ddd; padding: 8px 12px; text-align: left; }
.markdown-body :deep(ul), .markdown-body :deep(ol) { padding-left: 2em; }
</style>
