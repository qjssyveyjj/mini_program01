<template>
  <div class="login-page">
    <el-card class="login-card">
      <h2 class="title">熊猫哥健康管理系统</h2>
      <p class="subtitle">管理后台登录</p>
      <el-form :model="form" @submit.prevent>
        <el-form-item>
          <el-input v-model="form.username" placeholder="用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.password"
            type="password"
            placeholder="密码"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="onSubmit"
          />
        </el-form-item>
        <el-button type="primary" class="login-btn" @click="onSubmit">登录</el-button>
        <p class="tip">演示账号：admin / admin123</p>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useAuthStore } from '@/store/auth'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const form = reactive({ username: '', password: '' })

function onSubmit() {
  if (auth.login(form.username, form.password)) {
    ElMessage.success('登录成功')
    router.push(route.query.redirect || '/')
  } else {
    ElMessage.error('用户名或密码错误')
  }
}
</script>

<style scoped>
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #42a5f5 0%, #66bb6a 100%);
}
.login-card {
  width: 360px;
  padding: 20px;
}
.title {
  text-align: center;
  margin: 0;
  color: var(--text-color);
}
.subtitle {
  text-align: center;
  color: #888;
  margin: 8px 0 24px;
}
.login-btn {
  width: 100%;
}
.tip {
  text-align: center;
  color: #aaa;
  font-size: 12px;
  margin-top: 12px;
}
</style>
