package cc.xiaoxu.cloud.ai.manager;

import cc.xiaoxu.cloud.ai.entity.ConversationDetail;
import cc.xiaoxu.cloud.ai.entity.ModelInfo;
import cc.xiaoxu.cloud.ai.service.ConversationDetailService;
import cc.xiaoxu.cloud.bean.ai.dto.AiChatResultDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiChatRoleEnum;
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
import java.util.Map;

/**
 * 通用模型调用
 */
@Slf4j
@Component
@AllArgsConstructor
public class AiManager {

    private final PersistentChatMemoryStore persistentChatMemoryStore;
    private final ConversationDetailService conversationDetailService;

    public void knowledge(String question, String knowledgeData, Integer conversationId, ModelInfo modelInfo, SseEmitter emitter, Integer userId, ConversationDetail conversationDetail) {

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(conversationId)
                .maxMessages(10)
                .chatMemoryStore(persistentChatMemoryStore)
                .build();

        // TODO 缓存 model，一个 modelEnum 只加载一次
        StreamingChatLanguageModel model = OpenAiStreamingChatModel.builder()
                .baseUrl(modelInfo.getUrl())
                .apiKey(modelInfo.getApiKey())
                .modelName(modelInfo.getModel())
                .logRequests(true)
//                .logResponses(true)
                .build();

        KnowledgeAssistant knowledgeAssistant = AiServices.builder(KnowledgeAssistant.class)
                .streamingChatLanguageModel(model)
                .chatMemory(chatMemory)
                .build();

        TokenStream tokenStream = knowledgeAssistant.knowledge(question, knowledgeData);

        tokenStream.onPartialResponse(k -> {
                    // 回答中
                    try {
                        emitter.send(SseVO.msg(k));
                        System.out.print(k);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .onCompleteResponse(k -> {
                    conversationDetailService.create(k.aiMessage().text(), conversationId, userId, modelInfo.getId(), AiChatRoleEnum.AI, k.tokenUsage().outputTokenCount());
                    conversationDetailService.lambdaUpdate()
                            .eq(ConversationDetail::getId, conversationDetail.getId())
                            .set(ConversationDetail::getToken, k.tokenUsage().inputTokenCount())
                            .update();
                    try {
                        emitter.send(SseVO.paramMap(Map.of("TOKEN", k.tokenUsage().totalTokenCount())));
                        emitter.send(SseVO.end());
                        emitter.complete();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
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
    public AiChatResultDTO chat(String question, ModelInfo modelInfo) {

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