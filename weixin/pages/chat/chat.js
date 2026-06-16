const { askAi } = require('../../utils/ai')

Page({
  data: {
    messages: [
      { from: 'ai', content: '你好，我是熊猫哥健康助手，有什么健康问题都可以问我～' }
    ],
    inputValue: '',
    loading: false,
    scrollTo: ''
  },

  onInput(e) {
    this.setData({ inputValue: e.detail.value })
  },

  sendMessage(e) {
    const question = (e.detail.value || this.data.inputValue || '').trim()
    if (!question) {
      return
    }

    const messages = this.data.messages.concat({ from: 'user', content: question })
    this.setData({
      messages,
      inputValue: '',
      loading: true
    })
    this.scrollToBottom()

    askAi(question)
      .then((answer) => {
        this.setData({
          messages: this.data.messages.concat({ from: 'ai', content: answer }),
          loading: false
        })
        this.scrollToBottom()
      })
      .catch(() => {
        this.setData({
          messages: this.data.messages.concat({ from: 'ai', content: '抱歉，服务暂时不可用。' }),
          loading: false
        })
      })
  },

  scrollToBottom() {
    const idx = this.data.messages.length - 1
    this.setData({ scrollTo: 'msg' + idx })
  }
})
