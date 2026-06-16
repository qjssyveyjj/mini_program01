<template>
  <el-card>
    <template #header>
      <div class="header">
        <span>健康数据记录</span>
        <el-button :icon="Refresh" @click="loadData">刷新</el-button>
      </div>
    </template>

    <el-table :data="records" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="70" />
      <el-table-column prop="userId" label="用户ID" width="90" />
      <el-table-column prop="timestamp" label="测量时间" :formatter="formatTime" />
      <el-table-column label="血压(mmHg)">
        <template #default="{ row }">
          {{ row.systolic ?? '-' }} / {{ row.diastolic ?? '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="heartRate" label="心率" width="90" />
      <el-table-column prop="bloodOxygen" label="血氧(%)" width="90" />
      <el-table-column prop="weight" label="体重(kg)" width="100" />
      <el-table-column prop="notes" label="备注" show-overflow-tooltip />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button type="danger" size="small" @click="onDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import request from '@/utils/request'

const records = ref([])
const loading = ref(false)

function formatTime(row, column, value) {
  return value ? new Date(value).toLocaleString('zh-CN') : '-'
}

async function loadData() {
  loading.value = true
  try {
    records.value = await request.get('/health/all')
  } finally {
    loading.value = false
  }
}

async function onDelete(row) {
  await ElMessageBox.confirm('确认删除这条健康记录吗？', '提示', { type: 'warning' })
  await request.delete(`/health/${row.id}`)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
