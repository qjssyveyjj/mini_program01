// AI 智能客服接口封装
const { request } = require('./request')

/**
 * 向 AI 客服提问（同步返回完整回答）
 * @param {string} message 用户问题
 * @returns {Promise<string>} 回答文本
 */
function askAi(message) {
  return request({
    url: '/ai/chat?message=' + encodeURIComponent(message),
    method: 'GET'
  }).then((res) => (res && res.answer) || '抱歉，暂时无法回答。')
}

module.exports = { askAi }
