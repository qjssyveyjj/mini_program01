const { request } = require('../../utils/request')

Page({
  data: {
    nickname: '微信用户',
    latest: null
  },

  onShow() {
    const app = getApp()
    if (app.globalData.userInfo) {
      this.setData({ nickname: app.globalData.userInfo.nickname || '微信用户' })
    }
    this.loadLatest()
  },

  // 获取最新一条健康数据
  loadLatest() {
    const app = getApp()
    const userId = app.globalData.userId || wx.getStorageSync('userId')
    if (!userId) {
      return
    }
    request({ url: '/health/latest?userId=' + userId })
      .then((res) => {
        if (res) {
          this.setData({ latest: res })
        }
      })
      .catch(() => {})
  },

  goLog() {
    wx.navigateTo({ url: '/pages/log/log' })
  }
})
