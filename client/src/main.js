import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import ECharts from 'vue-echarts'
import 'echarts'

import './style.css'
import App from './App.vue'
import router from './router'

const app = createApp(App)

// 注册 Element Plus 全部图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

// 全局注册 ECharts 组件
app.component('v-chart', ECharts)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

app.mount('#app')
