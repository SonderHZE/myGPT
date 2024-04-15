<script setup>
import { ref, watch, nextTick } from 'vue'
import { ElIcon } from 'element-plus'
import { message } from 'ant-design-vue';
import axios from 'axios'
import { marked } from '/node_modules/.vite/deps/marked.js?v=fc896a47';

const emit = defineEmits(['send-message', 'change-sessionID'])

const chatModel = ref('通义千问')
const props = defineProps(['chatID', 'ifChat', 'sessionInfo', 'isLogin'])
const localIfChat = ref(props.ifChat)
const chatID = ref(props.chatID)
const inputValue = ref('')
const result = ref('')
const combinedArray = ref([])
const scrollbar = ref(null)
const forbidInput = ref(false)
const temperature = ref(0.5)
const top_p = ref(0.5)
const system = ref('你是一个学识渊博的大师')
watch(() => props.ifChat, (newVal, oldVal) => {
    localIfChat.value = newVal
    // 如果是变成false，则清空聊天记录
    if (!newVal) {
        combinedArray.value = []
    }
})

watch(() => props.chatID, (newVal, oldVal) => {
    // 如果新旧值相等，则不做任何操作，===比较的是引用地址，==
    if (newVal == oldVal || newVal === oldVal) {
        return
    }

    // 如果chatID的值和newVal一致
    if (chatID.value == newVal) {
        return
    }


    chatID.value = newVal

    // 发起请求，获取sessionInfo
    if (newVal > 0) {
        axios.get('http://127.0.0.1:8080/getChatInfo?chatID=' + newVal, {
            withCredentials: true,
        }
        ).then(res => {
            if (res.data.status === 'success') {
                // 清空combinedArray
                combinedArray.value = []

                let messageList = res.data.data
                // system:你是赵今麦\nuser: 你是谁\nassistant: 我是赵今麦，中国内地青年女演员，出生于2001年，因出演《小别离》中的朵朵一角崭露头角。我参演过多部影视作品，致力于表演艺术。如果你有关于我的作品、演艺经历或者合作项目的问题，我会尽力回答。\n
                // 找到第一个user:的下标
                let userIndex = messageList.indexOf('user:')
                // 将下标之间的字符串截取出来
                let systemInfo = messageList.substring(0, userIndex)
                // 去掉system:和\n
                system.value = systemInfo.substring(7, systemInfo.length - 1)


               
                //1. 先将messageList按照user分割
                let splitArray = messageList.split('user: ')
                //2. 遍历splitArray，将每一项按照assistant分割  
                splitArray.forEach((item, index) => {
                    if (index === 0) {
                        return
                    }
                    let tempArray = item.split('assistant: ')
                    //3. 将每一项的assistant部分插入到combinedArray中
                    combinedArray.value.push({
                        type: 'human',
                        content: tempArray[0]
                    })
                    combinedArray.value.push({
                        type: 'ai',
                        content: tempArray[1]
                    })
                })

            } else {
                message.error('获取聊天记录失败')
            }
        }).catch(err => {
            console.log(err)
        })
    }
})

// 如果sessionInfo不为空，则将sessionInfo中的数据插入到combinedArray中
watch(() => props.sessionInfo, (newVal, oldVal) => {
    if (newVal) {
        combinedArray.value = []

        //1. 先将newVal按照user分割
        let splitArray = newVal.messageList.split('user: ')
        //2. 遍历splitArray，将每一项按照assistant分割
        splitArray.forEach((item, index) => {
            if (index === 0) {
                return
            }
            let tempArray = item.split('assistant: ')
            //3. 将每一项的assistant部分插入到combinedArray中
            combinedArray.value.push({
                type: 'human',
                content: tempArray[0]
            })
            combinedArray.value.push({
                type: 'ai',
                content: tempArray[1]
            })
        })
    }
})

// 占位，待实现
function changeRecommend() {
    console.log('换一批推荐')
}

// 占位，待实现
function RefreshRight() {
    return '换一批'
}

function clearInput() {
    inputValue.value = ''
}

