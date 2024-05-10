import { createRouter, createWebHistory } from 'vue-router'
import App from '../App.vue'
import Chat from '../Chat.vue'
import User from '../User.vue'
import Tools from '../Tools.vue'


const routes = [
  {
    path: '/',
    name: 'home',
    component: Chat
  },
  {
    path: '/chat',
    name: 'chat',
    component: Chat
  },
  {
    path: '/user/:id',
    name: 'user',
    component: User
  },
  {
    path: '/tools',
    name: 'tools',
    component: Tools
  }
]



const router = createRouter({
  history: createWebHistory(),
  routes
})


export default router