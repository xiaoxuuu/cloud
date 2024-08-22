package cc.xiaoxu.cloud.manager.ai;

import cc.xiaoxu.cloud.bean.ai.dto.AiChatMessageDTO;
import cc.xiaoxu.cloud.bean.ai.dto.AiChatResultDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiChatModelEnum;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import com.alibaba.dashscope.aigc.conversation.Conversation;
import com.alibaba.dashscope.aigc.conversation.ConversationParam;
import com.alibaba.dashscope.aigc.conversation.ConversationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * 千问模型调用
 */
@Slf4j
public class AiForQWen {

    protected static AiChatResultDTO chat(List<AiChatMessageDTO> aiChatMessageDto, String apiKey, AiChatModelEnum model, SseEmitter sseEmitter) {
        return chatNotStream(aiChatMessageDto, apiKey, model);
    }

    /**
     * 聊天
     * @param apiKey apiKey
     * @return 返回的文字
     */
    private static AiChatResultDTO chatNotStream(List<AiChatMessageDTO> messageList, String apiKey, AiChatModelEnum model) {

        Conversation conversation = new Conversation();
        List<Message> messages = BeanUtils.populateList(messageList, Message.class);
        ConversationParam param = ConversationParam
                .builder()
                .messages(messages)
                .model(model.getCode())
                .apiKey(apiKey)
                .build();
        ConversationResult result;
        try {
            result = conversation.call(param);
        } catch (Exception e) {
            log.error("QWenAiUtil.chatNotStream:{}", e.getMessage());
            AiChatResultDTO aiChatResultDTO = new AiChatResultDTO();
            setErrorResponse(aiChatResultDTO, e.getMessage());
            return aiChatResultDTO;
        }
        String resText = result.getOutput().getText() != null ? result.getOutput().getText() : result.getOutput().getChoices().getFirst().getMessage().getContent();
        int token = result.getUsage().getInputTokens() + result.getUsage().getOutputTokens();
        return new AiChatResultDTO()
                .setResult(resText)
                .setToken(token)
                .setStatusCode(200);
    }

    private static void setErrorResponse(AiChatResultDTO dto, String response) {

        if (StringUtils.isBlank(response)) {
            dto.setStatusCode(400);
            return;
        }
        com.alibaba.fastjson2.JSONObject jsonObject = JSON.parseObject(response);
        int statusCode = jsonObject.getInteger("statusCode");
        String message = jsonObject.getString("message");
        dto.setStatusCode(statusCode);
        dto.setErrorMsg(message);
    }
}