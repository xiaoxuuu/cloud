package cc.xiaoxu.cloud.ai.manager.ai;

import cc.xiaoxu.cloud.bean.ai.dto.AiChatMessageDTO;
import cc.xiaoxu.cloud.bean.ai.dto.AiChatResultDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiChatModelEnum;
import cc.xiaoxu.cloud.core.exception.CustomException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public class ChooseAiUtil {

    public static AiChatResultDTO getAiChatResultDTO(List<AiChatMessageDTO> dtoList, String key,
                                                     AiChatModelEnum model, SseEmitter emitter) {

        return switch (model) {
            case MOONSHOT_V1_128K -> AiForMoonshot.chat(dtoList, key, model, emitter);
            case TEST, CUSTOM -> AiForTest.test(dtoList);
            case Q_WEN_72B_CHAT, Q_WEN_MAX, Q_WEN_7B_CHAT, Q_WEN_LONG_CHAT ->
                    AiForQWen.chat(dtoList, key, model, emitter);
            default -> throw new CustomException("暂不支持的类型：" + model.getCode());
        };
    }
}