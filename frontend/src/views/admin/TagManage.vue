<template>
  <div class="tag-manage">
    <el-card>
      <template #header><div class="card-header"><span>标签管理</span></div></template>
      <el-tag v-for="tag in tags" :key="tag.name" style="margin:4px" size="large">{{ tag.name }} ({{ tag.articleCount }})</el-tag>
      <el-empty v-if="!tags.length" description="暂无标签" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getTags } from '../../api/tag'
const tags = ref([])
onMounted(async () => { tags.value = await getTags() })
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>
