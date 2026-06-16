// 网络请求封装：基于 wx.request，返回 Promise
const config = require('../config')

function request({ url, method = 'GET', data = {}, header = {} }) {
  const app = getApp()
  const baseUrl = (app && app.globalData && app.globalData.baseUrl) || config.baseUrl
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
