<template>
<el-form :model="userLogin" label-width="auto" style="max-width: 600px">
    <el-form-item label="账号" prop="username">
        <el-input v-model="userLogin.userName" placeholder="请输入用户名或手机号"></el-input>
    </el-form-item>
    <el-form-item label="密码" prop="password">
        <el-input v-model="userLogin.password" type="password" placeholder="请输入密码"></el-input>
    </el-form-item>
    <el-form-item>
        <el-button type="primary" @click="login">登录</el-button>
    </el-form-item>
</el-form>
</template>

<script setup>
import { ref } from 'vue'
import axios from 'axios'
import CryptoJS from 'crypto-js';
import { defineEmits } from 'vue'
import { ElMessage } from 'element-plus'

const emit = defineEmits(['login-success'])

const userLogin = ref({
    userName: '',
    password: ''
})

const user = ref({
    userID: '',
    userName: '',
    mobilePhone: '',
    defaultModel: '',
    defaultPrompt: ''
})

function login() {
    //1. 校验表单完整性
    if (!userLogin.value.userName || !userLogin.value.password) {
        ElMessage({
            message: '请填写完整信息',
            type: 'error'
        })
        return
    }

    let hashedPassword = CryptoJS.MD5(userLogin.value.password).toString()


    //2. 发送请求
    axios.post('http://127.0.0.1:8080/user/login', {
        userName: userLogin.value.userName,
        password: hashedPassword
    },
        {
            withCredentials: true
        }
    ).then(res => {
        if (res.data.status === "success") {
            user.value.userID = res.data.data.userID
            user.value.userName = res.data.data.userName
            user.value.mobilePhone = res.data.data.mobilePhone
            user.value.defaultModel = res.data.data.defaultModel
            user.value.defaultPrompt = res.data.data.defaultPrompt


            userLogin.value.userName = ''
            userLogin.value.password =  ''
            ElMessage({
                message: '登录成功',
                type: 'success'
            })
            emit('login-success', user.value)
        } else {
            ElMessage({
                message: res.data.message,
                type: 'error'
            })
        }
    }).catch(err => {
        console.log(err)
    })
}
</script>