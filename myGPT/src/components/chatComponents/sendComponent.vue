<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
const props = defineProps(['currentChatID', 'ifLogin', 'systemInfo', 'messageList'])  
const emit = defineEmits([ 'add-message', 'append-message', 'change-chat-id'])
const currentChatID = ref(-1)
const ifLogin = ref(false)
const messageList = ref([])
const temperature = ref(0.5)
const top_p = ref(0.9)
const system = ref('你是一个万能大师')
const inputValue = ref('')
const forbidInput = ref(false)
const chatModel = ref('通义千问')

watch(() => props.systemInfo, (value) => {
    system.value = value
})

watch(() => props.currentChatID, (value) => {
    if(value !== -1){
        currentChatID.value = value
    }else{
        currentChatID.value = -1
        system.value = '你是一个万能大师'
    }
})

watch(() => props.messageList, (value) => {
    messageList.value = value
})

watch(() => props.ifLogin, (value) => {
    ifLogin.value = value
})

function clearInput() {
    inputValue.value = ''
}

function sendMessage() {
    if (props.ifLogin === false) {
        ElMessage.error('请先登录')
        return
    }

    if (event.shiftKey && event.keyCode === 13) {
        //插入换行符
        inputValue.value += '\n'
        return
    }
    if (!inputValue.value) {
        return
    }

    emit('add-message', 'human',inputValue.value)

    aiChat()
    clearInput()
}

function aiChat() {
    // 禁止输入框输入
    forbidInput.value = true
    let chatID = props.currentChatID

    // 携带cookie发送请求
    const source = new EventSource("http://127.0.0.1:8080/aiChat?inputValue=" + inputValue.value +
        "&chatID=" + chatID+
        "&chatModel=" + chatModel.value + 
        "&temperature=" + temperature.value +
        "&top_p=" + top_p.value +
        "&system=" + system.value
        , {
        withCredentials: true
    })

    let ifClose = false

    let count = 0;
    let result = ref('')
    source.onmessage = function (event) {
        if (ifClose) {
            chatID = event.data
            emit('change-chat-id', chatID)
            forbidInput.value = false
            source.close()
            return
        }
        if (event.data === 'CHAT COMPLETED!') {
            ifClose = true
        }
        // 如果当前id为-1或者还没有向父组件发出信号，说明是新数据，则插入，否则拼接在最后一条AI数据后面
        if (event.data !== 'CHAT COMPLETED!') {
            if (count === 0) {
                emit('add-message', 'ai',event.data)
                count++;
            } else {
                emit("append-message", event.data)
            }
        }
    }
    source.onerror = function (event) {
        console.log(event)
        ElMessage.error('请求失败')
        //终止请求
        source.close()
        forbidInput.value = false
    }
}



</script>

<template>
    <div class="setting-area">
        <el-row :gutter="10">
            <!-- 模型选择，通义千问或者文心一言 -->
            <el-col :span="4">
                <div class="param-item">
                    <el-select v-model="chatModel" placeholder="请选择模型" size="small">
                        <el-option label="通义千问" value="通义千问"></el-option>
                        <el-option label="文心一言" value="文心一言"></el-option>
                    </el-select>
                </div>
            </el-col>

            <el-col :span="4">
                <div class="param-item">
                    <el-input-number v-model="temperature" controls-position="right" :min="0.1" :max="1" :step="0.1"
                        placeholder="temperature" size="small" title="影响输出文本的多样性，取值越大，生成文本的多样性越强">
                    </el-input-number>
                </div>
            </el-col>

            <!-- top_p -->
            <el-col :span="4">
                <div class="param-item">
                    <el-input-number v-model="top_p" controls-position="right" :min="0.1" :max="1" :step="0.1"
                        placeholder="top_p" size="small" title="影响生成文本的多样性，取值越大，生成文本的多样性越强">
                    </el-input-number>
                </div>
            </el-col>

            <!-- system设置 -->
            <el-col :span="6">
                <div class="param-item">
                    <el-input v-model="system" placeholder="系统设置" size="small" title="系统设置" :disabled="currentChatID !== -1"></el-input>
                </div>
            </el-col>
        </el-row>
    </div>


    <div class="chat-area">
        <el-row>
            <el-col :span="24">
                <div class="no-border-input">
                    <el-input v-model="inputValue" type="textarea" placeholder="请输入内容" size="large" maxlength="1500"
                        :disabled="forbidInput" resize="none" :rows="4" @clear="clearInput"
                        @keydown.enter.prevent="sendMessage">
                    </el-input>
                    <div class="input-button" style="padding-top: 60px;">
                        <el-button type="primary" @click="sendMessage" style="width:100%;">发送</el-button>
                    </div>
                </div>
            </el-col>
        </el-row>


    </div>
</template>

<style scoped>
.chat-area {
    width: 100%;
    height: 300px;
    background-color: #f7f8fc;
}

.setting-area {
    margin-top: 20px;
    width: 100%;

    background-color: #f7f8fc;
}

.no-border-input {
    position: relative;
}

.input-button {
    position: absolute;

    right: 15px;
    bottom: 15px;
}
</style>