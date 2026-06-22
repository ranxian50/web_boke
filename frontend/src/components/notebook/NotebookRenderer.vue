<template>
  <div class="notebook-body">
    <div v-for="cell in cells" :key="cell.index" class="notebook-cell">
      <div v-if="cell.cellType === 'markdown'" class="cell-markdown">
        <MarkdownRenderer :content="cell.source" />
      </div>
      <div v-else-if="cell.cellType === 'code'" class="cell-code">
        <div class="cell-header">
          <el-tag size="small" type="info">[{{ cell.index }}]</el-tag>
          <span class="cell-label">代码</span>
        </div>
        <MarkdownRenderer :content="'```\n' + cell.source + '\n```'" />
        <div v-if="cell.outputs && cell.outputs.length" class="cell-outputs">
          <div v-for="(output, oi) in cell.outputs" :key="oi" class="cell-output">
            <pre v-if="output.type === 'text'" class="output-text">{{ output.data }}</pre>
            <img v-else-if="output.type === 'image'" :src="'data:image/png;base64,' + output.data" class="output-image" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { parseNotebook } from '../../utils/parseNotebook'
import MarkdownRenderer from '../markdown/MarkdownRenderer.vue'

const props = defineProps({ content: { type: String, default: '' } })
const cells = computed(() => {
  if (!props.content) return []
  return parseNotebook(props.content)
})
</script>

<style scoped>
.notebook-body { max-width: 100%; }
.notebook-cell { margin-bottom: 16px; border: 1px solid #e8e8e8; border-radius: 6px; overflow: hidden; }
.cell-header { display: flex; align-items: center; gap: 8px; padding: 8px 12px; background: #fafafa; border-bottom: 1px solid #e8e8e8; }
.cell-label { font-size: 12px; color: #999; }
.cell-markdown { padding: 12px; }
.cell-code { background: #f6f8fa; }
.cell-outputs { border-top: 1px solid #e8e8e8; background: #fff; padding: 12px; }
.output-text { background: #f5f5f5; padding: 8px; border-radius: 4px; font-size: 13px; overflow-x: auto; }
.output-image { max-width: 100%; }
</style>
