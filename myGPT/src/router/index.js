import { createRouter, createWebHistory } from 'vue-router'
import App from '../App.vue'
import Chat from '../Chat.vue'
import User from '../User.vue'


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
    path: '/user',
    name: 'user',
    component: User
  }
]



const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  console.log('to:', to)
  console.log('from:', from)
  next()
})

export default router