<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'
import MainMessageVue from './components/userComponent/MainMessage.vue';
import userRegister from './components/userComponent/userRegister.vue'
import userLogin from './components/userComponent/userLogin.vue'
import { ElMessageBox } from 'element-plus';

const chatID = ref(-1)
const ifChat = ref(false)
const searchInfo = ref('')
const notifyMessage = ref('最多显示最近的100条消息')
const chatList = ref([
]);
const sessionInfo = ref();
const chatListOneDay = ref([]);
const chatListSevenDay = ref([]);
const chatListThirtyDay = ref([]);
const ifLogin = ref(false);
const register = ref(false);
const login = ref(false);

const filterChatListOneDay = computed(() => {
    // 如果搜索框为空，则返回所有聊天记录
    if (!searchInfo.value) {
        return chatListOneDay.value;
    }
    // 如果搜索框不为空，则返回包含搜索关键字的聊天记录
    return chatListOneDay.value.filter(item => item.chatTitle.includes(searchInfo.value));
});

const filterChatListSevenDay = computed(() => {
    if (!searchInfo.value) {
        return chatListSevenDay.value;
    }
    return chatListSevenDay.value.filter(item => item.chatTitle.includes(searchInfo.value));
});

const filterChatListThirtyDay = computed(() => {
    if (!searchInfo.value) {
        return chatListThirtyDay.value;
    }
    return chatListThirtyDay.value.filter(item => item.chatTitle.includes(searchInfo.value));
});

function distributeChars() {
    // 清空之前的聊天记录
    chatListOneDay.value = [];
    chatListSevenDay.value = [];
    chatListThirtyDay.value = [];

    // 获取当前时间
    const now = Date.now();

    // 根据当前时间，将聊天记录分为三个时间段
    const oneDay = 24 * 60 * 60 * 1000;
    const sevenDay = 7 * oneDay;
    const thirtyDay = 30 * oneDay;

    // 从最新的聊天记录开始遍历
    for (let i = 0; i < chatList.value.length; i++) {
        const chat = chatList.value[i];
        const chatTime = new Date(chat.time).getTime();
        const diff = now - chatTime;

        if (diff <= oneDay) {
            chatListOneDay.value.push(chat);
        } else if (diff <= sevenDay) {
            chatListSevenDay.value.push(chat);
        } else if (diff <= thirtyDay) {
            chatListThirtyDay.value.push(chat);
        }
    }
}

function handleLoginSuccess() {
    ifLogin.value = true;
    login.value = false;

    // 向后端请求聊天记录,携带cookie
    gettAllChatList();
}

function resetPage() {
    ifChat.value = false;
    chatID.value = -1;
    console.log(chatID.value);
}

function sendMessage(inputValue, chatID) {
    ifChat.value = true;
    // 向消息列表中添加新消息，标题为inputValue，时间为当前时间
    // 消息列表中没有该对话，则添加该对话
    if (chatList.value.find(item => item.chatID == chatID)) {
        return;
    }

    chatList.value.push({
        chatID: chatID,
        chatTitle: inputValue,
        time: new Date().toISOString()
    });
    distributeChars();
}

function changeSession(newSessionID) {
    chatID.value = newSessionID;
}

function clearChatList() {
    chatList.value = [];
    chatListOneDay.value = [];
    chatListSevenDay.value = [];
    chatListThirtyDay.value = [];
}

function gettAllChatList() {
    if (ifLogin.value === false) {
        return;
    }

    // 向后端请求聊天记录,携带cookie
    axios.get('http://127.0.0.1:8080/user/getAllChatList', {
        withCredentials: true,
    }).then(res => {
        if (res.data.status === 'success') {
            clearChatList();
            chatList.value = res.data.data;
            distributeChars();
        }
    }).catch(err => {
        console.log(err);
    });
}

function readChat(ID) {
    ifChat.value = true;
    //通过chatID找到对应的sessionInfo
    // sessionInfo.value = chatList.value.find(item => item.chatID === ID);
    // 将sessionID传递给子组件
    chatID.value = ID;

}

