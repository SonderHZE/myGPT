<script setup>
import { ref,watch, onMounted} from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import axios from 'axios'
import UserLogin from './userLogin.vue'
import userRegister from './userRegister.vue';
const router = useRouter()
const ifLogin = ref(false)
const login = ref(false)
const register = ref(false)
const props = defineProps({
    ifLogin: Boolean
})

onMounted(() => {
    // 如果cookie中有登录信息，直接登录
    axios.get("http://127.0.0.1:8080/user/login", {
        withCredentials: true
    }).then(res => {
        if (res.data.status === 'success') {
            handleLoginSuccess(res.data.data.userID, res.data.data.userName, res.data.data.mobilePhone)
        }
    }).catch(err => {
        console.log(err)
    })
})

// 监视从父组件传来的登录状态
watch(() => props.ifLogin, (value) => {
    ifLogin.value = value
})

const emit = defineEmits(['login-success','user-logout'])

const user = ref({
    userID: '',
    userName: '',
    mobilePhone: ''
})

function handleLoginSuccess(userID, userName, mobilePhone) {
    ifLogin.value = true
    login.value = false
    register.value = false

    user.value.userID = userID
    user.value.userName = userName
    user.value.mobilePhone = mobilePhone

    ElMessage({
        message: '登录成功',
        type: 'success'
    })

    emit('login-success', userID, userName, mobilePhone)
}

function logout() {
    // 携带cookie向后端发送请求
    axios.post("http://127.0.0.1:8080/user/logout", {}, {
        withCredentials: true
    }).then(res => {
        if (res.data.status === 'success') {
            handleLogoutSuccess()
        } else {
            ElMessage({
                message: res.data.message,
                type: 'error'
            })
        }
    }).catch(err => {
        console.log(err)
    })

    emit('user-logout')
    ifLogin.value = false
    ElMessage({
        message: '退出登录成功',
        type: 'success'
    })
}

function changeToUserInfo() {
    // 跳转到个人信息页面
    router.push('/user')
}

</script>

<template>
    <el-row>
        <el-col :span="4" style="height:50px;align-items: center;display: flex;">
            <img src="https://img.alicdn.com/imgextra/i1/O1CN01CC9kic1ig1r4sAY5d_!!6000000004441-2-tps-880-210.png"
                style="height: 24px;padding-left: 50px" />
        </el-col>
        <el-col :span="2" :offset="16" style="height:50px;align-items: center;display: flex;">
            <el-icon>
                <Position />
            </el-icon>
            百宝袋
        </el-col>
        <el-col :span="2" style="height:50px;align-items: center;display: flex;">
            <el-dropdown>
                <span class="el-dropdown-link"
                    style="cursor: pointer; color: #333; font-size: 14px; display: flex; align-items: center; justify-content: center; height: 50px; width: 100%;">
                    <el-icon size="18px">
                        <User />
                    </el-icon>
                </span>
                <template #dropdown>
                    <el-dropdown-item v-if="ifLogin === true" @click="changeToUserInfo">个人中心</el-dropdown-item>
                    <el-dropdown-item v-if="ifLogin === true" @click="logout">退出登录</el-dropdown-item>
                    <el-dropdown-item v-if="ifLogin === false" @click="login = true">登录</el-dropdown-item>
                    <el-dropdown-item v-if="ifLogin === false" @click="register = true">注册</el-dropdown-item>
                </template>
            </el-dropdown>
        </el-col>
    </el-row>

    <!-- 注册对话框 -->
    <el-dialog title="注册" v-model="register" width="30%" center>
        <userRegister />
    </el-dialog>

    <!-- 登录对话框 -->
    <el-dialog title="登录" v-model="login" width="30%" center>
        <user-login @login-success="handleLoginSuccess" />
    </el-dialog>
</template>