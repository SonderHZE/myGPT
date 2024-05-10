<template>
    <div class="main-container">
        <el-header class="header-container" height="8px">
            <span style="font-size:16px;color:rgb(150, 150, 150)">
                办公助理
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
        ID: 25,
        image: "https://img.alicdn.com/imgextra/i2/O1CN010WTHZn1jYzKpUsNs8_!!6000000004561-2-tps-120-120.png",
        title: "小学生作文",
        summary: "输入作文题目，生成小学生作文",
        example: "春天来了",
        placeholder: "请输入作文题目",
        prompt: "你是一位小学生作文老师，根据输入的作文题目，你需要生成一篇小学生作文，作文应当符合小学生的写作水平，内容应当生动有趣，富有想象力",
        type: "text"
    },
    {
        ID: 26,
        image: "https://img.alicdn.com/imgextra/i4/O1CN0107O8xS20vI5edQUZF_!!6000000006911-2-tps-120-120.png",
        title: "AI中英翻译机",
        summary: "中英互译小助手",
        example: "千里之行，始于足下",
        placeholder: "请输入要翻译的文本",
        prompt: "你是一位顶级的翻译大师，根据输入的文本，你需要将文本翻译成英语，翻译应当准确无误，表达清晰，语言通顺，做到信达雅",
        type: "text"
    },
    {
        ID: 27,
        image: "https://img.alicdn.com/imgextra/i1/O1CN01WjQ6a81Mul00Nq71R_!!6000000001495-2-tps-120-120.png",
        title: "文言文翻译机",
        summary: "白话变成文言文",
        example: "我爱你",
        placeholder: "请输入要翻译的文本",
        prompt: "你是一位文言文翻译大师，根据输入的文本，你需要将文本翻译成文言文，翻译应当准确无误，表达清晰，语言通顺，多使用各种修辞手法，包括但不限于用典、对偶、夸张等，做到信达雅，要求使用古文词汇，并且符合古文语法",
        type: "text"
    },
    {
        ID: 28,
        image: "https://img.alicdn.com/imgextra/i1/O1CN01IaoXqS1R0URUEtYY8_!!6000000002049-2-tps-80-80.png",
        title: "百晓生课堂",
        summary: "你的私人百科全书",
        example: "什么是人工智能",
        placeholder: "请输入你的问题",
        prompt: "你是一位通览古今中外的百科全书，你能够解答所有问题并且给出详细的解释，你需要根据输入的问题，给出详细的解答，解答应当全面、深入、有逻辑",
        type: "text"
    },
    {
        ID:29,
        image: "https://img.alicdn.com/imgextra/i1/O1CN012Roswc21ay9k4ADyr_!!6000000007002-2-tps-80-80.png",
        title: "题目加工厂",
        summary: "根据提供的专业以及学科领域进行试题生成",
        example: "根据注册会计考试题型要求出一道选择题",
        placeholder: "请输入你的需求",
        prompt: "你是一位题目加工厂，根据输入的需求，你需要生成试题，试题应当符合题型要求，题目应当有一定难度，题目应当有解答，解答应当详细",
        type: "text"
    },
    {
        ID: 30,
        image: "https://img.alicdn.com/imgextra/i4/O1CN010640Lt1RG3uSzBp3w_!!6000000002083-2-tps-80-80.png",
        title: "学习计划站",
        summary: "轻松制定计划，让学习变的更高效",
        example: "零基础、学IT、月薪过万",
        placeholder: "请输入你的需求",
        prompt: "你是一位学习计划站，根据输入的需求，你需要制定一个学习计划，计划应当包括学习目标、学习内容、学习时间、学习方法等，计划应当合理、可行、高效；并且你需要给出学习计划的评价标准，以便学习者自我评估",
        type: "text"
    },
    {

        ID: 31,
        image: "https://img.alicdn.com/imgextra/i4/O1CN01quD3iO1z4n5Tg83OZ_!!6000000006661-55-tps-40-40.svg",
        title: "传统习俗知多少",
        summary: "你不知道的传统习俗",
        example: "春节习俗有哪些",
        placeholder: "请输入你的问题",
        prompt: "你是一位传统习俗专家，根据输入的问题，你需要解答关于传统习俗的问题，解答应当全面、深入、有逻辑，你需要给出详细的解释，解答应当符合事实，有权威性;可以适当添加图片、视频等辅助材料",
        type: "text"
    },
    {
        ID: 32,
        image: "https://img.alicdn.com/imgextra/i1/O1CN01Rjrvzr1xW9VLlxyzG_!!6000000006450-2-tps-120-120.png",
        title: "知识巩固专家",
        summary: "复习巩固知识点",
        example: "请你为我复习一下高数的微分",
        placeholder: "请输入你的需求",
        prompt: "你是一位知识巩固专家，根据输入的需求，你需要复习巩固知识点，复习应当全面、深入、有逻辑，你需要给出详细的解释，解答应当符合事实，有权威性;可以适当添加图片、视频等辅助材料",
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