<template>
    <div class="dialog-settings">
      <h2 class="section-title">对话设置</h2>
      
      <div class="settings-card">
        <div class="settings-group">
          <label for="defaultModel" class="label">默认对话模型:</label>
          <el-select 
            v-model="selectedModel" 
            placeholder="请选择默认模型" 
            class="model-selector"
          >
            <el-option
              v-for="model in models"
              :key="model.value"
              :label="model.label"
              :value="model.value"
            ></el-option>
          </el-select>
        </div>
        
        <div class="settings-group">
          <label for="defaultPrompt" class="label">默认提示词:</label>
          <el-input 
            v-model="defaultPrompt" 
            placeholder="请输入默认提示词" 
            class="input-field"
          ></el-input>
        </div>
        
        <el-button 
          type="primary" 
          @click="confirmChanges" 
          class="confirm-button"
        >确定修改</el-button>
      </div>
    </div>
</template>

<script setup>
import { ref, defineProps, defineEmits } from 'vue';

const props = defineProps({
    user: Object,
});
const emit = defineEmits(['update-user-settings']);


const selectedModel = ref(props.user.defaultModel);
const defaultPrompt = ref(props.user.defaultPrompt);
const models = [
  { value: '通义千问', label: '通义千问' },
  { value: '文心一言', label: '文心一言' },
];

const confirmChanges = () => {
  emit('update-user-settings', selectedModel.value, defaultPrompt.value);
};

</script>

<style scoped>
.dialog-settings {
  padding: 20px;
}

.section-title {
  color: #3498db;
  margin-bottom: 15px;
  font-weight: 600;
}

.settings-card {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

.settings-group {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.label {
  font-size: 14px;
  color: #555;
  margin-right: 10px;
  min-width: 120px;
}

.model-selector,
.input-field {
  width: 100%;
  max-width: 300px;
}

.confirm-button {
  width: 100%;
  max-width: 150px;
  margin-top: 20px;
}
</style>