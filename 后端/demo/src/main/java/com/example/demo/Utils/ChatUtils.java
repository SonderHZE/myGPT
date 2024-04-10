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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
            // handle exception
        } finally {
            sseEmitter.complete();
        }
    }
}
