package cc.xiaoxu.cloud.ai.manager.ai;

import cc.xiaoxu.cloud.ai.utils.OkHttpUtils;
import cc.xiaoxu.cloud.bean.ai.dto.AiChatMessageDTO;
import cc.xiaoxu.cloud.bean.ai.dto.AiChatResultDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiChatModelEnum;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.core.utils.PrettifyDateTime;
import com.alibaba.fastjson2.JSONObject;
import okhttp3.Response;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

public class ChooseAiUtil {

    public static AiChatResultDTO getAiChatResultDTO(List<AiChatMessageDTO> dtoList, String key,
                                                     AiChatModelEnum model, SseEmitter emitter) {

        return switch (model) {
            case MOONSHOT_V1_128K -> AiForMoonshot.chat(dtoList, key, model, emitter);
            case TEST, CUSTOM -> AiForTest.test(dtoList);
            case LOCAL -> AiForLocal.chat(dtoList, key, model, emitter);
            case LOCAL_QWEN2_5_32B_INSTRUCT_AWQ, LOCAL_QWEN2_5_14B_INSTRUCT_AWQ ->
                    AiForLocalQwen.chat(dtoList, key, model, emitter);
            default -> throw new CustomException("暂不支持的类型：" + model.getCode());
        };
    }

    public static void main(String[] args) throws IOException {
        Response object = OkHttpUtils.builder()
                .url("http://172.168.1.216:50004/v1/chat/completions")
                .body("{ \"model\": \"qwen\", \"message\": [ { \"role\": \"user\", \"content\": \"你是谁\"  } ] , \"stream\": false }")
                .post(true)
                .syncResponse();
        System.out.println(JSONObject.toJSONString(object));


        long currentTimeMillis = System.currentTimeMillis();
        List<AiChatMessageDTO> ask = Prompt.Test.simple("你是谁");
//        AiChatResultDTO aiChatResultDTO = ChooseAiUtil.getAiChatResultDTO(ask, "123", AiChatModelEnum.LOCAL, null);
//        System.out.println(JsonUtils.toString(aiChatResultDTO));
        long currentTimeMillisEnd = System.currentTimeMillis();
        System.out.println("调用完成，耗时：" + PrettifyDateTime.initWithMillisecond(currentTimeMillisEnd - currentTimeMillis).prettify());

        System.out.println("stream");

        AiChatResultDTO aiChatResultDTOStream = ChooseAiUtil.getAiChatResultDTO(ask, "123", AiChatModelEnum.LOCAL, new SseEmitter());
        long currentTimeMillisEndStream = System.currentTimeMillis();
        System.out.println("stream 调用完成，耗时：" + PrettifyDateTime.initWithMillisecond(currentTimeMillisEndStream - currentTimeMillisEnd).prettify());

        System.out.println("stream End");
    }
}