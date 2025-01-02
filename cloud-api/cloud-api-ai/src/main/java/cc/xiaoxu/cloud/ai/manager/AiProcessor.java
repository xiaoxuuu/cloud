package cc.xiaoxu.cloud.ai.manager;

import cc.xiaoxu.cloud.ai.manager.ai.ChooseAiUtil;
import cc.xiaoxu.cloud.ai.service.AiResultLogService;
import cc.xiaoxu.cloud.bean.ai.dto.AiChatMessageDTO;
import cc.xiaoxu.cloud.bean.ai.dto.AiChatResultDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiChatModelEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Set;

@Slf4j
@Component
@AllArgsConstructor
public class AiProcessor {

    private final AiResultLogService aiResultLogService;
    private static final Set<String> sensitiveResultSet = Set.of("Output data may contain inappropriate content.", "The request was rejected because it was considered high risk");

    public AiChatResultDTO chat(ChatInfo chatInfo) {

        return chat(chatInfo.getChatMessageList(),
                chatInfo.getTalkTypeEnum().getIntroduction(),
                chatInfo.getModelTypeEnum(),
                chatInfo.getApiKey(),
                chatInfo.getRetryTime() + 1,
                chatInfo.getSseEmitter(),
                chatInfo.getRemark());
    }

    /**
     * @param chatMessageList 请求参数
     * @param talkType          业务类型
     * @param model           模型类型
     * @param surplus         剩余次数
     * @return 调用结果
     */
    private AiChatResultDTO chat(List<AiChatMessageDTO> chatMessageList, String talkType, AiChatModelEnum model,
                                 String apiKey, Integer surplus, SseEmitter sseEmitter, String remark) {

        surplus--;
        if (surplus < 0) {
            return null;
        }

        // 调用
        long l = System.currentTimeMillis();
        //
        AiChatResultDTO chat = ChooseAiUtil.getAiChatResultDTO(chatMessageList, apiKey, model, sseEmitter);
        chat.setSummaryDuration((int) (System.currentTimeMillis() - l));
        aiResultLogService.saveLog(chatMessageList, talkType, model, chat, l, remark);
        if (200 == chat.getStatusCode()) {
            return chat;
        }
        if (sensitiveResultSet.contains(chat.getErrorMsg())) {
            log.warn("{} 答案涉及敏感词，停止重试", model.getCode());
            return chat;
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        AiChatResultDTO chatNext = chat(chatMessageList, talkType, model, apiKey, surplus, sseEmitter, remark);
        if (null == chatNext) {
            return chat;
        }
        return chatNext;
    }
}