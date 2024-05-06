<template>
    <div class="personal-center">
      <el-container>
        <el-header>
          <el-menu
            mode="horizontal"
            :default-active="currentView"
            background-color="#545c64"
            text-color="#fff"
            active-text-color="#ffd04b"
            @select="handleMenuSelect"
          >
            <el-menu-item index="dialogSettings">对话设置</el-menu-item>
            <el-menu-item index="profile">个人资料</el-menu-item>
            <el-menu-item index="contact">联系我们</el-menu-item>
          </el-menu>
        </el-header>
        <el-main class="content-wrap">
          <transition name="slide-fade">
            <component :is="currentComponent" :key="currentView" />
          </transition>
        </el-main>
      </el-container>
    </div>
  </template>
  
  <script setup>
  import Profile from './components/personalCenterComponents/Profile.vue';
  import DialogSettings from './components/personalCenterComponents/DialogSettings.vue';
  import ContactUs from './components/personalCenterComponents/ContactUs.vue';
  import { ref, computed } from 'vue';
  
  const currentView = ref('profile');
  
  const handleMenuSelect = (key) => {
    currentView.value = key;
  };
  
  const currentComponent = computed(() => {
    switch (currentView.value) {
      case 'dialogSettings':
        return DialogSettings;
      case 'contact':
        return ContactUs;
      default:
        return Profile;
    }
  });
  </script>
  
  <style scoped>
  .personal-center {
    height: 100vh;
    overflow: hidden; 
  }
  
  .content-wrap {
    position: relative; 
    width: 100%;
    height: calc(100% - 60px); 
    overflow-y: auto; 
  }
  
  .slide-fade-enter-active,
  .slide-fade-leave-active {
    transition: all 0.5s ease;
  }
  
  .slide-fade-enter-from,
  .slide-fade-leave-to {
    transform: translateY(30px); 
    opacity: 0;
  }
  </style>