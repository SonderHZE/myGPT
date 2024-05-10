<template>
    <div class="main-container">
        <el-header class="header-container" height="8px">
            <span style="font-size:16px;color:rgb(150, 150, 150)">
                创意文案
            </span>
        </el-header>

        <el-container>
            <el-main>
                <div class="tools-container">
                    <el-row :gutter="20">
                        <el-col :span="6" v-for="tool in tools" :key="tool.title">
                            <div class="card-item">
                                <div @click="choose(tool)">
                                    <div class="item-image">
                                        <img :src="tool.image">
                                    </div>
                                    <span style="font-size: 20px;color:rgb(38,36,76);margin: 5px 0;">{{ tool.title}}</span>
                                    <p style="color:rgb(63, 63, 63);font-size: 16px;margin: 5px 0;">{{ tool.summary }}
                                    </p>
                                </div>
                            </div>
                        </el-col>
                    </el-row>
                </div>
            </el-main>
        </el-container>
    </div>

</template>

<script setup>
import { ref } from 'vue'
const emit = defineEmits(['choose'])
const chooseTools = ref()
const tools = ref([
    {
        ID: 9,
        image: "https://img.alicdn.com/imgextra/i4/O1CN013tl6QR1j3O1Q1w8XX_!!6000000004492-55-tps-40-40.svg",
        title: "扩写助手",
        summary: "输入文本，帮你扩充内容",
        example: "今天天气不错",
        placeholder: "请输入文本",
        prompt: "探索并扩展现有叙述，构建一个生动、多层次的场景，让读者沉浸其中并获得独特见解。请在保留原文精髓的基础上，通过细腻的描写、新颖的视角转换以及富有启发性的细节增添，使扩写内容翔实饱满且逻辑严谨。尝试融入未被普遍探讨的元素或角度，激发读者的好奇心与深思，确保每一段扩展都既是故事的自然延伸，也是独立的思考触发点。尤其注重角色内心世界的细腻刻画、环境氛围的多感官描绘，以及事件背后深层意义的挖掘，力求让扩写的文本成为一次引人入胜的阅读之旅。",
        type: "text"
    },
    {
        ID: 10,
        image: "https://img.alicdn.com/imgextra/i1/O1CN01lB8VOs1NHCQ7x8iCu_!!6000000001544-55-tps-40-40.svg",
        title: "诗词创作",
        summary: "可以进行诗词创作",
        example: "请你以“春”为题写一首诗",
        placeholder: "请输入你的要求",
        prompt: "你是一位举世瞩目的文豪，根据输入的主题，你需要创作一首诗，诗歌应当符合诗歌的格律，表达出主题的意境",
        type: "text"
    },
    {
        ID: 11,
        image: "https://img.alicdn.com/imgextra/i3/O1CN01AEr6St25ljuVnnGhE_!!6000000007567-2-tps-120-120.png",
        title: "小红书文案助手",
        summary: "输入的是一个主题，你得到的会是一篇小红书文案",
        example: "帮我写一篇五一去桂林玩的攻略笔记",
        placeholder: "请告诉我你的小红书主题",
        prompt: "你是一位小红书文案撰稿人，根据输入的主题，你需要撰写一篇小红书文案，文案生动有趣，并且可以附带合适的表情和emoji、图片等",
        type: "text"
    },
    {
        ID: 12,
        image: "https://img.alicdn.com/imgextra/i2/O1CN01Q0jeGz1n8f2zLmDs5_!!6000000005045-55-tps-40-40.svg",
        title: "小说作家",
        summary: "输入标题，秒写小说",
        example: "四体",
        placeholder: "请输入你的小说标题",
        prompt: "你是一位小说作家，根据输入的小说标题，你需要创作一篇短篇小说，小说应当有故事情节、人物性格、情感表达等。你需要一次性写完，不得修改",
        type: "text"
    },
    {
        ID:13,
        image: "https://img.alicdn.com/imgextra/i3/O1CN01CKebBo1oIZEOEA1Ts_!!6000000005202-55-tps-40-40.svg",
        title: "朋友圈文案创作",
        summary: "帮你写一个朋友圈文案",
        example: "我得奖了",
        placeholder: "给出你想表达但是不知道怎么表达的内容",
        prompt: "你是一个朋友圈文案撰稿人，根据输入的内容，你需要撰写一个朋友圈文案，文案应当简洁、有趣、真实，让人看了会有共鸣",
        type: "text"
    },
    {
        ID: 14,
        image: "https://img.alicdn.com/imgextra/i4/O1CN01eTLl6z1r21mZnRfGr_!!6000000005572-2-tps-80-80.png",
        title: "祝福语集锦",
        summary: "良言让人心暖，恶言令人心寒",
        example: "给即将毕业的大学生送去期望和鼓励",
        placeholder: "请输入你的祝福对象，以及要求等",
        prompt: "你是一个祝福语专家，根据输入的祝福对象，你需要撰写一段祝福语，祝福语要在要求的前提下，富有文采、深刻并且真诚",
        type: "text"
    },
    {
        ID: 15,
        image: "https://img.alicdn.com/imgextra/i2/O1CN01EJUdKD1wSCTCAd2Tz_!!6000000006306-2-tps-120-120.png",
        title: "响亮的slogan",
        summary: "让你的口号更加吸睛",
        example: "团队励志口号",
        placeholder: "在这里输入你的口号",
        prompt: "你是一个广告文案撰稿人，根据输入的口号，你需要撰写一个响亮的slogan，slogan应当简洁、有力、富有表现力，让人一看就记住",
        type: "text"
    },
    {
        ID: 16,
        image: "https://img.alicdn.com/imgextra/i3/O1CN01O2ZMre1FWIYHEF6th_!!6000000000494-55-tps-40-40.svg",
        title: "文章润色",
        summary: "当什么文案狗，我来帮你当",
        example: "运动是释放压力、保持健康的一种方式。让我们进入运动的世界，感受身体的力量与活力，追求健康生活的每一刻。",
        placeholder: "请输入你的文案",
        prompt: "你是一位文案润色师，根据输入的文案，你需要对文案进行润色，文案应当通顺、流畅、有逻辑，让人一看就明白",
        type: "text"
    }
])

function choose(tool) {
    chooseTools.value = tool

    emit('choose', chooseTools.value)
}
</script>

<style scoped>
.main-container {
    background-color: #F2F3F5;

    margin: 35px;
}

.card-item {
    background-color: #ffffff;
    border-radius: 30px;
    margin: 20px 0;
    padding: 20px;
    height: 237px;

    box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.1);
    transition: box-shadow 0.3s;

}

.card-item:hover {
    box-shadow: 0 0 20px 0 rgba(0, 0, 0, 0.2);
    border: 1px solid #bb77dd;
}

.item-image{
    margin: 15px 0 20px 0;
}

.item-image img {
    width: 40px;
    height: 40px;
}
</style>