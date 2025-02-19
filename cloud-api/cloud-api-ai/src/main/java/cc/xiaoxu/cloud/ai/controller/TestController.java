package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.manager.AiManager;
import cc.xiaoxu.cloud.bean.ai.dto.AiChatResultDTO;
import cc.xiaoxu.cloud.core.utils.bean.JsonUtils;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.output.Response;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "测试")
@RequestMapping("/test")
@AllArgsConstructor
public class TestController {

    private final AiManager aiManager;

    @SneakyThrows
    @GetMapping(value = "/check")
    @Operation(summary = "check")
    public String check() {

        AiChatResultDTO aiChatResultDTOStream = aiManager.chat("你是谁", null);
        log.error("res: " + JsonUtils.toString(aiChatResultDTOStream));
        return aiChatResultDTOStream.getResult();
    }

    @SneakyThrows
    @GetMapping(value = "/embedding")
    @Operation(summary = "embedding")
    public String embedding() {

        main(null);
        return "";
    }

    public static void main(String[] args) {

        String apiKey = "sk-"; // 替换为你的 OpenAI API Key
        String baseUrl = "https://dashscope.aliyuncs.com/compatible-mode/v1"; // OpenAI API 的基础 URL

        // 初始化 Embedding 模型
        OpenAiEmbeddingModel embeddingModel = new OpenAiEmbeddingModel.OpenAiEmbeddingModelBuilder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .dimensions(1024)
                .modelName("text-embedding-v3")
                .logRequests(true)
                .logResponses(true)
                .build();

        System.out.println("当前使用的模型是: " + embeddingModel.modelName());
        Response<List<Embedding>> response = embeddingModel.embedAll(List.of(TextSegment.from("你好"), TextSegment.from("你好2"), TextSegment.from("你好2")));
        response.content().forEach(k -> {
            System.out.print(k.dimension() + " ");
            System.out.println(k);
        });
    }
}