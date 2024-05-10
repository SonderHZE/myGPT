<template>
  <div class="profile-view">
    <div class="header">
      <h2 class="section-title">个人资料</h2>
      <el-icon class="edit-icon" @click="showEditProfile">
        <EditPen />
      </el-icon>
    </div>
    <div class="info-card">
      <div class="info-item" v-for="(item, key) in userInfoItems" :key="key">
        <span class="label">{{ item.label }}:</span>
        <span class="value">{{ props.user[item.key] }}</span>
      </div>
      <el-button class="custom-btn" @click="showPasswordModal">修改密码</el-button>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue';
import { EditPen } from '@element-plus/icons-vue';

const props = defineProps({
  user: Object
});
const userInfoItems = [
  { label: '用户ID', key: 'userID' },
  { label: '用户名', key: 'userName' },
  { label: '手机号', key: 'mobilePhone' },
];

const emit = defineEmits(['edit-profile-requested', 'password-modal-requested']);

const showEditProfile = () => {
  emit('edit-profile-requested');
};

const showPasswordModal = () => {
  emit('password-modal-requested');
};
</script>

<style scoped>
.profile-view {
  padding: 30px;
  box-sizing: border-box;
  animation: fadeIn 1s ease-in;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-title {
  color: #3498db;
  font-size: 24px;
}

.edit-icon {
  cursor: pointer;
  color: #606c71;
}

.info-card {
  background: linear-gradient(to right, #f0f2f5, #ffffff);
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.info-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.label {
  font-weight: bold;
  width: 80px;
}

.value {
  color: #333;
  margin-left: 10px;
}

.custom-btn {
  width: 100%;
  max-width: 200px;
  margin-top: 20px;
  background-color: #3498db;
  border-color: #3498db;
  color: white;
  border-radius: 30px;
  transition: all 0.3s;
}

.custom-btn:hover {
  background-color: #2980b9;
  border-color: #2980b9;
}
</style>