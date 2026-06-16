const { request } = require('../../utils/request')

Page({
  // 微信一键登录：获取 code 换取用户信息
  wxLogin() {
    wx.showLoading({ title: '登录中...' })
    wx.login({
      success: (res) => {
        if (!res.code) {
          wx.hideLoading()
          wx.showToast({ title: '登录失败', icon: 'none' })
          return
        }
        request({ url: '/auth/login', method: 'POST', data: { code: res.code } })
          .then((user) => {
            const app = getApp()
            app.globalData.userInfo = user
            app.globalData.userId = user.id
            app.globalData.openid = user.openid
            wx.setStorageSync('userId', user.id)
            wx.hideLoading()
            wx.switchTab({ url: '/pages/home/home' })
          })
          .catch(() => {
            wx.hideLoading()
            wx.showToast({ title: '登录失败', icon: 'none' })
          })
      },
      fail: () => {
        wx.hideLoading()
        wx.showToast({ title: '登录失败', icon: 'none' })
      }
    })
  }
})
