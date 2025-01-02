package cc.xiaoxu.cloud.ai.manager.ai;

import cc.xiaoxu.cloud.ai.manager.Assistant;
import cc.xiaoxu.cloud.bean.ai.dto.AiChatMessageDTO;
import cc.xiaoxu.cloud.bean.ai.dto.AiChatResultDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiChatModelEnum;
import cc.xiaoxu.cloud.core.utils.bean.JsonUtils;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

/**
 * 通用模型调用
 */
@Slf4j
public class AiForLangchain {

    protected static AiChatResultDTO chat(List<AiChatMessageDTO> aiChatMessageDto, String apiKey, AiChatModelEnum model, SseEmitter sseEmitter) {
        if (null == sseEmitter) {
            return chatNotStream(aiChatMessageDto, apiKey, model);
        }
        return chatStream(aiChatMessageDto, apiKey, model, sseEmitter);
    }

    /**
     * 聊天
     */
    @SneakyThrows
    private static AiChatResultDTO chatNotStream(@NonNull List<AiChatMessageDTO> aiChatMessageDTOList, String apiKey, @NonNull AiChatModelEnum modelEnum) {

        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        ChatLanguageModel model = OpenAiChatModel.builder()
                .baseUrl(modelEnum.getType().getUrl())
                .apiKey(apiKey)
                .modelName(modelEnum.getCode())
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemory(chatMemory)
                .build();

        String res2 = assistant.chat("你是谁");
        String res = assistant.chat("我刚刚问的什么");

        AiChatResultDTO aiChatResultDTO = new AiChatResultDTO();
        aiChatResultDTO.setResult(res);
        aiChatResultDTO.setToken(0);
        aiChatResultDTO.setStatusCode(200);
        return aiChatResultDTO;
    }

    private static AiChatResultDTO chatStream(List<AiChatMessageDTO> aiChatMessageDto, String apiKey, AiChatModelEnum modelEnum, SseEmitter emitter) {

        StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
                .baseUrl(modelEnum.getType().getUrl())
                .apiKey(apiKey)
                .modelName(modelEnum.getCode())
                .build();

        Assistant assistant = AiServices.create(Assistant.class, model);

        TokenStream tokenStream = assistant.chatStream("你是谁");

        AiChatResultDTO resultDTO = new AiChatResultDTO();
        resultDTO.setStatusCode(200);
        tokenStream.onPartialResponse(k -> {
                    // 回答中
                    try {
                        emitter.send(k);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .onCompleteResponse(k -> {
                    // 回答结束
                    resultDTO.setResult(k.aiMessage().text());
                    resultDTO.setToken(k.tokenUsage().totalTokenCount());
                    emitter.complete();
                    System.out.println(JsonUtils.toString(resultDTO));
                })
                .onError(e -> {
                    e.printStackTrace();
                    resultDTO.setToken(0);
                    resultDTO.setStatusCode(400);
                    resultDTO.setErrorMsg(e.getMessage());
                })
                .start();
        return resultDTO;
    }
}