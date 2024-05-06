<script setup>
import { ref } from 'vue'
import chatInfo from './components/chatComponents/chatInfo.vue'
import chatHead from './components/chatComponents/chatHead.vue'
import chatList from './components/chatComponents/chatList.vue';
import sendComponent from './components/chatComponents/sendComponent.vue';
const ifLogin = ref(false)
const currentChatID = ref(-1)
const systemInfo = ref('')
const messageList = ref([])



// 搜索框内容
const searchInfo = ref([])
function handleLoginSuccess() {
    ifLogin.value = true
}

function handleReadChat(chatID) {
    currentChatID.value = chatID
}

function handleSystemInfo(info, messages) {
    systemInfo.value = info
    messageList.value = messages
}

function resetPage() {
    currentChatID.value = -1
    messageList.value = []
}

function handleAddMessage(type, content) {
    if(type === 'human'){
        messageList.value.push({
            type: 'human',
            content: content
        })
    }else{
        messageList.value.push({
            type: 'ai',
            content: content
        })
    }
}

function handleAppendMessage(content) {
    messageList.value[messageList.value.length-1].content += content
}

function handleChangeChatID(chatID) {
    currentChatID.value = chatID
}

function handleClearMessages() {
    messageList.value = []
}

function logout() {
    ifLogin.value = false

    // 清空聊天框
    currentChatID.value = -1
    messageList.value = []
}
</script>

<template>
    <!-- 顶栏 -->
    <el-container>
        <el-header style="padding:0">
            <div style="background-color: #f7f8fc;text-align: center; ">
                <chatHead @login-success="handleLoginSuccess" @user-logout="logout"
                        :ifLogin="ifLogin"/>
            </div>
        </el-header>

        <!-- 主体 -->
        <el-container style="padding-left:20px;padding-right:20px;margin-top:0">
            <!-- 侧边栏 -->
            <el-aside width="250px" style="padding:20px; background-color: #ffffff;">
                <el-row>
                    <!-- 新建对话按钮 -->
                    <el-button type="primary" @click="resetPage"
                        style="width:100%; height:40px; background-color: purple; border-radius: 20px;">
                        <el-icon>
                            <CirclePlus />
                        </el-icon>
                        新建对话
                    </el-button>
                </el-row>

                <!-- 聊天列表 -->
                <div class="chat-list">
                    <chatList :ifLogin="ifLogin" 
                    :currentChatID="currentChatID"
                     @read-chat="handleReadChat" />
                </div>

            </el-aside>

            <!-- 中心 -->
            <el-main style="background-color: #f7f8fc;height:90vh;display:flex;flex-direction:column">
                <!-- 消息框 -->
                <div class="info-area">
                    <chatInfo :currentChatID="currentChatID"  :messageList="messageList"
                    @system-info="handleSystemInfo" @clear-messages="handleClearMessages" />
                </div>

                <!-- 发送范围 -->
                <div class="input-area">  
                    <sendComponent   
                        :currentChatID="currentChatID"   
                        :systemInfo="systemInfo"    
                        :messageList="messageList"   
                        :ifLogin="ifLogin"  
                        @add-message="handleAddMessage"   
                        @append-message="handleAppendMessage"   
                        @change-chat-id="handleChangeChatID"   
                    />  
                </div>
            </el-main>
        </el-container>
    </el-container>
</template>

<style scoped>
.info-area {
    width: 100%;
    height: 72%;
    padding: 8px 6px;
    overflow-y: auto;
}

.input-area {
    margin-left: 160px;
    margin-right: 160px;
    padding-top: 30px;
    height:28%;

    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}
</style>