// 要根据选择的模型来发送请求
function sendMessage() {
    if (props.isLogin === false) {
        message.error('请先登录')
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

    combinedArray.value.push({
        type: 'human',
        content: inputValue.value
    })
    // 发送信号给父组件提示已经发送消息
    emit('send-message', inputValue.value, chatID.value)

    aiChat()
    clearInput()
}

function aiChat() {
    // 禁止输入框输入
    forbidInput.value = true
    console.log('forbidInput:', forbidInput.value)

    const sessionID = props.chatID

    // 携带cookie发送请求
    const source = new EventSource("http://127.0.0.1:8080/aiChat?inputValue=" + inputValue.value +
        "&chatID=" + chatID.value +
        "&chatModel=" + chatModel.value + 
        "&temperature=" + temperature.value +
        "&top_p=" + top_p.value +
        "&system=" + system.value
        , {
        withCredentials: true
    })

    let ifClose = false

    source.onmessage = function (event) {
        if (ifClose) {
            chatID.value = event.data
            emit('change-sessionID', event.data)
            forbidInput.value = false
            source.close()
            return
        }
        if (event.data === 'CHAT COMPLETED!') {
            ifClose = true
        }
        // 如果最后一条数据是人类的，则插入，否则拼接在最后一条AI数据后面
        if (event.data !== 'CHAT COMPLETED!') {
            if (combinedArray.value.length === 0 || combinedArray.value[combinedArray.value.length - 1].type === 'human') {
                combinedArray.value.push({
                    type: 'ai',
                    content: event.data
                })
            } else {
                combinedArray.value[combinedArray.value.length - 1].content += event.data
            }
            result.value += event.data
            nextTick(() => {
                if (scrollbar.value && scrollbar.value.wrap) {
                    scrollbar.value.wrap.scrollTop = scrollbar.value.wrap.scrollHeight;
                }
            });
        }
    }
    source.onerror = function (event) {
        console.log(event)
        message.error('请求失败')
        //终止请求
        source.close()
        forbidInput.value = false
    }
}

function markdownToHtml(content) {
    return marked(content)
}
</script>

<template>
    <div class="main">
        <div v-if="!localIfChat">
            <div class="logo">
                <img
                    src="https://img.alicdn.com/imgextra/i3/O1CN01sffRIx1nb3dXCKdFC_!!6000000005107-2-tps-1024-1024.png">
                <h1>你好我是GPT</h1>
                <p>我能理解人类语言、生成内容，是你生活和工作的智能助手。</p>
                <p>你可以在这里和我聊天，我会尽力帮助你。</p>
            </div>
            <div class="recommend-area">
                <el-row>
                    <el-col :span="2" :offset="20">
                        <el-button type="default" size="large" round style="background-color: white;"><el-icon>
                                <Refresh />
                            </el-icon>换一批</el-button>
                    </el-col>
                </el-row>
            </div>
            <div class="recommend-area">
                <el-row :gutter="20" justify="center">
                    <el-col :span="8">
                        <div class="recommend-item">
                            <p id="recommend-title">文本改写</p>
                            <p id="recommend-content">将文本改写为更加通顺的表达方式</p>
                        </div>
                        <div class="recommend-item">
                            <p id="recommend-title">文本改写</p>
                            <p id="recommend-content">将文本改写为更加通顺的表达方式</p>
                        </div>
                    </el-col>

                    <el-col :span="8">
                        <div class="recommend-item">
                            <p id="recommend-title">文本改写</p>
                            <p id="recommend-content">将文本改写为更加通顺的表达方式</p>
                        </div>
                        <div class="recommend-item">
                            <p id="recommend-title">文本改写</p>
                            <p id="recommend-content">将文本改写为更加通顺的表达方式</p>
                        </div>
                    </el-col>
                </el-row>
            </div>
            <br>

        </div>
        <div v-if="localIfChat">
            <div class="info-area" ref="scrollbar">
                <el-scrollbar style="border-radius: 20px;">
                    <div v-for="(item, index) in combinedArray" :key="index">
                        <!-- 如果是人类 -->
                        <div v-if="item.type === 'human'">
                            <div class="content">
                                <el-icon style="margin-right:20px;border-radius: 20px;border:2px">
                                    <User />
                                </el-icon>
                                <span style="color:rgb(31, 35, 40);font-size:16px;">
                                    {{ item.content }}
                                </span>
                            </div>
                        </div>

                        <!-- 如果是AI -->
                        <div v-else>
                            <div class="content">
                                <el-icon style="margin-right:20px;border-radius: 20px;border:2px">
                                    <ChatDotRound />
                                </el-icon>
                                <el-card style="border-radius: 14px;">
                                    <p style="color:rgb(31, 35, 40);font-size:16px;">
                                        <!-- 将\n替换为<br> -->
                                        <span v-html="markdownToHtml(item.content.replace(/\\n/g, '<br>'))"></span>
                                    </p>
                                </el-card>
                            </div>
                        </div>
                    </div>
                </el-scrollbar>
            </div>
        </div>

        <div class="setting-area" style="margin-left: 166px;display:flex">
            <!-- 模型选择，通义千问或者文心一言 -->
            <el-select v-model="chatModel" placeholder="请选择模型" style="width: 120px">
                <el-option label="通义千问" value="通义千问"></el-option>
                <el-option label="文心一言" value="文心一言"></el-option>
            </el-select>

            <!-- tempeture -->
            <el-input-number v-model="temperature" controls-position="right" :min="0.001" :max="0.999" :step="0.1"
                style="margin-left: 20px;width: 120px" placeholder="tempeture" size="small" title="较高的数值会使输出更加随机，而较低的数值会使其更加集中和确定">
            </el-input-number>

            <!-- top_p -->
            <el-input-number v-model="top_p" controls-position="right" :min="0.001" :max="0.999" :step="0.1"
                style="margin-left: 20px;width: 120px" placeholder="top_p" size="small" title="影响输出文本的多样性，取值越大，生成文本的多样性越强">
            </el-input-number>

            <!-- system设置 -->
            <el-input v-model="system" placeholder="请输入系统设置" style="margin-left: 20px;width: 325px" size="small" 
                                        :disabled="props.chatID > 0"
                title="模型人设，主要用于人设设定，例如，你是xxx公司制作的AI助手" />
        </div>


        <div class="chat-area">
            <el-input v-model="inputValue" type="textarea" placeholder="请输入内容" size="large" maxlength="1500"
                :disabled="forbidInput" resize="none" show-word-limit :rows="4" @clear="clearInput"
                @keydown.enter.prevent="sendMessage" style="width: 65%;height:80px;">
            </el-input>
            <el-button type="primary" size="large" @click="sendMessage" style="margin-top: 52px;">发送</el-button>
        </div>
    </div>


</template>

<style scoped>
.cursor {
    display: inline-block;
    width: 1px;
    height: 1em;
    background-color: black;
    animation: blink 1s infinite;
}

@keyframes blink {
    0% {
        opacity: 1;
    }

    50% {
        opacity: 0;
    }

    100% {
        opacity: 1;
    }
}

.main {
    background-color: rgb(247, 248, 252);
    height: calc(100vh - 100px);

    border-radius: 20px;
}

.logo {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;

    text-align: center;
    padding-top: 25px;
}

.logo img {
    width: 36px;
    height: 36px;
}

.logo h1 {
    font-size: 28px;
    color: #26244c;
    margin-top: 8px;
    font-weight: 700;
}

.logo p {
    margin: 8px;
}

.recommend-area {
    margin-top: 20px;
}

.recommend-item {
    width: 100%;
    height: 100px;
    background-color: white;
    border-radius: 8px;
    margin: 8px;
    padding: 18px;

    display: flex;
    flex-direction: column;
    justify-content: space-around
}

#recommend-title {
    font-size: 16px;
    color: rgb(38, 36, 76)
}

#recommend-content {
    font-size: 14px;
    color: rgb(135, 138, 171)
}

.chat-area {
    display: flex;
    justify-content: center;
}

.info-area {
    width: 100%;
    height: 521.8px;
    padding: 25px;
    overflow-y: auto;
}

.content {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    margin: 15px 100px;
    padding: auto;
}
</style>