function renameChat(ID) {
    // 弹出重命名对话框
    ElMessageBox.prompt('请输入新的对话名称', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /\S/,
        inputErrorMessage: '对话名称不能为空'
    }).then(({ value }) => {
        // 向后端发送重命名请求
        axios.post('http://127.0.0.1:8080/user/renameChat', {
            chatID: ID,
            chatTitle: value,
        },
            {
                withCredentials: true,
            }).then(res => {
                if (res.data.status === 'success') {
                    // 重命名成功后，更新chatList
                    gettAllChatList();
                }
            }).catch(err => {
                console.log(err);
            });
    }).catch(() => {
        // 用户取消重命名
    });

}

function deleteChat(ID) {
    // 弹出删除对话框
    ElMessageBox.confirm('此操作将永久删除该对话, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(() => {
        // 向后端发送删除请求
        axios.post('http://127.0.0.1:8080/user/deleteChat', {
            chatID: ID,
        },
            {
                withCredentials: true,
            }).then(res => {
                if (res.data.status === 'success') {
                    // 删除成功后，更新chatList
                    gettAllChatList();
                }else{
                    console.log(res.data.message);
                }
            }).catch(err => {
                console.log(err);
            });
    }).catch(() => {
        // 用户取消删除
    });
}

onMounted(() => {
    //如果没登录，弹出登录框
    if (ifLogin.value === false) {
        return;
    }
    gettAllChatList();
});
</script>

