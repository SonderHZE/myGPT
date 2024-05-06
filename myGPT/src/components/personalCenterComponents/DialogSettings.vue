<template>
    <div class="dialog-settings">
      <h2 class="section-title">对话设置</h2>
      <div class="settings-group">
        <label for="defaultModel">默认对话模型:</label>
        <el-select v-model="selectedModel" placeholder="请选择默认模型">
          <el-option
            v-for="model in models"
            :key="model.value"
            :label="model.label"
            :value="model.value"
          ></el-option>
        </el-select>
      </div>
      <div class="settings-group">
        <label for="defaultPrompt">默认提示词:</label>
        <el-input v-model="defaultPrompt" placeholder="请输入默认提示词"></el-input>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  
  const selectedModel = ref('');
  const defaultPrompt = ref('');
  const models = [
    { value: 'casual', label: '休闲聊天模式' },
    { value: 'professional', label: '专业咨询模式' },
    { value: 'technical', label: '技术讨论模式' },
  ];
  </script>
  
  <style scoped>
  .dialog-settings {
    padding: 20px;
  }
  
  .section-title {
    color: #3498db;
    margin-bottom: 15px;
  }
  
  .settings-group {
    margin-bottom: 20px;
  }
  </style>