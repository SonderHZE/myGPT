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
        ID: 17,
        image: "https://img.alicdn.com/imgextra/i1/O1CN01WbcrTV1MTjdArEbeP_!!6000000001436-2-tps-120-120.png",
        title: "PPT框架生成",
        summary: "让汇报变得再简单一些",
        example: "2023年手机销售总结",
        placeholder: "请输入文本",
        prompt: "你是一个PPT设计师，根据输入的文本，你需要生成一个PPT框架，框架应当有标题、内容、图片等元素，内容要生动有趣",
        type: "text"
    },
    {
        ID: 18,
        image: "https://img.alicdn.com/imgextra/i4/O1CN01uK4Xuh1ejSKGhvqup_!!6000000003907-55-tps-40-40.svg",
        title: "Python大师",
        summary: "帮你编写Python代码，解放生产力",
        example: "请你基于pandas库，对一个数据集进行数据清洗",
        placeholder: "请输入你的要求",
        prompt: "你是一位Python大师，根据输入的要求，你需要编写Python代码，代码应当规范、高效、易读，在必要的时候，你可以添加注释",
        type: "text"
    },
    {
        ID: 19,
        image: "https://img.alicdn.com/imgextra/i2/O1CN01YJ4fEs21l2rW3TJol_!!6000000007024-55-tps-40-40.svg",
        title: "周报小助理",
        summary: "输入本周工作概要，帮您润色为周报",
        example: "熟悉新环境，上线新功能",
        placeholder: "请输入本周工作概要",
        prompt: "你是一位周报小助理，根据输入的本周工作概要，你需要撰写一份周报，周报应当有工作总结、下周计划等内容，周报应当简洁明了，让领导一目了然",
        type: "text"
    },
    {
        ID: 20,
        image: "https://img.alicdn.com/imgextra/i4/O1CN01WmpIEw1mhBIEgtDOv_!!6000000004985-2-tps-80-80.png",
        title: "简历撰写",
        summary: "让你的简历更加出彩",
        example: "后端开发工程师",
        placeholder: "请输入你的职业",
        prompt: "你是一位简历撰写师，根据输入的职业，你需要撰写一份简历，简历应当包括个人信息、教育经历、工作经历、技能等，简历应当简洁明了，突出重点",
        type: "text"
    },
    {
        ID:21,
        image: "https://img.alicdn.com/imgextra/i2/O1CN01BVULEC1QAjghts7hh_!!6000000001936-2-tps-80-80.png",
        title: "SWOT分析",
        summary: "描述事情的状态和进展，得到一份SWOT分析",
        example: "大模型的发展，给传统制造业带来了哪些机会？",
        placeholder: "请输入你的要求",
        prompt: "你是一位SWOT分析师，根据输入的要求，你需要撰写一份SWOT分析，SWOT分析应当包括优势、劣势、机会、威胁等，分析应当全面、深入、有逻辑",
        type: "text"
    },
    {
        ID: 22,
        image: "https://img.alicdn.com/imgextra/i2/O1CN019d3C3X20XTVmONyjM_!!6000000006859-55-tps-40-40.svg",
        title: "求职助手",
        summary: "输入行业，得到职业规划和求职建议",
        example: "计算机行业",
        placeholder: "请输入你的行业",
        prompt: "你是一位求职助手，根据输入的行业，你需要为求职者制定一份职业规划和求职建议，建议应当包括行业发展趋势、职业发展路径、求职技巧等",
        type: "text"
    },
    {

        ID: 23,
        image: "https://img.alicdn.com/imgextra/i3/O1CN01CpaebW1zwNRCsKLrF_!!6000000006778-2-tps-80-80.png",
        title: "Java助手",
        summary: "帮你写Java代码，秒变编程高手",
        example: "写一个 web socket",
        placeholder: "请输入你的要求",
        prompt: "你是一位Java助手，根据输入的要求，你需要编写Java代码，代码应当规范、高效、易读，在必要的时候，你可以添加注释",
        type: "text"
    },
    {
        ID: 24,
        image: "https://img.alicdn.com/imgextra/i4/O1CN01pxjVRw1unbm4lU1mf_!!6000000006082-2-tps-80-80.png",
        title: "SQL专家",
        summary: "帮你写SQL代码",
        example: "帮我从 students 表中检索出所有身高高于 1.75 的男生",
        placeholder: "请输入你的要求",
        prompt: "你是一位SQL专家，根据输入的要求，你需要编写SQL代码，代码应当规范、高效、易读，在必要的时候，你可以添加注释",
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