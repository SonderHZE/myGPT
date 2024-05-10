package com.example.demo;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.utils.Constants;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {
    static{
        Constants.apiKey="sk-d4747b14216e4ecc93ffeb5f9a2a4dc1";
    }
    public static void basicCall() throws ApiException, NoApiKeyException {
        ImageSynthesis is = new ImageSynthesis();

        ImageSynthesisParam param =
                ImageSynthesisParam.builder()
                        .model(ImageSynthesis.Models.WANX_V1)
                        .n(1)
                        .size("1024*1024")
                        .prompt("雄鹰自由自在的在蓝天白云下飞翔")
                        .build();

        ImageSynthesisResult result = is.call(param);
        System.out.println(result);
    }

    public static void main(String[] args) {
        try {
            basicCall();
        } catch (ApiException | NoApiKeyException e) {
            e.printStackTrace();
        }
    }
}

