import './assets/main.css'

import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import App from './App.vue'
import axios from 'axios'
import router from './router'

import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

const baseURL = '/api'
const axiosInstance = axios.create({
  baseURL,
  timeout: 10000
})

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(ElementPlus)
app.use(router)
app.mount('#app')
