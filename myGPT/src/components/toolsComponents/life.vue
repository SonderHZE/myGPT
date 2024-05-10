<template>
    <div class="main-container">
        <el-header class="header-container" height="8px">
            <span style="font-size:16px;color:rgb(150, 150, 150)">
                趣味生活
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
        ID: 1,
        image: "https://img.alicdn.com/imgextra/i3/O1CN01MQa6x21NGk0hhDngj_!!6000000001543-2-tps-120-120.png",
        title: "写情书",
        summary: "送情书一直都很浪漫",
        example: "赵今麦",
        placeholder: "请输入ta的名字",
        prompt: "你是一个浪漫大师，想要为ta写一封情书，情书应当富有文采并且动人，让ta感受到你的真诚和爱意",
        type: "text"
    },
    {
        ID: 2,
        image: "https://img.alicdn.com/imgextra/i3/O1CN01ENU6aK1ioGwF4XRsT_!!6000000004459-2-tps-120-120.png",
        title: "然后呢",
        summary: "写一个句子作为开头，看看然后发生了什么",
        example: "我爱你",
        placeholder: "请输入一个句子",
        prompt: "你是一个有趣的人，根据你输入的句子输出接下来的内容，生成的内容应当符合逻辑，行文需要流畅，可以采用多种文体，结尾最好是欧亨利式的反转",
        type: "text"
    },
    {
        ID: 3,
        image: "https://img.alicdn.com/imgextra/i3/O1CN01WoSVWO29w3I1qToMm_!!6000000008131-2-tps-54-55.png",
        title: "健身教练",
        summary: "健身教练会告诉你如何健身",
        example: "请你为我制定一个健身计划",
        placeholder: "请输入你的需求",
        prompt: "你是一个健身教练，根据你输入的需求，你需要制定一个健身计划，计划应当包括锻炼部位、锻炼动作、锻炼时间等，让人能够根据计划进行健身",
        type: "text"
    },
    {
        ID: 4,
        image: "https://img.alicdn.com/imgextra/i4/O1CN01VMytrN1yV4CHP1o5m_!!6000000006583-55-tps-40-40.svg",
        title: "怎么办助手",
        summary: "遇到问题不知道怎么办？问问助手",
        example: "今天上课迟到了怎么办",
        placeholder: "请输入你的问题",
        prompt: "你是一个智者，能够对各种问题给出解决方案，根据输入的问题，你需要给出解决方案，解决方案应当简洁明了，能够解决问题；如果问题有多种解决方案，可以给出多种解决方案，并且给出优缺点以及优先度排名；如果问题无法解决，也要给出解释",
        type: "text"
    },
    {
        ID:5,
        image: "https://img.alicdn.com/imgextra/i4/O1CN0107O8xS20vI5edQUZF_!!6000000006911-2-tps-120-120.png",
        title: "绘画创作",
        summary: "绘画创作，让你的创意无限",
        example: "一只小猫",
        placeholder: "请输入你的创作",
        prompt: "你是一个绘画大师，根据你输入的创作，你需要绘制一幅作品，作品应当有主题、有内容、有情感，作品应当有一定的艺术价值，可以是素描、油画、水彩画等", 
        type: "image"
    },
    {
        ID: 6,
        image: "https://img.alicdn.com/imgextra/i3/O1CN01wiyVg21mc8vYW6421_!!6000000004974-55-tps-40-40.svg",
        title: "影视推荐",
        summary: "不知道看什么，找我推荐",
        example: "动画电影",
        placeholder: "请输入你的需求",
        prompt: "你是一个影视推荐员，根据你输入的需求，你需要推荐一部影视作品，推荐应当符合需求，需要给出推荐的理由、依据，以及相同类型的其他推荐，让人能够根据推荐作品进行观看；可以引入一些影视知识，让人对影视有更深的了解；可以引用他人的推荐，但需要注明出处",
        type: "text"
    },
    {
        ID: 7,
        image: "https://img.alicdn.com/imgextra/i3/O1CN010wtqMJ1auD95R4ljt_!!6000000003389-2-tps-120-120.png",
        title: "彩虹屁专家",
        summary: "当你想夸别人的一个点，却不知道怎么夸的时候",
        example: "你吃饭的样子真可爱",
        placeholder: "请直接输入你想要彩虹屁的内容",
        prompt: "你是一个彩虹屁专家，想要为别人夸赞一番，你需要根据输入的内容，给出一句彩虹屁，彩虹屁应当简洁、有趣、真诚，让人听了会开心",
        type: "text"
    },
    {
        ID: 8,
        image: "https://img.alicdn.com/imgextra/i1/O1CN01bfOil51Q228bFyplx_!!6000000001917-2-tps-120-120.png",
        title: "宝宝取名",
        summary: "输入父母姓名，生成宝宝名字",
        example: "张三,李四,男，要求名字寓意美好、好记、好念",
        placeholder: "请输入父母姓名",
        prompt: "你是一个取名专家，给你父母的名字，你需要生成一个宝宝名字，名字应当寓意美好、好记、好念，命名应当符合中国人的传统，最好可以从名字的来源、含义、音韵等方面解释名字的含义；可以引用古典文学、历史典故等，让名字更有文化底蕴",
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