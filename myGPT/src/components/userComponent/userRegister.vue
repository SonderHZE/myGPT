<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'

const userRegister = ref({
    username: '',
    password: '',
    password2: '',
    email: '',
})

function register(){
    //1. 校验表单完整性
    if(!userRegister.value.username || !userRegister.value.password || !userRegister.value.password2 || !userRegister.value.email){
        ElMessage({
            message: '请填写完整信息',
            type: 'error'
        })
        return
    }

    //2. 校验两次密码是否一致
    if(userRegister.value.password !== userRegister.value.password2){
        ElMessage({
            message: '两次密码不一致',
            type: 'error'
        })
        return
    }

    //3. 发送请求
    axios.post('http://127.0.0.1:8080/user/register',{
        username: userRegister.value.username,
        password: userRegister.value.password,
        email: userRegister.value.email
    }).then(res => {
        if(res.data.status === "success"){
            ElMessage({
                message: '注册成功',
                type: 'success'
            })
            register.value = false
        }else{
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
    <el-form :model="userRegister" label-width="auto" style="max-width: 600px">
        <el-form-item label="用户名" prop="username">
            <el-input v-model="userRegister.username"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
            <el-input v-model="userRegister.password" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="password2">
            <el-input v-model="userRegister.password2" show-password></el-input>
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
            <el-input v-model="userRegister.email"></el-input>
        </el-form-item>
        <el-form-item>
            <el-button type="primary" @click="register">注册</el-button>
        </el-form-item>
    </el-form>
</template>