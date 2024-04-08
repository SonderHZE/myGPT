<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import { defineEmits } from 'vue'

const emit = defineEmits(['login-success'])

const userLogin = ref({
    username: '',
    password: ''
})

function login() {
    //1. 校验表单完整性
    if (!userLogin.value.username || !userLogin.value.password) {
        ElMessage({
            message: '请填写完整信息',
            type: 'error'
        })
        return
    }


    //2. 发送请求
    axios.post('http://127.0.0.1:8080/user/login', {
        username: userLogin.value.username,
        password: userLogin.value.password
    }
        , {
            withCredentials: true
        }
    ).then(res => {
        if (res.data.status === "success") {
            ElMessage({
                message: '登录成功',
                type: 'success'
            })
            // 将相应中的userID和token存入localStorage，并设置过期时间
            localStorage.setItem('userID', res.data.data.userID)

            //ifLogin.value = true
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
        <el-form-item label="用户名" prop="username">
            <el-input v-model="userLogin.username"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
            <el-input v-model="userLogin.password" type="password"></el-input>
        </el-form-item>
        <el-form-item>
            <el-button type="primary" @click="login">登录</el-button>
        </el-form-item>
    </el-form>
</template>