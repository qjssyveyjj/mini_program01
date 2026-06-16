<template>
  <div :style="{ height: height }">
    <v-chart :option="option" autoresize />
  </div>
</template>

<script setup>
// ECharts 图表封装组件：接收配置 option 与高度
defineProps({
  option: {
    type: Object,
    required: true
  },
  height: {
    type: String,
    default: '300px'
  }
})
</script>
