<template>
    <div class="personal-center">
        <el-container>
            <el-header class="app-header">
                <el-menu mode="horizontal" :default-active="currentView" :style="{
                    backgroundColor: 'linear-gradient(to right, #6f42c1, #9d58a8)',
                }" text-color="black" active-text-color="#ffd04b" @select="handleMenuSelect"
                    :class="{ 'menu-shadow': true }">
                    <el-menu-item index="dialogSettings">对话设置</el-menu-item>
                    <el-menu-item index="profile">个人资料</el-menu-item>
                    <el-menu-item index="contact">联系我们</el-menu-item>
                </el-menu>
            </el-header>
            <el-main class="content-wrap">
                <transition name="slide-fade" mode="out-in">
                    <component :is="currentComponent" :key="currentView" :user="user"
                        @edit-profile-requested="handleEditProfileRequest"
                        @password-modal-requested="handlePasswordModalRequest"
                        @update-user-settings="handleUpdateUserSettings"
                        />
                </transition>
            </el-main>
        </el-container>
    </div>

    <el-dialog v-model="editProfileDialogVisible" title="编辑个人资料" width="30%">
        <el-form ref="editProfileForm" :model="form" label-width="80px">
            <!-- 用户名 -->
            <el-form-item label="用户名">
                <el-input v-model="form.userName"></el-input>
            </el-form-item>
            <!-- 手机号 -->
            <el-form-item label="手机号">
                <el-input v-model="form.mobilePhone"></el-input>
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="editProfileDialogVisible = false">取消</el-button>
                <el-button type="primary" @click="saveProfileChanges">保存</el-button>
            </span>
        </template>
    </el-dialog>

    <el-dialog v-model="passwordModalVisible" title="修改密码" width="30%">
        <el-form :model="passwordForm" label-width="80px">
            <el-form-item label="旧密码">
                <el-input v-model="passwordForm.oldPassword" show-password></el-input>
            </el-form-item>
            <el-form-item label="新密码">
                <el-input v-model="passwordForm.newPassword" show-password></el-input>
            </el-form-item>
            <el-form-item label="确认新密码">
                <el-input v-model="passwordForm.confirmPassword" show-password></el-input>
            </el-form-item>
        </el-form>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="passwordModalVisible = false">取消</el-button>
                <el-button type="primary" @click="updatePassword">保存</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script setup>
import Profile from "./components/personalCenterComponents/Profile.vue";
import DialogSettings from "./components/personalCenterComponents/DialogSettings.vue";
import ContactUs from "./components/personalCenterComponents/ContactUs.vue";
import { ref, computed, onMounted, h } from "vue";
import axios from "axios";
import { ElMessage } from "element-plus";
import CryptoJS from "crypto-js";

const currentView = ref("profile");

const handleMenuSelect = (key) => {
    currentView.value = key;
};

const currentComponent = computed(() => {
    switch (currentView.value) {
        case "dialogSettings":
            return DialogSettings;
        case "contact":
            return ContactUs;
        default:
            return Profile;
    }
});

const user = ref({
    userID: "",
    userName: "",
    mobilePhone: "",
    defaultPrompt: "",
    defaultModel: "",
});

// 表单数据对象
const form = ref({
    userName: "",
    mobilePhone: "",
});

// 密码表单数据对象
const passwordForm = ref({
    oldPassword: "",
    newPassword: "",
    confirmPassword: "",
});

const editProfileDialogVisible = ref(false);

// 处理编辑个人资料请求
const handleEditProfileRequest = () => {
    // 打开编辑模态框
    editProfileDialogVisible.value = true;
    form.value = { ...user.value };
};

// 保存个人资料更改的逻辑
const saveProfileChanges = () => {
    axios.post("http://127.0.0.1:8080/user/updateUserProfile"
        , {
            userID: user.value.userID,
            userName: form.value.userName,
            mobilePhone: form.value.mobilePhone,
        }, {
        withCredentials: true,
    }).then((res) => {
        if (res.data.status === "success") {
            // 更新用户信息

            user.value.userName = form.value.userName;
            user.value.mobilePhone = form.value.mobilePhone;

            ElMessage({
                message: "保存成功",
                type: "success",
            });
        } else {
            ElMessage({
                message: res.data.message,
                type: "error",
            });
        }
    }).catch((err) => {
        console.log(err);
    });

    // 关闭对话框
    editProfileDialogVisible.value = false;
};

// 修改密码模态框显示状态
const passwordModalVisible = ref(false);

// 处理修改密码请求
const updatePassword = () => {
    if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
        ElMessage.error("新密码与确认密码不一致，请重新输入！");
        return;
    }

    // 对新旧密码进行md5加密
    let hashedPassword = CryptoJS.MD5(passwordForm.value.oldPassword).toString();
    let hashedNewPassword = CryptoJS.MD5(passwordForm.value.newPassword).toString();

    // 发起修改密码的API请求，x-www-form-urlencoded
    axios.put(
        "http://127.0.0.1:8080/user/updatePassword",
        `userID=${user.value.userID}&oldPassword=${hashedPassword}&newPassword=${hashedNewPassword}`,
        {
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
            },
            withCredentials: true,
        }
    )
        .then((res) => {
            if (res.data.status === "success") {
                ElMessage({
                    message: "密码修改成功",
                    type: "success",
                });
                // 清空表单
                passwordForm.value = {
                    oldPassword: "",
                    newPassword: "",
                    confirmPassword: "",
                };
                // 关闭模态框
                passwordModalVisible.value = false;
            } else {
                ElMessage({
                    message: res.data.message,
                    type: "error",
                });
            }
        })
        .catch((err) => {
            console.log(err);
        });
};

// 处理修改密码模态框请求
const handlePasswordModalRequest = () => {
    passwordModalVisible.value = true;
};

onMounted(() => {
    getUserProfile();
});

function getUserProfile() {
    const userID = window.location.pathname.split("/")[2];
    axios
        .get("http://127.0.0.1:8080/user/getUserProfile", {
            withCredentials: true,
            params: {
                userID: userID,
            },
        })
        .then((res) => {
            if (res.data.status === "success") {
                user.value = res.data.data;
            } else {
                console.log(res.data.message);
            }
        })
        .catch((err) => {
            console.log(err);
        });
}

function handleUpdateUserSettings(model, prompt) {
    axios.put("http://127.0.0.1:8080/user/updateUserSettings", {
        userID: user.value.userID,
        defaultModel: model,
        defaultPrompt: prompt,
    }, {
        withCredentials: true,
    }).then(res => {
        if (res.data.status === "success") {
            user.value.defaultModel = model;
            user.value.defaultPrompt = prompt;
            ElMessage({
                message: "保存成功",
                type: "success",
            });
        } else {
            ElMessage({
                message: res.data.message,
                type: "error",
            });
        }
    }).catch(err => {
        console.log(err);
    });
}
</script>

<style scoped>
.personal-center {
    height: 100vh;
    overflow: hidden;
}

.app-header {
    border-radius: 0 0 10px 10px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    padding: 0 20px;
}

.menu-shadow {
    transition: box-shadow 0.3s;
}

.app-header .el-menu-item:hover {
    background-color: rgba(255, 255, 255, 0.1);
}

.content-wrap {
    width: 100%;
    height: 100vh;
    overflow-y: auto;
    scrollbar-width: none;
}

.slide-fade-enter-active,
.slide-fade-leave-active {
    transition: all 0.25s ease;
}

.slide-fade-enter-from,
.slide-fade-leave-to {
    transform: translateY(100px);
    opacity: 0;
}
</style>