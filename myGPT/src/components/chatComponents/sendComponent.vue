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
                    <el-tooltip placement="top">
                        <template #content>
                            <div>temperature是一个大于0的浮点数，用于控制生成文本的创造性。</div>
                            <div>temperature越大，生成文本的创造性越大，temperature越小，生成文本的创造性越小。</div>
                        </template>
                        <el-input-number v-model="temperature" controls-position="right" :min="0.1" :max="1" :step="0.01"
                            placeholder="temperature" size="small">
                        </el-input-number>
                    </el-tooltip>
                </div>
            </el-col>

            <!-- top_p -->
            <el-col :span="4">
                <div class="param-item">
                    <el-tooltip placement="top">
                        <template #content>
                            <div>top_p是一个0到1之间的浮点数，用于控制生成文本的多样性。</div>
                            <div>top_p越大，生成文本的多样性越大，top_p越小，生成文本的多样性越小。</div>
                        </template>
                        <el-input-number v-model="top_p" controls-position="right" :min="0.1" :max="1" :step="0.01"
                            placeholder="top_p" size="small">
                        </el-input-number>
                    </el-tooltip>
                </div>
            </el-col>

            <!-- system设置 -->
            <el-col :span="6">
                <div class="param-item">
                    <el-tooltip placement="top">
                        <template #content>
                            <div>system是一个字符串，用于控制生成文本的风格。</div>
                            <div>system可以是一个句子，也可以是一个词，用于控制生成文本的风格。</div>
                        </template>
                        <el-input v-model="system" placeholder="system" size="small" :disabled="chatID !== -1">
                        </el-input>
                    </el-tooltip>
                </div>
            </el-col>
        </el-row>
    </div>


    <div class="chat-area">

        <!--  -->
        <!-- <el-row>
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
        </el-row> -->

        <textarea v-model="inputValue" :forbidInput="forbidInput" placeholder="请输入内容" :disabled="forbidInput"
            @clear="clearInput" @keydown.enter.prevent="sendMessage" />

        <div class="input-foot">
            <span class="input-count"> {{ inputValue.length }}/1500 </span>
            <el-button type="primary" class="input-submit" @click="sendMessage" :disabled="forbidInput">
                <el-icon>
                    <ChatDotRound />
                </el-icon>
            </el-button>
        </div>
    </div>
</template>


<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
    currentChatID: Number,
    messageList: Array,
    ifLogin: Boolean,
    systemInfo: String,
    user: Object
})
const emit = defineEmits(['add-message', 'append-message', 'change-chat-id'])

const currentChatID = ref(-1)
const ifLogin = ref(false)
const messageList = ref([])
const temperature = ref(0.85)
const top_p = ref(0.8)
const system = ref("you are a helpful assistant!")
const inputValue = ref('')
const forbidInput = ref(false)
const chatModel = ref('通义千问')

watch(() => props.user, (value) => {
    system.value = props.user.defaultPrompt
    chatModel.value = props.user.defaultModel
})

watch(() => props.systemInfo, (value) => {
    system.value = value
})

watch(() => props.currentChatID, (value) => {
    if (value !== -1) {
        currentChatID.value = value
    } else {
        currentChatID.value = -1
        system.value = props.user.defaultPrompt
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

    emit('add-message', 'human', inputValue.value)

    aiChat()
    clearInput()
}

function aiChat() {
    // 禁止输入框输入
    forbidInput.value = true
    let chatID = props.currentChatID

    // 携带cookie发送请求
    const source = new EventSource("http://127.0.0.1:8080/aiChat?inputValue=" + inputValue.value +
        "&chatID=" + chatID +
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
                emit('add-message', 'ai', event.data)
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

.chat-area {
    position: relative;
    width: 100%;
    height: 300px;
    background-color: #ffffff;

    border-radius: 16px;
    border: 1px solid #c6c4ff;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    padding: 10px 10px 50px 10px;
}

.chat-area:hover {
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    border-top-color: #337ecc;
    border-right-color: #34a853;
    border-bottom-color: #fbbc05;
    border-left-color: #ea4335;

    transition: border-color 0.8s;
}

.chat-area textarea {
    width: 100%;
    height: 100%;
    border: none;
    resize: none;
    outline: none;
    font-size: 16px;
    line-height: 1.5;
    scrollbar-width: none;

    background-color: #ffffff;
}

.input-foot {
    display:flex;
    justify-content:space-between;

    padding: 2px 8px;
}

.input-count {
    color: #606266;
    font-size: 13px;
}

.input-submit {
    width: 50px;
    height: 30px;
    border-radius: 15px;
    background-color: #800080;
    color: #ffffff;
}
</style>