<script setup>
import { ref, watch } from 'vue'
import { ElIcon } from 'element-plus'
import { message } from 'ant-design-vue';
import axios from 'axios'
import { nextTick } from 'vue';

const emit = defineEmits(['send-message', 'change-sessionID'])

const chatModel = ref('通义千问')
const props = defineProps(['chatID', 'ifChat', 'sessionInfo', 'isLogin'])
const localIfChat = ref(props.ifChat)
const chatID = ref(props.chatID)
const inputValue = ref('')
const result = ref('')
const combinedArray = ref([])
const scrollbar = ref(null)
watch(() => props.ifChat, (newVal, oldVal) => {
    localIfChat.value = newVal
    // 如果是变成false，则清空聊天记录
    if (!newVal) {
        combinedArray.value = []
    }
})

watch(() => props.chatID, (newVal, oldVal) => {
    // 如果新旧值相等，则不做任何操作，===比较的是引用地址，==
    if (newVal == oldVal||newVal===oldVal) {
        return
    }

    // 如果chatID的值和newVal一致
    if(chatID.value == newVal){
        return
    }


    chatID.value = newVal


    // 输出新旧值
    console.log('newVal:', newVal)
    console.log('oldVal:', oldVal)
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
                // 将messageList中的数据按照user分割
                let splitArray = messageList.split('user: ')
                // 遍历splitArray，将每一项按照assistant分割
                splitArray.forEach((item, index) => {
                    if (index === 0) {
                        return
                    }
                    let tempArray = item.split('assistant: ')
                    // 将每一项的assistant部分插入到combinedArray中
                    combinedArray.value.push({
                        type: 'human',
                        content: tempArray[0]
                    })
                    combinedArray.value.push({
                        type: 'ai',
                        content: tempArray[1]
                    })
                })


                //  // 判断localStrorage中是否有chatID的值，如果没有，则以当前chatID为键,res.data.data为值存入localStorage
                // if (!localStorage.getItem(chatID.value)) {
                //     localStorage.setItem(chatID.value, res.data.data)
                // }else{
                //     localStorage.setItem(chatID.value, localStorage.getItem(chatID.value) + res.data.data)
                // }


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

// 要根据选择的模型来发送请求，这里暂时只有通义千问
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
    aliChat()
    clearInput()
}

function aliChat() {
    const sessionID = props.chatID

    // 携带cookie发送请求
    const source = new EventSource("http://127.0.0.1:8080/aliChat?inputValue=" + inputValue.value +
        "&chatID=" + chatID.value +
        "&chatModel=" + "通义千问", {
        withCredentials: true
    })

    let ifClose = false

    source.onmessage = function (event) {
        if (ifClose) {
            chatID.value = event.data
            emit('change-sessionID', event.data)
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

        // // 判断localStrorage中是否有chatID的值，如果没有，则以当前chatID为键,event.data为值存入localStorage
        // if (!localStorage.getItem(chatID.value)) {
        //     localStorage.setItem(chatID.value, event.data)
        // }else{
        //     localStorage.setItem(chatID.value, localStorage.getItem(chatID.value) + event.data)
        // }


    }
    source.onerror = function (event) {
        console.log(event)
        message.error('请求失败')
    }
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
                                    <div style="white-space: pre-line;">{{ item.content }}</div>
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
                                    <div style="white-space: pre-line;">{{ item.content }}</div>
                                    </p>
                                </el-card>
                            </div>
                        </div>
                    </div>
                </el-scrollbar>
            </div>
        </div>
        <!-- 模型选择，通义千问或者文心一言 -->
        <el-select v-model="chatModel" placeholder="请选择模型" style="width: 120px;">
            <el-option label="通义千问" value="通义千问"></el-option>
            <el-option label="文心一言" value="文心一言"></el-option>
        </el-select>

        <div class="chat-area">
            <el-input v-model="inputValue" type="textarea" placeholder="请输入内容" size="large" maxlength="1500"
                resize="none" show-word-limit :rows="4" @clear="clearInput" @keydown.enter.prevent="sendMessage"
                style="width: 65%;height:80px;">
            </el-input>
        </div>
    </div>

</template>

<style scoped>
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