<template>
  <el-tag :type="type" size="small" style="cursor:pointer" @click.stop="$router.push(`/tag/${name}`)">{{ name }}</el-tag>
</template>

<script setup>
defineProps({ name: { type: String, required: true }, type: { type: String, default: 'primary' } })
</script>
