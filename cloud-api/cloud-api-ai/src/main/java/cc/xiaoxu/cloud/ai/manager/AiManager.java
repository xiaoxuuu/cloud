package cc.xiaoxu.cloud.ai.manager;

import cc.xiaoxu.cloud.bean.ai.dto.AiChatResultDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiModelEnum;
import cc.xiaoxu.cloud.bean.ai.vo.SseVO;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

/**
 * 通用模型调用
 */
@Slf4j
@Component
@AllArgsConstructor
public class AiManager {

    // TODO 聊天历史持久化
    private final PersistentChatMemoryStore persistentChatMemoryStore;

    public void knowledge(String question, String knowledgeData, Integer conversationId, String apiKey, AiModelEnum modelEnum, SseEmitter emitter) {

        // TODO 聊天历史持久化
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
//                .id(conversationId)
                .maxMessages(10)
//                .chatMemoryStore(persistentChatMemoryStore)
                .build();

        // TODO 缓存 model，一个 modelEnum 只加载一次
        StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
                .baseUrl(modelEnum.getType().getUrl())
                .apiKey(apiKey)
                .modelName(modelEnum.getCode())
                .logRequests(true)
                .logResponses(true)
                .build();

        KnowledgeAssistant knowledgeAssistant = AiServices.builder(KnowledgeAssistant.class)
                .streamingChatLanguageModel(model)
                .chatMemory(chatMemory)
                .build();

        TokenStream tokenStream = knowledgeAssistant.knowledge(question, knowledgeData);
        chat(emitter, tokenStream);
    }

    private void chat(SseEmitter emitter, TokenStream tokenStream) {
        tokenStream.onPartialResponse(k -> {
                    // 回答中
                    try {
//                        emitter.send(k);
                        emitter.send(SseVO.msg(k));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .onCompleteResponse(k -> {
                    // 回答结束
                    emitter.complete();
                })
                .onError(e -> {
                    // TODO 保存错误日志
                    e.printStackTrace();
                })
                .start();
    }

    /**
     * 聊天
     */
    public AiChatResultDTO chat(String question, String apiKey, @NonNull AiModelEnum aiModel) {

        ChatLanguageModel model = OpenAiChatModel.builder()
                .baseUrl(aiModel.getType().getUrl())
                .apiKey(apiKey)
                .modelName(aiModel.getCode())
                .logRequests(true)
                .logResponses(true)
                .build();

        KnowledgeAssistant knowledgeAssistant = AiServices.builder(KnowledgeAssistant.class)
                .chatLanguageModel(model)
//                .chatMemory(chatMemory)
                .build();

        String res = knowledgeAssistant.chat(question);

        AiChatResultDTO aiChatResultDTO = new AiChatResultDTO();
        aiChatResultDTO.setResult(res);
        aiChatResultDTO.setToken(0);
        aiChatResultDTO.setStatusCode(200);
        return aiChatResultDTO;
    }
}