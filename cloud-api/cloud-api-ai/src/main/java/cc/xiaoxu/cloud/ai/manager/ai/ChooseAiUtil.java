package cc.xiaoxu.cloud.ai.manager.ai;

import cc.xiaoxu.cloud.bean.ai.dto.AiChatMessageDTO;
import cc.xiaoxu.cloud.bean.ai.dto.AiChatResultDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiModelEnum;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.core.utils.PrettifyDateTime;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public class ChooseAiUtil {

    public static AiChatResultDTO getAiChatResultDTO(List<AiChatMessageDTO> dtoList, String key,
                                                     AiModelEnum model, SseEmitter emitter) {

        return switch (model) {
            case MOONSHOT_V1_128K -> AiForLangchain.chat(dtoList, key, model, emitter);
            case TEST, CUSTOM -> AiForTest.test(dtoList);
            case LOCAL -> AiForLocal.chat(dtoList, key, model, emitter);
            case LOCAL_QWEN2_5_32B_INSTRUCT_AWQ, LOCAL_QWEN2_5_14B_INSTRUCT_AWQ ->
                    AiForLocalQwen.chat(dtoList, key, model, emitter);
            default -> throw new CustomException("暂不支持的类型：" + model.getCode());
        };
    }

    public static void main(String[] args) {

        List<AiChatMessageDTO> ask = Prompt.Test.simple("你是谁");
        long currentTimeMillis = System.currentTimeMillis();

        AiChatResultDTO aiChatResultDTOStream = ChooseAiUtil.getAiChatResultDTO(ask, "123", AiModelEnum.LOCAL, new SseEmitter());
        long currentTimeMillisEndStream = System.currentTimeMillis();
        System.out.println("stream 调用完成，耗时：" + PrettifyDateTime.initWithMillisecond(currentTimeMillisEndStream - currentTimeMillis).prettify());
    }
}