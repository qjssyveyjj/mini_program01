Page({
  data: {
    userInfo: {}
  },

  onShow() {
    const app = getApp()
    this.setData({ userInfo: app.globalData.userInfo || {} })
  },

  goLog() {
    wx.navigateTo({ url: '/pages/log/log' })
  },

  goChat() {
    wx.switchTab({ url: '/pages/chat/chat' })
  },

  relogin() {
    const app = getApp()
    app.wxLogin()
    wx.showToast({ title: '已重新登录', icon: 'none' })
    setTimeout(() => {
      this.setData({ userInfo: app.globalData.userInfo || {} })
    }, 1000)
  }
})
