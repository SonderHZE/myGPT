<template>
    <div class="main-page">
        <div>
            <el-container>
                <el-aside class="aside-container">
                    <div class="choice" id="chat"
                        style="display: flex; flex-direction: column; align-items: center;margin-top:50px"
                        @click="$router.push('/chat')">
                        <el-icon size="30" class="aside-icon">
                            <ChatDotSquare />
                        </el-icon>
                        对话
                    </div>
                    <div class="choice" id="tools"
                        style="display: flex; flex-direction: column; align-items: center; background-color: #337ecc; color: #ffffff; margin-top: 30px">
                        <el-icon size="30" class="aside-icon">
                            <Present />
                        </el-icon>
                        百宝袋
                    </div>

                    <div style="position: absolute; bottom: 30px;cursor:pointer" @click="isShow = !isShow">
                        <el-icon size="30" class="aside-icon">
                            <User />
                        </el-icon>
                    </div>

                    <div class="info-choice" :class="{ visable: isShow }">
                        <div v-if="!ifLogin" @click="tryLogin = true">
                            登录
                        </div>
                        <div v-if="!ifLogin" @click="tryRegister = true">
                            注册
                        </div>
                        <div v-if="ifLogin" @click="$router.push('/user/' + user.userID)">
                            个人中心
                        </div>
                        <div v-if="ifLogin" @click="logout">
                            退出
                        </div>
                    </div>
                </el-aside>
                <el-container class="main-container" v-if="chooseID === -1">
                    <el-header class="header-container">
                        <!-- 全部、趣味生活、创意文案、办公助理、学习助手 -->
                        <div class="choice-tab" @click="isActive = 1">
                            <div class="pre-icon" style="
                    width: 12px;
                    height: 12px;
                    background-color: #8e8e8e;
                    border-radius: 50%;
                    margin-right: 5px;
                  "></div>
                            <span :class="{ active: isActive === 1 }"> 全部 </span>
                        </div>
                        <div class="choice-tab" @click="isActive = 2">
                            <div class="pre-icon" style="
                    width: 12px;
                    height: 12px;
                    background-color: #fd8649;
                    border-radius: 50%;
                    margin-right: 5px;
                  "></div>
                            <span :class="{ active: isActive === 2 }"> 趣味生活 </span>
                        </div>
                        <div class="choice-tab" @click="isActive = 3">
                            <div class="pre-icon" style="
                    width: 12px;
                    height: 12px;
                    background-color: #ff8ea8;
                    border-radius: 50%;
                    margin-right: 5px;
                  "></div>
                            <span :class="{ active: isActive === 3 }"> 创意文案 </span>
                        </div>
                        <div class="choice-tab" @click="isActive = 4">
                            <div class="pre-icon" style="
                    width: 12px;
                    height: 12px;
                    background-color: #48cefd;
                    border-radius: 50%;
                    margin-right: 5px;
                  "></div>
                            <span :class="{ active: isActive === 4 }"> 办公助理 </span>
                        </div>
                        <div class="choice-tab" @click="isActive = 5">
                            <div class="pre-icon" style="
                    width: 12px;
                    height: 12px;
                    background-color: #59f4cd;
                    border-radius: 50%;
                    margin-right: 5px;
                  "></div>
                            <span :class="{ active: isActive === 5 }"> 学习助手 </span>
                        </div>
                    </el-header>

                    <el-container>
                        <el-main>
                            <life v-if="isActive === 2 || isActive === 1" @choose="handleChoose" />
                            <creation v-if="isActive === 3 || isActive === 1" @choose="handleChoose" />   
                            <work v-if="isActive === 4 || isActive === 1" @choose="handleChoose" />  
                            <learn v-if="isActive === 5 || isActive === 1" @choose="handleChoose" />               
                        </el-main>
                    </el-container>
                </el-container>

                <el-container class="main-container" v-if="chooseID !== -1">
                    <el-header class="header-container" id="choose-header">
                        <el-button class="choose-header-item" type="text" @click="backToHome" style="width: 40px;height: 40px; 
                            border-radius: 10px;
                            background-color: #ffffff;">
                            <el-icon size="20" class="aside-icon" color="black">
                                <Back />
                            </el-icon>
                        </el-button>

                        <h1 class="choose-header-item" style="color:#26244c;font-size:28px">
                            {{ chooseDetail.title }}
                        </h1>

                        <img class="choose-header-item" :src="chooseDetail.image" style="width: 40px; height: 40px;">
                    </el-header>

                    <el-mian id="choose-main">
                        <div class="choose-summary">
                            {{ chooseDetail.summary }}
                        </div>

                        <div class="choose-tips" style="margin-top:40px">
                            <span style="color:#878aab;font-size:14px;margin-right:10px">试试这样问我:</span>
                            <el-tooltip effect="dark" content="点击复制并快速填入" placement="top">
                                <el-icon @click="copyText(chooseDetail.example)" style="cursor:pointer">
                                    <CopyDocument />
                                </el-icon>
                            </el-tooltip>
                        </div>

                        <div class="choose-content" style="margin-top:10px">
                            {{ chooseDetail.example }}
                        </div>

                        <div style="margin-top:40px;color:#878aab;font-size:14px;">
                            输入问题
                        </div>

                        <div class="input-area">
                            <textarea v-model="inputText" @keydown.enter.prevent="submitQuestion" class="input-field"
                                :placeholder="chooseDetail.placeholder" :disabled="ifRunning"></textarea>
                            <div class="input-foot" style="display:flex;justify-content:space-between">
                                <span class="input-count">{{ inputText.length }}/500</span>
                                <el-button type="primary" class="input-submit" @click="submitQuestion"
                                    :disabled="ifRunning">
                                    <el-icon>
                                        <ChatDotRound />
                                    </el-icon>
                                </el-button>
                            </div>
                        </div>

                        <div class="output-area" v-if="outputText || outputImageUrl || ifRunning">
                            <div style="margin-top:40px;color:#878aab;font-size:14px;">
                                生成结果
                            </div>
                            <div class="output-content" element-loading-text="正在生成">
                                <div v-if="chooseDetail.type === 'text'">
                                    <span v-html="markdownToHtml(outputText)"></span>
                                </div>
                                <div v-if="chooseDetail.type === 'image'"  v-loading="ifRunning">
                                    <img :src="outputImageUrl" v-if="chooseDetail.type === 'image'"
                                        style="width: 100%;height: auto;">
                                </div>
                            </div>
                        </div>

                    </el-mian>
                </el-container>
            </el-container>

            <el-dialog title="登录" v-model="tryLogin" width="30%" center>
                <userLogin @login-success="handleLoginSuccess" />
            </el-dialog>

            <el-dialog title="注册" v-model="tryRegister" width="30%" center>
                <userRegister />
            </el-dialog>
        </div>


    </div>
