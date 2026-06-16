<template>
  <el-card>
    <template #header>
      <div class="header">
        <span>用户列表</span>
        <el-button :icon="Refresh" @click="loadUsers">刷新</el-button>
      </div>
    </template>

    <el-table :data="users" v-loading="loading" stripe>
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="nickname" label="昵称" />
      <el-table-column prop="openid" label="OpenID" show-overflow-tooltip />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="createdAt" label="注册时间" :formatter="formatTime" />
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

const users = ref([])
const loading = ref(false)

function formatTime(row, column, value) {
  return value ? new Date(value).toLocaleString('zh-CN') : '-'
}

async function loadUsers() {
  loading.value = true
  try {
    users.value = await request.get('/users')
  } finally {
    loading.value = false
  }
}

async function onDelete(row) {
  await ElMessageBox.confirm(`确认删除用户「${row.nickname}」吗？`, '提示', { type: 'warning' })
  await request.delete(`/users/${row.id}`)
  ElMessage.success('删除成功')
  loadUsers()
}

onMounted(loadUsers)
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
