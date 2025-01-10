package cc.xiaoxu.cloud.ai.manager;

import cc.xiaoxu.cloud.bean.ai.dto.AiChatMessageDTO;
import cc.xiaoxu.cloud.bean.ai.dto.AiChatResultDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiModelEnum;
import cc.xiaoxu.cloud.core.utils.bean.JsonUtils;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

/**
 * 通用模型调用
 */
@Slf4j
@Component
@AllArgsConstructor
public class AiManager {

    private final PersistentChatMemoryStore persistentChatMemoryStore;

    protected AiChatResultDTO chat(List<AiChatMessageDTO> aiChatMessageDto, String userId, String apiKey, AiModelEnum aiModel, SseEmitter sseEmitter) {

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(userId)
                .maxMessages(10)
                .chatMemoryStore(persistentChatMemoryStore)
                .build();

        if (null == sseEmitter) {
            return chatNotStream(aiChatMessageDto, chatMemory, userId, apiKey, aiModel);
        }
        return chatStream(aiChatMessageDto, chatMemory, userId, apiKey, aiModel, sseEmitter);
    }

    /**
     * 聊天
     */
    @SneakyThrows
    private AiChatResultDTO chatNotStream(@NonNull List<AiChatMessageDTO> aiChatMessageDTOList, ChatMemory chatMemory, String userId, String apiKey, @NonNull AiModelEnum aiModel) {

        ChatLanguageModel model = OpenAiChatModel.builder()
                .baseUrl(aiModel.getType().getUrl())
                .apiKey(apiKey)
                .modelName(aiModel.getCode())
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemory(chatMemory)
                .build();

        String res = assistant.chat("你是谁");

        AiChatResultDTO aiChatResultDTO = new AiChatResultDTO();
        aiChatResultDTO.setResult(res);
        aiChatResultDTO.setToken(0);
        aiChatResultDTO.setStatusCode(200);
        return aiChatResultDTO;
    }

    private AiChatResultDTO chatStream(List<AiChatMessageDTO> aiChatMessageDto, ChatMemory chatMemory, String userId, String apiKey, AiModelEnum modelEnum, SseEmitter emitter) {

        StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
                .baseUrl(modelEnum.getType().getUrl())
                .apiKey(apiKey)
                .modelName(modelEnum.getCode())
                .build();

        Assistant assistant = AiServices.builder(Assistant.class)
                .streamingChatLanguageModel(model)
                .chatMemory(chatMemory)
                .build();

        TokenStream tokenStream = assistant.chatStream("你是谁");

        AiChatResultDTO resultDTO = new AiChatResultDTO();
        resultDTO.setResult("stream...");
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