<template>
    <div class="common-layout">
        <el-container>
            <el-header height="50px" style="background-color: rgb(247,248,252);width:100vw;">
                <el-row>
                    <el-col :span="4" style="height:50px;align-items: center;display: flex;">
                        <img src="https://img.alicdn.com/imgextra/i1/O1CN01CC9kic1ig1r4sAY5d_!!6000000004441-2-tps-880-210.png"
                            style="height: 24px;" />
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
                                <el-dropdown-item v-if="ifLogin === true">个人中心</el-dropdown-item>
                                <el-dropdown-item v-if="ifLogin === true">退出登录</el-dropdown-item>
                                <el-dropdown-item v-if="ifLogin === false" @click="login = true">登录</el-dropdown-item>
                                <el-dropdown-item v-if="ifLogin === false"
                                    @click="register = true">注册</el-dropdown-item>
                            </template>
                        </el-dropdown>
                    </el-col>
                </el-row>
            </el-header>

            <el-container>
                <el-aside width="301px" style="padding:20px; background-color: white;">
                    <el-row align>
                        <!-- 新建对话按钮 -->
                        <el-button type="primary" @click="resetPage"
                            style="width:100%; height:40px; background-color: purple; border-radius: 20px;">
                            <el-icon>
                                <CirclePlus />
                            </el-icon>
                            新建对话
                        </el-button>

                        <!-- 搜索区域 -->
                        <div class="search-area">
                            <el-icon style="margin-right:5px">
                                <Search />
                            </el-icon>
                            <input v-model="searchInfo"
                                style="width: 100%; height: 40px; border: none; outline: none; background-color: transparent; font-size: 14px; color: #333;"
                                placeholder="搜索历史记录" />
                        </div>

                        <!-- 提示区域 -->
                        <div class="notice-area">
                            <el-icon style="margin-right:5px">
                                <Notice />
                            </el-icon>
                            <span style="font-size: 14px; color: #333;">{{ notifyMessage }}</span>
                        </div>

                        <!-- 历史聊天列表 -->
                        <el-scrollbar style="width:100%;" height="calc(100vh - 300px)">
                            <!-- 刷新按钮 -->
                            <div class="refresh-area"
                                style="width:100%; height:40px; display: flex; align-items: center; justify-content: center;">
                                <el-button type="plain" @click="gettAllChatList" style="color: #333; font-size: 14px;"
                                    round><el-icon>
                                        <Refresh />
                                    </el-icon>刷新</el-button>
                            </div>

                            <div id="one-day-item">
                                <span class="date-title">今天</span>
                                <div class="chat-item" v-for="item in filterChatListOneDay" :key="item.chatID"
                                    @click="readChat(item.chatID)">
                                    <el-icon>
                                        <ChatLineRound />
                                    </el-icon>
                                    <el-text style="margin-left:6px;margin-right:6px; user-select: none;" truncated>{{
                                        item.chatTitle }}</el-text>
                                    <el-dropdown trigger="click">
                                        <el-button type="text">
                                            <el-icon>
                                                <More />
                                            </el-icon>
                                        </el-button>
                                        <template #dropdown>
                                            <el-dropdown-menu>
                                                <el-dropdown-item @click="deleteChat(item.chatID)">删除</el-dropdown-item>
                                                <el-dropdown-item
                                                    @click="renameChat(item.chatID)">重命名</el-dropdown-item>
                                            </el-dropdown-menu>
                                        </template>
                                    </el-dropdown>
                                </div>
                            </div>

                            <br>

                            <div id="seven-day-item">
                                <span class="date-title">7天内</span>
                                <div class="chat-item" v-for="item in filterChatListSevenDay" :key="item.id"
                                    @click="readChat(item.chatID)">
                                    <el-icon>
                                        <ChatLineRound />
                                    </el-icon>
                                    <el-text style="margin-left:6px;margin-right:6px; user-select: none;" truncated>{{
                                        item.chatTitle }}</el-text>
                                    <el-dropdown trigger="click">
                                        <el-button type="text">
                                            <el-icon>
                                                <More />
                                            </el-icon>
                                        </el-button>
                                        <template #dropdown>
                                            <el-dropdown-menu>
                                                <el-dropdown-menu>
                                                    <el-dropdown-item
                                                        @click="deleteChat(item.chatID)">删除</el-dropdown-item>
                                                    <el-dropdown-item
                                                        @click="renameChat(item.chatID)">重命名</el-dropdown-item>
                                                </el-dropdown-menu>
                                            </el-dropdown-menu>
                                        </template>
                                    </el-dropdown>
                                </div>
                            </div>

                            <br>

                            <div id="thirty-day-item">
                                <span class="date-title">30天内</span>
                                <div class="chat-item" v-for="item in filterChatListThirtyDay" :key="item.id"
                                    @click="readChat(item.chatID)">
                                    <el-icon>
                                        <ChatLineRound />
                                    </el-icon>
                                    <el-text style="margin-left:6px;margin-right:6px; user-select: none;" truncated>{{
                                        item.chatTitle }}</el-text>
                                    <el-dropdown trigger="click">
                                        <el-button type="text">
                                            <el-icon>
                                                <More />
                                            </el-icon>
                                        </el-button>
                                        <template #dropdown>
                                            <el-dropdown-menu>
                                                <el-dropdown-menu>
                                                    <el-dropdown-item
                                                        @click="deleteChat(item.chatID)">删除</el-dropdown-item>
                                                    <el-dropdown-item
                                                        @click="renameChat(item.chatID)">重命名</el-dropdown-item>
                                                </el-dropdown-menu>
                                            </el-dropdown-menu>
                                        </template>
                                    </el-dropdown>
                                </div>
                            </div>
                        </el-scrollbar>
                    </el-row>
                </el-aside>

                <el-main>
                    <MainMessageVue :chatID="chatID" :ifChat="ifChat" :sessionInfo="sessionInfo" :isLogin="ifLogin"
                        @send-message="sendMessage" @change-sessionID="changeSession" />
                </el-main>
            </el-container>
        </el-container>

        <!-- 注册对话框 -->
        <el-dialog title="注册" v-model="register" width="30%" center>
            <userRegister />
        </el-dialog>

        <!-- 登录对话框 -->
        <el-dialog title="登录" v-model="login" width="30%" center>
            <user-login @login-success="handleLoginSuccess" />
        </el-dialog>
    </div>
</template>

<style scoped>
.common-layout {
    width: 100vw;
    height: 100vh;
}

.search-area {
    margin: 20px;
    width: 100%;
    height: 48px;

    display: flex;
    align-items: center;
    justify-content: center;

    border-top: 1px solid #b7b5b5;
    border-bottom: 1px solid #b7b5b5;
}

.notice-area {
    width: 100%;
    height: 42px;
    padding: 9px 18px;

    background-color: rgb(247, 248, 252);
    color: rgb(135, 138, 171);
    border-radius: 21px;
}

.date-title {
    width: 248px;
    height: 42px;
    padding: 16px 0px 8px 18px;

    font-size: 12px;
    color: rgb(135, 138, 171);
}

.chat-item {
    width: 248px;
    height: 38px;
    padding: 8px 18px;

    font-size: 14px;
    color: rgb(38, 36, 76);

    display: flex;
    align-items: center;
    justify-content: space-between;
    border-radius: 8px;
}

.chat-item:hover {
    background-color: rgb(247, 248, 252);
    border-radius: 8px;
}
</style>