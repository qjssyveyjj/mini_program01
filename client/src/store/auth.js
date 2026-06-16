import { defineStore } from 'pinia'

/**
 * 管理后台登录态。
 * 说明：当前为简化的本地登录（无后端管理员表），仅用于演示路由守卫。
 */
export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('admin_token') || '',
    username: localStorage.getItem('admin_username') || ''
  }),
  getters: {
    isLoggedIn: (state) => !!state.token
  },
  actions: {
    login(username, password) {
      // 演示用：固定管理员账号 admin / admin123
      if (username === 'admin' && password === 'admin123') {
        this.token = 'demo-token-' + Date.now()
        this.username = username
        localStorage.setItem('admin_token', this.token)
        localStorage.setItem('admin_username', username)
        return true
      }
      return false
    },
    logout() {
      this.token = ''
      this.username = ''
      localStorage.removeItem('admin_token')
      localStorage.removeItem('admin_username')
    }
  }
})
