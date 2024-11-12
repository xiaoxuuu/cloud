package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.manager.ai.ChooseAiUtil;
import cc.xiaoxu.cloud.ai.manager.ai.Prompt;
import cc.xiaoxu.cloud.ai.utils.OkHttpUtils;
import cc.xiaoxu.cloud.bean.ai.dto.AiChatResultDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiChatModelEnum;
import cc.xiaoxu.cloud.core.utils.bean.JsonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@Tag(name = "租户")
@RequestMapping("/test")
@AllArgsConstructor
public class TestController {

    @SneakyThrows
    @GetMapping(value = "/v1")
    @Operation(summary = "v1")
    public void check() {

        Response object = OkHttpUtils.builder()
                .url("http://172.168.1.216:8000/v1/chat/completions")
                .body("{ \"model\": \"qwen\", \"messages\": [ { \"role\": \"user\", \"content\": \"你是谁\"  } ] , \"stream\": false }")
                .post(true)
                .syncResponse();
        log.error("v1: " + object.body().string());

        AiChatResultDTO aiChatResultDTO = ChooseAiUtil.getAiChatResultDTO(Prompt.Test.simple("你是谁"), "123", AiChatModelEnum.LOCAL, null);
        log.error("v2: " + JsonUtils.toString(aiChatResultDTO));

        AiChatResultDTO aiChatResultDTOStream = ChooseAiUtil.getAiChatResultDTO(Prompt.Test.simple("你是谁"), "123", AiChatModelEnum.LOCAL, new SseEmitter());
        log.error("v3: " + JsonUtils.toString(aiChatResultDTOStream));
    }
}