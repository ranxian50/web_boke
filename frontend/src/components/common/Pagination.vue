<template>
  <div class="pagination-wrapper" v-if="total > size">
    <el-pagination background layout="prev, pager, next" :total="total" :page-size="size" :current-page="current" @current-change="$emit('change', $event)" />
  </div>
</template>

<script setup>
defineProps({ total: { type: Number, default: 0 }, size: { type: Number, default: 10 }, current: { type: Number, default: 1 } })
defineEmits(['change'])
</script>

<style scoped>
.pagination-wrapper { display: flex; justify-content: center; margin: 32px 0; }
</style>