</template>


<script setup>
import { ref, onMounted, watch } from "vue";
import { ElMessage } from "element-plus";
import axios from "axios";
import { marked } from 'marked';
import life from "./components/toolsComponents/life.vue";
import creation from "./components/toolsComponents/creation.vue";
import work from "./components/toolsComponents/work.vue";
import learn from "./components/toolsComponents/learn.vue";
import userLogin from "./components/toolsComponents/userLogin.vue";
import userRegister from "./components/toolsComponents/userRegister.vue";

const isActive = ref(1);
const isShow = ref(false);
const ifLogin = ref(false);
const tryLogin = ref(false);
const tryRegister = ref(false);
const chooseID = ref(-1);
const chooseDetail = ref({})
const inputText = ref('')
const ifOutput = ref(true)
const outputText = ref('')
const ifRunning = ref(false)

const outputImageUrl = ref('')
const user = ref({
    userID: '',
    userName: '',
    mobilePhone: '',
    defaultModel: '',
    defaultPrompt: ''
})

onMounted(() => {
    axios.get("http://127.0.0.1:8080/user/login", {
        withCredentials: true
    }).then(res => {
        if (res.data.status === 'success') {
            ifLogin.value = true
            user.value = res.data.data
        }
    }).catch(err => {
        console.log(err)
    })
});

function logout() {
    // 携带cookie向后端发送请求
    axios.post("http://127.0.0.1:8080/user/logout", {}, {
        withCredentials: true
    }).then(res => {
        if (res.data.status === 'success') {
            ifLogin.value = false
            ElMessage({
                message: '退出登录成功',
                type: 'success'
            })
        } else {
            ElMessage({
                message: res.data.message,
                type: 'error'
            })
        }
    }).catch(err => {
        console.log(err)
    })

    user.value = {
        userID: '',
        userName: '',
        mobilePhone: '',
        defaultModel: '',
        defaultPrompt: ''
    }
    isShow.value = false

    chooseID.value = -1
    chooseDetail.value = {}
    inputText.value = ''
    outputText.value = ''
    outputImageUrl.value = ''
    ifOutput.value = true
    ifRunning.value = false

    tryLogin.value = false
    tryRegister.value = false
    ifLogin.value = false
}

function handleLoginSuccess(newUser) {
    ifLogin.value = true
    tryLogin.value = false
    isShow.value = false
    user.value = newUser
}

function handleChoose(tool) {
    chooseDetail.value = tool
    chooseID.value = tool.ID
}

function submitQuestion() {
    if (!ifLogin.value) {
        ElMessage({
            message: '请先登录',
            type: 'error'
        })
        return
    }

    if (inputText.value.length === 0) {
        ElMessage({
            message: '请输入问题',
            type: 'error'
        })
        return
    }

    // 清空输出
    outputText.value = ''
    outputImageUrl.value = ''

    let type = chooseDetail.value.type
    if (type === 'text') {
        sendTypeQuestion()
    } else if (type === 'image') {
        sendImageQuestion()
    }
}

function sendImageQuestion() {
    if (ifRunning.value) {
        ElMessage({
            message: '正在生成，请稍后',
            type: 'warning'
        })
    }

    // 禁止输入框输入
    ifRunning.value = true

    axios.post("http://127.0.0.1:8080/imageCreation", {
        prompt: inputText.value,
        size: "1024*1024",
        n: 1
    }, {
        withCredentials: true,
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
        }
    }).then(res => {
        if (res.data.status === 'success') {
            outputImageUrl.value = res.data.data
            ifRunning.value = false
        } else {
            ElMessage({
                message: res.data.message,
                type: 'error'
            })
            ifRunning.value = false
        }
    }).catch(err => {
        console.log(err)
    })
}

