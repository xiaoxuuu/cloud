package cc.xiaoxu.cloud.ai.manager.ai;

import cc.xiaoxu.cloud.bean.ai.dto.AiChatMessageDTO;
import cc.xiaoxu.cloud.bean.ai.dto.AiChatResultDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiChatModelEnum;
import cc.xiaoxu.cloud.bean.ai.vo.SseVO;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import com.alibaba.dashscope.aigc.conversation.Conversation;
import com.alibaba.dashscope.aigc.conversation.ConversationParam;
import com.alibaba.dashscope.aigc.conversation.ConversationResult;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.ResultCallback;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * 千问模型调用
 */
@Slf4j
public class AiForQWen {

    protected static AiChatResultDTO chat(List<AiChatMessageDTO> aiChatMessageDto, String apiKey, AiChatModelEnum model, SseEmitter sseEmitter) {
        if (null == sseEmitter) {
            return chatNotStream(aiChatMessageDto, apiKey, model);
        }
        return chatStream(aiChatMessageDto, apiKey, model, sseEmitter);
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

    private static AiChatResultDTO chatStream(List<AiChatMessageDTO> aiChatMessageDto, String apiKey, AiChatModelEnum model, SseEmitter emitter) {

        AiChatResultDTO resultDTO = new AiChatResultDTO();
        try {

            Generation gen = new Generation();
            List<Message> messages = aiChatMessageDto.stream().map(k -> (Message) Message.builder().role(k.getRole()).content(k.getContent()).build()).toList();
            GenerationParam param = buildGenerationParam(messages, model, apiKey);

            // 处理回调
            String fullContent = streamCallWithCallback(gen, param, emitter);
            resultDTO.setResult(fullContent);
            resultDTO.setStatusCode(200);

        } catch (Exception e) {
            e.printStackTrace();
            resultDTO.setToken(0);
            resultDTO.setStatusCode(400);
            resultDTO.setErrorMsg(e.getMessage());
        }
        return resultDTO;
    }

    public static String streamCallWithCallback(Generation gen, GenerationParam param, SseEmitter emitter)
            throws NoApiKeyException, ApiException, InputRequiredException, InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        StringBuilder fullContent = new StringBuilder();

        gen.streamCall(param, new ResultCallback<>() {
            @Override
            public void onEvent(GenerationResult message) {
                String content = message.getOutput().getChoices().get(0).getMessage().getContent();
                try {
                    emitter.send(SseVO.msg(content));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                fullContent.append(content);
            }

            @Override
            public void onError(Exception err) {
                log.error("Exception occurred: {}", err.getMessage());
                try {
                    emitter.send(SseVO.end());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                emitter.complete();
                semaphore.release();
            }

            @Override
            public void onComplete() {
                try {
                    emitter.send(SseVO.end());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                emitter.complete();
                semaphore.release();
            }
        });

        semaphore.acquire();
        return fullContent.toString();
    }

    private static GenerationParam buildGenerationParam(List<Message> userMsg, AiChatModelEnum model, String apiKey) {
        return GenerationParam.builder()
                .model(model.getCode())
                .messages(userMsg)
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .topP(0.8)
                .apiKey(apiKey)
                .incrementalOutput(true)
                .build();
    }
}