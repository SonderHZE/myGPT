<script setup>
import { ref, watch, computed } from 'vue'
import { ElMessageBox } from 'element-plus'
import axios from 'axios'
const props = defineProps(['ifLogin', 'currentChatID'])
const emit = defineEmits(['read-chat'])
watch(() => props.currentChatID, (value) => {
    //等待1秒后再获取聊天记录
    setTimeout(() => {
        if (value !== -1) {
            gettAllChatList();
        }
    }, 1000);

})

const chatList = ref([])
const todayChatList = ref([])
const weekChatList = ref([])
const monthChatList = ref([])
const searchInfo = ref('')
const filterChatListOneDay = computed(() => {
    return todayChatList.value.filter(item => item.chatTitle.includes(searchInfo.value))
})
const filterChatListSevenDay = computed(() => {
    return weekChatList.value.filter(item => item.chatTitle.includes(searchInfo.value))
})
const filterChatListThirtyDay = computed(() => {
    return monthChatList.value.filter(item => item.chatTitle.includes(searchInfo.value))
})

const ifLogin = ref(false)
watch(() => props.ifLogin, (value) => {
    if (value == false) {
        clearChatList();
    }

    if (ifLogin.value === value) {
        return;
    }

    ifLogin.value = value
    if (value) {
        gettAllChatList();
    }
})

function gettAllChatList() {
    if (ifLogin.value === false) {
        return;
    }

    // 向后端请求聊天记录,携带cookie
    axios.get('http://127.0.0.1:8080/user/getAllChatList', {
        withCredentials: true,
    }).then(res => {
        if (res.data.status === 'success') {
            chatList.value = res.data.data;
            distributechatList();
        }
    }).catch(err => {
        console.log(err);
    });
}

function clearChatList() {
    todayChatList.value = [];
    weekChatList.value = [];
    monthChatList.value = [];
}

function distributechatList() {
    // 清空之前的聊天记录
    clearChatList();

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
            todayChatList.value.push(chat);
        } else if (diff <= sevenDay) {
            weekChatList.value.push(chat);
        } else if (diff <= thirtyDay) {
            monthChatList.value.push(chat);
        }
    }
}

function readChat(ID) {
    // 向父组件发送读取聊天记录的事件
    emit('read-chat', ID);
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
                } else {
                    console.log(res.data.message);
                }
            }).catch(err => {
                console.log(err);
            });
    }).catch(() => {
        // 用户取消删除
    });
}

</script>

<template>
    <!-- 搜索区域 -->
    <div class="search-area">
        <el-icon style="margin-right:5px">
            <Search />
        </el-icon>
        <input v-model="searchInfo"
            style="width: 100%; height: 40px; border: none; outline: none; background-color: transparent; font-size: 14px; color: #333;"
            placeholder="搜索历史记录" />
    </div>

    <!-- 历史聊天列表 -->
    <el-scrollbar style="width:100%;" height="calc(100vh - 300px)">
        <!-- 刷新按钮 -->
        <div class="refresh-area"
            style="width:100%; height:40px; display: flex; align-items: center; justify-content: center;">
            <el-button type="primary" @click="gettAllChatList" style="background-color:#800080; font-size: 14px;" round><el-icon>
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
                            <el-dropdown-item @click="renameChat(item.chatID)">重命名</el-dropdown-item>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </div>

        <br>

        <div id="seven-day-item">
            <span class="date-title">7天内</span>
            <div class="chat-item" v-for="item in filterChatListSevenDay" :key="item.id" @click="readChat(item.chatID)">
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
                                <el-dropdown-item @click="deleteChat(item.chatID)">删除</el-dropdown-item>
                                <el-dropdown-item @click="renameChat(item.chatID)">重命名</el-dropdown-item>
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
                                <el-dropdown-item @click="deleteChat(item.chatID)">删除</el-dropdown-item>
                                <el-dropdown-item @click="renameChat(item.chatID)">重命名</el-dropdown-item>
                            </el-dropdown-menu>
                        </el-dropdown-menu>
                    </template>
                </el-dropdown>
            </div>
        </div>
    </el-scrollbar>
</template>

<style scoped>
.search-area {
    margin-top: 20px;
    width: 100%;
    height: 48px;

    display: flex;
    align-items: center;
    justify-content: center;

    border-top: 1px solid #b7b5b5;
    border-bottom: 1px solid #b7b5b5;
}

.date-title {
    height: 42px;
    padding: 16px 0px 8px 18px;

    font-size: 12px;
    color: rgb(135, 138, 171);
}

.chat-item {
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