function sendTypeQuestion() {
    // 禁止输入框输入
    ifRunning.value = true

    let readyToClose = false

    // 携带cookie发送请求
    const source = new EventSource("http://127.0.0.1:8080/textQuestion?inputValue="
        + inputText.value + "&prompt=" + chooseDetail.value.prompt
        , {
            withCredentials: true
        })

    source.onmessage = function (event) {
        console.log(event.data)
        if(readyToClose) {
            source.close()
            ifRunning.value = false
            return
        }

        if (event.data === 'CHAT COMPLETED!') {
            readyToClose = true
        } else {
            outputText.value += event.data
        }
    }

    source.onerror = function (event) {
        ElMessage({
            message: event,
            type: 'error'
        })
        source.close()
        ifRunning.value = false
    }

}

async function copyText(text) {
    try {
        await navigator.clipboard.writeText(text)
        inputText.value = text
    } catch (err) {
        ElMessage({
            message: '复制失败',
            type: 'error'
        })
    }
}

function markdownToHtml(content) {
    return marked(content)
}

function backToHome() {
    if(ifRunning.value) {
        ElMessage({
            message: '正在生成，请稍后',
            type: 'warning'
        })
        return
    }

    chooseID.value = -1
    chooseDetail.value = {}
    inputText.value = ''
    outputText.value = ''
    outputImageUrl.value = ''
    ifOutput.value = true
    ifRunning.value = false

}
</script>

<style scoped>
.main-page {
    background-color: #f2f3f5;

    scrollbar-width: none;
}

.main-container {
    margin-left: 90px;
    margin-right: 50px;

    padding: 0px 80px;
}

.aside-container {
    position: fixed;
    overflow: visible;
    height: 100%;
    width: 70px;
    background-color: #337ecc;
    padding: 30px 5px;
    display: flex;
    flex-direction: column;
    align-items: center;

    border-top-right-radius: 20px;
    border-bottom-right-radius: 20px;
}


.aside-container div {
    font-size: 12px;
    color: #c6c4ff;

    margin: 30px 0;
}

.aside-container .choice:hover {
    color: #ffffff;
    cursor: pointer;
}

.aside-icon {
    margin-bottom: 3px;
}

.header-container {
    background-color: #f2f3f5;
    padding: 60px 0 15px 0;

    display: flex;
    justify-content: center;
    align-items: center;
}

.info-choice {
    width: 100px;
    position: absolute;
    border: 1px solid #ffffff;
    border-radius: 13px;
    left: 110%;
    bottom: 30px;
    display: flex;
    flex-direction: column;

    background-color: #ffffff;

    opacity: 0;
    transition: opacity 0.25s;
}

.info-choice.visable {
    opacity: 1;
    transition: opacity 0.25s;
}

.info-choice div {
    font-size: 16px;
    color: #050505;
    padding: 5px 10px;
    margin: 0;

    display: flex;
    justify-content: center;
    cursor: pointer;
}

.info-choice div:hover {
    background-color: #f2f3f5;
}

.choice-tab {
    height: max-content;
    margin: 0 20px;
    font-size: 16px;
    font-family: PingFangSC-Regular, PingFang SC;
    color: #3f3f3f;
    cursor: pointer;

    display: flex;
    align-items: center;
}

.active {
    color: #121212;
    border-bottom: #337ecc solid 4px;
}

#choose-header {
    display: flex;
    align-items: center;
    justify-content: flex-start;

    padding: 80px 125px 40px 125px;
}

.choose-header-item {
    display: flex;
    align-items: center;

    margin: 10px 7px;
}

#choose-main {
    padding: 0px 100px 0 185px;
}

.input-area {
    margin-top: 20px;
    height: 170px;
    width: 1000px;
    padding: 10px 20px;
    border-radius: 15px;
    border: 1px solid #c6c4ff;


    background-color: #ffffff;
    transition: border-color 0.3s ease-in-out;
}

.input-area:hover {
    border-top-color: #337ecc;
    border-right-color: #34a853;
    border-bottom-color: #fbbc05;
    border-left-color: #ea4335;
}

.input-field {
    width: 100%;
    height: 100px;
    border: none;
    resize: none;

    font-size: 16px;
    color: rgb(63, 63, 63);
    font-weight: 400;
    line-height: 1.5;

    scrollbar-width: none;

    transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
}

.input-field:focus {
    outline: none;
}

.input-count {
    color: #878aab;
    font-size: 14px;
    margin-top: 10px;
}

.input-submit {
    width: 40px;
    height: 40px;
    border-radius: 8px;
    background-color: #337ecc;
    cursor: pointer;
}

.output-content {
    width: 1000px;
    height: fit-content;
    margin-top: 10px;
    padding: 10px 20px;
    border-radius: 15px;
    border: 1px solid #c6c4ff;

    background-color: #ffffff;
}
</style>