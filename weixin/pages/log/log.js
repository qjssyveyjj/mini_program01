const { request } = require('../../utils/request')

Page({
  data: {
    form: {
      systolic: '',
      diastolic: '',
      heartRate: '',
      bloodOxygen: '',
      weight: '',
      notes: ''
    }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    this.setData({ [`form.${field}`]: e.detail.value })
  },

  submit() {
    const app = getApp()
    const userId = app.globalData.userId || wx.getStorageSync('userId')
    if (!userId) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      return
    }

    const f = this.data.form
    const payload = {
      userId,
      timestamp: this.formatNow(),
      systolic: f.systolic ? Number(f.systolic) : null,
      diastolic: f.diastolic ? Number(f.diastolic) : null,
      heartRate: f.heartRate ? Number(f.heartRate) : null,
      bloodOxygen: f.bloodOxygen ? Number(f.bloodOxygen) : null,
      weight: f.weight ? Number(f.weight) : null,
      notes: f.notes || ''
    }

    request({ url: '/health', method: 'POST', data: payload })
      .then(() => {
        wx.showToast({ title: '保存成功', icon: 'success' })
        setTimeout(() => wx.navigateBack(), 800)
      })
      .catch(() => {})
  },

  // 生成 yyyy-MM-ddTHH:mm:ss 格式时间字符串
  formatNow() {
    const d = new Date()
    const pad = (n) => (n < 10 ? '0' + n : '' + n)
    return (
      d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate()) +
      'T' + pad(d.getHours()) + ':' + pad(d.getMinutes()) + ':' + pad(d.getSeconds())
    )
  }
})
