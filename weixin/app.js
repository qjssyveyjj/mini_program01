// app.js
const { request } = require('./utils/request')
const config = require('./config')

App({
  globalData: {
    // 后端接口基础地址，从环境配置模块 config.js 读取
    baseUrl: config.baseUrl,
    userInfo: null,
    userId: null,
    openid: null
  },

  onLaunch() {
    this.wxLogin()
  },

  /**
   * 微信登录：wx.login 获取 code，发送到后端换取用户信息
   */
  wxLogin() {
    wx.login({
      success: (res) => {
        if (!res.code) {
          return
        }
        request({
          url: '/auth/login',
          method: 'POST',
          data: { code: res.code }
        })
          .then((user) => {
            this.globalData.userInfo = user
            this.globalData.userId = user.id
            this.globalData.openid = user.openid
            wx.setStorageSync('userId', user.id)
          })
          .catch(() => {
            // 登录失败时静默处理，页面可引导用户重试
          })
      }
    })
  }
})
