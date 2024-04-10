<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import CryptoJS from 'crypto-js';
import { defineEmits } from 'vue'

const emit = defineEmits(['login-success'])

const userLogin = ref({
    userName: '',
    password: ''
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
            ElMessage({
                message: '登录成功',
                type: 'success'
            })

            emit('login-success')

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