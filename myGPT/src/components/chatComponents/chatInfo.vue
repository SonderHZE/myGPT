<script setup>
import { ref, watch, nextTick } from 'vue'
import axios from 'axios'
import { defineEmits } from 'vue';
import { marked } from 'marked';
const messageList = ref([])
const props = defineProps(['currentChatID', 'messageList'])
const emit = defineEmits(['system-info'])
const currentChatID = ref('')
const systemInfo = ref('')
const scrollbarRef = ref()
const innerRef = ref()

watch(() => props.currentChatID, (value) => {
    if (value == currentChatID.value) {
        return
    }

    currentChatID.value = value
    if (value != -1) {
        getChatInfo()
    } else {
        systemInfo.value = ''
        messageList.value = []
    }
})

watch(() => props.messageList, (value) => {
    messageList.value = value
    setScrollToBottom()
}, {
    deep: true
})




async function setScrollToBottom() {
    await nextTick()
    scrollbarRef.value.setScrollTop(999999999)
}

async function getChatInfo() {
    axios.get('http://127.0.0.1:8080/getChatInfo?chatID=' + currentChatID.value, {
        withCredentials: true,
    }
    ).then(res => {
        if (res.data.status === 'success') {
            messageList.value = []

            let temp = res.data.data
            // system:你是赵今麦\nuser: 你是谁\nassistant: 我是赵今麦，中国内地青年女演员，出生于2001年，因出演《小别离》中的朵朵一角崭露头角。我参演过多部影视作品，致力于表演艺术。如果你有关于我的作品、演艺经历或者合作项目的问题，我会尽力回答。\n
            // 找到第一个user:的下标
            let userIndex = temp.indexOf('user:')
            // 将下标之间的字符串截取出来
            let system = temp.substring(0, userIndex)
            // 去掉system:和\n
            systemInfo.value = system.substring(7, system.length - 1)



            //1. 按照user分割
            let splitArray = temp.split('user: ')
            //2. 遍历splitArray，将每一项按照assistant分割  
            splitArray.forEach((item, index) => {
                if (index === 0) {
                    return
                }
                let tempArray = item.split('assistant: ')
                //3. 将每一项的assistant部分插入到messageList中
                messageList.value.push({
                    type: 'human',
                    content: tempArray[0]
                })
                messageList.value.push({
                    type: 'ai',
                    content: tempArray[1]
                })
            })
            emit('system-info', systemInfo.value, messageList.value)
        }
    }).catch(err => {
        console.log(err)
    })
}

function markdownToHtml(content) {
    return marked(content)
}


</script>

<template>
    <div>
        <el-scrollbar ref="scrollbarRef"  max-height="440px" always>
            <div ref="innerRef" v-for="(item, index) in props.messageList" :key="index">
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
</template>

<style scoped>
.content {
    display: flex;
    justify-content: flex-start;
    align-items: center;
    margin: 15px 100px;
    padding: auto;
}
</style>