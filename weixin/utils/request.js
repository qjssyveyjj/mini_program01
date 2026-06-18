// 网络请求封装：基于 wx.request，返回 Promise（生产环境强制 HTTPS）
const config = require('../config')

function getBaseUrl() {
  const app = getApp()
  const baseUrl = (app && app.globalData && app.globalData.baseUrl) || config.baseUrl
  // 微信小程序要求生产接口必须为 HTTPS（本地开发 localhost 除外）
  if (!baseUrl.startsWith('https://') && !baseUrl.startsWith('http://localhost')) {
    console.error('API 地址必须使用 HTTPS:', baseUrl)
    wx.showToast({ title: 'API 配置错误', icon: 'none' })
    return 'https://www.qjssyveyjj.asia/api'
  }
  return baseUrl
}

function request({ url, method = 'GET', data = {}, header = {} }) {
  const baseUrl = getBaseUrl()
  return new Promise((resolve, reject) => {
    wx.request({
      url: baseUrl + url,
      method,
      data,
      header: {
        'content-type': 'application/json',
        ...header
      },
      success: (res) => {
        if (res.statusCode >= 200 && res.statusCode < 300) {
          resolve(res.data)
        } else {
          wx.showToast({ title: '请求失败', icon: 'none' })
          reject(res)
        }
      },
      fail: (err) => {
        wx.showToast({ title: '网络错误', icon: 'none' })
        reject(err)
      }
    })
  })
}

module.exports = { request }
