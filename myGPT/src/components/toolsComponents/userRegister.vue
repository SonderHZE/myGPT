<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import CryptoJS from 'crypto-js';

const userRegister = ref({
    userName: '',
    mobilePhone: '',
    password: '',
    password2: ''
})

function register(){
    //1. 校验表单完整性
    if(!userRegister.value.userName || !userRegister.value.mobilePhone || !userRegister.value.password || !userRegister.value.password2){
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

    //3. 检验手机号格式
    if(!/^1[3456789]\d{9}$/.test(userRegister.value.mobilePhone)){
        ElMessage({
            message: '手机号格式不正确',
            type: 'error'
        })
        return
    }

    let hashedPassword = CryptoJS.MD5(userRegister.value.password).toString()

    //4. 发送请求
    axios.post('http://127.0.0.1:8080/user/register',{
        userName: userRegister.value.userName,
        mobilePhone: userRegister.value.mobilePhone,
        password: hashedPassword
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

    //5. 清空表单
    userRegister.value = {
        userName: '',
        mobilePhone: '',
        password: '',
        password2: ''
    }
}
</script>

<template>  
    <el-form :model="userRegister" label-width="auto" style="max-width: 600px">
        <el-form-item label="用户名" prop="userName">
            <el-input v-model="userRegister.userName"></el-input>
        </el-form-item>
        <el-form-item label="手机号" prop="mobilePhone">
            <el-input v-model="userRegister.mobilePhone"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
            <el-input v-model="userRegister.password" show-password></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="password2">
            <el-input v-model="userRegister.password2" show-password></el-input>
        </el-form-item>
        <el-form-item>
            <el-button type="primary" @click="register">注册</el-button>
        </el-form-item>
    </el-form>
</template>