package com.example.demo.Service.Impl;
import com.alibaba.dashscope.common.Message;
import com.example.demo.Mapper.ChatMapper;
import com.example.demo.Utils.ChatUtils;

import java.io.*;
import java.util.List;

import okhttp3.*;
import okhttp3.Request;
import org.json.JSONObject;


public class BaiduServiceImpl {

    private ChatMapper chatMapper;
    public static final String API_KEY = "IrwsY0SE5ba9KvhmMXcWzPTS";
    public static final String SECRET_KEY = "IhqYv5CqzyG1PvKV0YZCvBAO9vDZNH1O";
    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder().build();//创建OkHttpClient对象

    public static void main(String []args) throws IOException{
        MediaType mediaType = MediaType.parse("application/json");//设置请求体的类型
//        {
//    "messages": [
//        {
//            "role": "user",
//            "content": "怒号"
//        }
//    ],
//    "disable_search": false,
//    "enable_citation": false
//}

        // 这里用硬编码模拟
        String messageList = "user: 我叫小艺\n" +
                "assistant: 你好，小艺！很高兴认识你。有什么我能帮助你的吗？\n" +
                "user: 我叫啥？\n" +
                "assistant: 你刚才自我介绍说是“小艺”，所以你的名字就是小艺。如果你有其他的名字或者需要我称呼你为别的什么，随时告诉我哦。\n" +
                "user: 我是谁\n" +
                "assistant: 你是在这里提问的用户，我叫你“小艺”。如果你在虚拟世界中有一个特定的身份或昵称，那可能需要你自己补充说明。如果你需要，我可以帮助解答问题或提供信息。\n" +
                "user: 死了都要爱\n" +
                "assistant: \"死了都要爱\"这句话通常是一种情感表达，表示对某人或某事的极度热爱和执着，即使面临生死也不放弃。它可能来源于歌曲《死了都要爱》。如果你需要关于这首歌的信息或者想分享与这首歌相关的情感，我很乐意倾听。\n" +
                "user: 这首歌是谁唱的\n" +
                "assistant: 《死了都要爱》是由台湾的摇滚乐团信乐团演唱的。信乐团由团长兼鼓手黄迈可、主唱刘飞鸣、吉他手孙志群、贝斯手刘晓华和键盘手傅超华组成。这首歌是2002年发行的，收录在他们的同名专辑《SHIN同名专辑》中，作词姚若龙，作曲是韩国的柳海准（Yoo Hae-jun）。这首歌因为其强烈的旋律和深情的歌词而广受欢迎。\n" +
                "user: 你确定吗\n" +
                "assistant: 是的，我的回答是准确的。《死了都要爱》确实是信乐团演唱的，如果你对这首歌有其他的问题，比如歌词、MV内容或者相关背景故事，我也很乐意帮你查找。\n" +
                "user: 我相信你\n" +
                "assistant: 如果你有任何疑问或者需要进一步的帮助，请随时告诉我。\n";

        // 从messageList中提取出messages
        List<Message> messages = ChatUtils.createAliMessageList(messageList);

        String mesaagesBody = "\"messages\":[";
        for(Message message: messages){
            mesaagesBody += "{" +
                    "            \"role\": \"" + message.getRole() + "\"," +
                    "            \"content\": \"" + message.getContent() + "\"\n" +
                    "        },";
        }

        // 加入问题
        mesaagesBody += "{\n" +
                "            \"role\": \"" + "user" + "\"," +
                "            \"content\": \"" + "你好" + "\"\n" +
                "        }";
        mesaagesBody += "]";

        //设置请求体
        RequestBody body = RequestBody.create(mediaType, "{\n" +
                mesaagesBody +
                "    \"disable_search\": false,\n" +
                "    \"enable_citation\": false\n" +
                "}");

        System.out.println("{\n" +
                mesaagesBody +
                "    \"disable_search\": false,\n" +
                "    \"enable_citation\": false\n" +
                "}");
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token=" + getAccessToken())
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        System.out.println(response.body().string());

    }


    /**
     * 从用户的AK，SK生成鉴权签名（Access Token）
     *
     * @return 鉴权签名（Access Token）
     * @throws IOException IO异常
     */
    static String getAccessToken() throws IOException {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "grant_type=client_credentials&client_id=" + API_KEY
                + "&client_secret=" + SECRET_KEY);
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/oauth/2.0/token")
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = HTTP_CLIENT.newCall(request).execute();
        return new JSONObject(response.body().string()).getString("access_token");
    }
}
