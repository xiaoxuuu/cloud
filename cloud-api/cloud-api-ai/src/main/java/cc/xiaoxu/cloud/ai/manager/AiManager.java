package cc.xiaoxu.cloud.ai.manager;

import cc.xiaoxu.cloud.ai.dao.ModelInfoMapper;
import cc.xiaoxu.cloud.ai.entity.ModelInfo;
import cc.xiaoxu.cloud.bean.ai.dto.AiChatResultDTO;
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
    private final ModelInfoMapper modelInfoMapper;

    public void knowledge(String question, String knowledgeData, Integer conversationId, Integer modelInfoId, SseEmitter emitter) {

        ModelInfo modelInfo = modelInfoMapper.selectById(modelInfoId);

        // TODO 聊天历史持久化
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
//                .id(conversationId)
                .maxMessages(10)
//                .chatMemoryStore(persistentChatMemoryStore)
                .build();

        // TODO 缓存 model，一个 modelEnum 只加载一次
        StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
                .baseUrl(modelInfo.getUrl())
                .apiKey(modelInfo.getApiKey())
                .modelName(modelInfo.getModel())
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
    public AiChatResultDTO chat(String question, Integer modelInfoId) {

        ModelInfo modelInfo = modelInfoMapper.selectById(modelInfoId);

        ChatLanguageModel model = OpenAiChatModel.builder()
                .baseUrl(modelInfo.getUrl())
                .apiKey(modelInfo.getApiKey())
                .modelName(modelInfo.getName())
                .logRequests(true)
                .logResponses(true)
                .build();

        KnowledgeAssistant knowledgeAssistant = AiServices.builder(KnowledgeAssistant.class)
                .chatLanguageModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        String res = knowledgeAssistant.chat(question);

        AiChatResultDTO aiChatResultDTO = new AiChatResultDTO();
        aiChatResultDTO.setResult(res);
        aiChatResultDTO.setToken(0);
        aiChatResultDTO.setStatusCode(200);
        return aiChatResultDTO;
    }
}