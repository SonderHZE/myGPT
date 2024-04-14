package com.example.demo.Utils;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.example.demo.Pojo.Chat;
import io.reactivex.Flowable;
import okhttp3.*;
import okio.Buffer;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.demo.Service.Impl.BaiduServiceImpl.HTTP_CLIENT;
import static com.example.demo.Service.Impl.BaiduServiceImpl.getAccessToken;

public class ChatUtils {
    public static List<Message> createAliMessageList(String messageList) {
        //1. 将messageList转换为List<Message>对象
        List<Message> messages = new ArrayList<>();

        // 将messageList按"user: "分割
        String[] userMessages = messageList.split("user: ");
        //遍历userMessages，将每个元素按"assistant: "分割
        for (String userMessage : userMessages) {
            //第一个元素是空字符串，跳过
            if (userMessage.equals("")) {
                continue;
            }
            String[] assistantMessages = userMessage.split("assistant: ");
            //此时第一个信息一定是用户信息，将其添加到messages中
            Message user = Message.builder()
                    .role(Role.USER.getValue())
                    .content(assistantMessages[0])
                    .build();
            messages.add(user);

            //如果有第二个信息，则一定是助手信息，将其添加到messages中
            if (assistantMessages.length > 1) {
                Message assistant = Message.builder()
                        .role(Role.ASSISTANT.getValue())
                        .content(assistantMessages[1])
                        .build();
                messages.add(assistant);
            }
        }
        return messages;
    }

    public static StringBuilder messageListToJson(List<Message> messages) {
        StringBuilder json = new StringBuilder("{\"messages\": [");
        for (Message message : messages) {
            String role = message.getRole();
            String content = message.getContent();
            content = content.replace("\"", "“");
            content = content.replace("\n", "");
            json.append("{\"role\":\"").append(role).append("\",\"content\":\"").append(content).append("\"},");
        }
        json.deleteCharAt(json.length() - 1);
        json.append("],\"stream\":true,\"disable_search\":false,\"enable_citation\":false}");
        return json;
    }

    public static void aliStreamCall(Chat chat, SseEmitter sseEmitter) throws NoApiKeyException, InputRequiredException, IOException {
        //1. 获取用户输入的问题，并创建一个List<Message>对象
        String inputValue = chat.getInputValue();
        String messageList = chat.getMessageList();
        // 用于存储对话记录
        List<Message> messages = new ArrayList<>();

        //如果messageList不为空，则用其构建一个Message对象
        if (messageList != null) {
            messages = ChatUtils.createAliMessageList(messageList);
        }

        //2. 创建新会话
        Generation generation = new Generation();

        //3. 用户新输入
        Message userMessage =
                Message.builder().
                        role(Role.USER.getValue()).
                        content(inputValue).
                        build();
        messages.add(userMessage);

        //4. 创建GenerationParam对象
        GenerationParam generationParam = GenerationParam.builder()
                .model("qwen-turbo")
                .messages(messages)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .topP(0.8).enableSearch(true)
                .incrementalOutput(true)
                .build();

        //5. 流式调用通义千问接口
        Flowable<GenerationResult> result = generation.streamCall(generationParam);
        StringBuilder fullContent = new StringBuilder();
        result.blockingForEach(message -> {
            StringBuilder content = new StringBuilder(message.getOutput().getChoices().get(0).getMessage().getContent());
            fullContent.append(content);
            sseEmitter.send(content.toString());
        });

        //6. 将对话记录存储到chat对象中
        StringBuilder userContent = new StringBuilder(inputValue);
        StringBuilder assistantContent = new StringBuilder(fullContent);
        if(chat.getMessageList() == null){
            chat.setMessageList("user: " + userContent + "\nassistant: " + assistantContent + "\n");
        }else{
            chat.setMessageList(chat.getMessageList() + "user: " + userContent + "\nassistant: " + assistantContent + "\n");
        }

        try {
            sseEmitter.send("CHAT COMPLETED!");
            sseEmitter.send(chat.getChatID());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            sseEmitter.complete();
        }
    }

    public static void baiduStreamCall(Chat chat, SseEmitter sseEmitter) throws IOException {
        // 获得用户输入的问题
        String inputValue = chat.getInputValue();
        String messageList = chat.getMessageList();

        // 如果messageList不为空，则将inputValue添加到messageList中
        if (messageList != null) {
            messageList += "user: " + inputValue + "\n";
        } else {
            messageList = "user: " + inputValue + "\n";
        }

        // 创建Json对象
        StringBuilder json = ChatUtils.messageListToJson(ChatUtils.createAliMessageList(messageList));

        // 向百度接口发送请求
        String accessToken = getAccessToken();
        final Boolean[] isEnd = {false};
        MediaType mediaType = MediaType.parse("application/json");
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token=" + accessToken)
                .post(RequestBody.create(mediaType, json.toString()))
                .addHeader("Content-Type", "application/json")
                .build();

        String finalMessageList = messageList;
        HTTP_CLIENT.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 请求失败的处理
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                } else {
                    try (ResponseBody responseBody = response.body()) {
                        if (responseBody != null) {
                            // 流式处理响应体
                            responseBody.source().timeout().timeout(60, TimeUnit.SECONDS);
                            Buffer buffer = new Buffer();
                            StringBuilder content = new StringBuilder();

                            // 实际流式处理逻辑可能涉及循环读取buffer部分内容，例如：
                            while (true) {
                                long read = responseBody.source().read(buffer, 8192);
                                if (read == -1) {
                                    break;
                                }

                                String all = buffer.readString(Charset.defaultCharset());
                                int start = all.indexOf("result") + 9;
                                int end = all.indexOf("need_clear_history") - 3;
                                String result = all.substring(start, end);
                                content.append(result);
                                sseEmitter.send(result);

                            }

                            // 发送完成消息
                            sseEmitter.send("CHAT COMPLETED!");
                            sseEmitter.send(chat.getChatID());

                            // 将对话记录存储到chat对象中
                            chat.setMessageList(finalMessageList + "assistant: " + content + "\n");
                            sseEmitter.complete();

                            isEnd[0] = true;
                        }
                    }
                }

            }
        });

        while(!isEnd[0]){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
