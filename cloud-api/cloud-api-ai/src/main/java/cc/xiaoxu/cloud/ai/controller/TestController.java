package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.manager.ai.ChooseAiUtil;
import cc.xiaoxu.cloud.ai.manager.ai.Prompt;
import cc.xiaoxu.cloud.bean.ai.dto.AiChatResultDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiChatModelEnum;
import cc.xiaoxu.cloud.core.utils.bean.JsonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@Tag(name = "测试")
@RequestMapping("/test")
@AllArgsConstructor
public class TestController {

    @SneakyThrows
    @GetMapping(value = "/check")
    @Operation(summary = "check")
    public SseEmitter check() {

        SseEmitter emitter = new SseEmitter();
        AiChatResultDTO aiChatResultDTOStream = ChooseAiUtil.getAiChatResultDTO(Prompt.Test.simple("你是谁"), "sk-K2eEeGAxnHqaSmNjHpPOAKfD6PbJRfOOB8y9qZkmj1Pshksw", AiChatModelEnum.MOONSHOT_V1_128K, null);
        log.error("res: " + JsonUtils.toString(aiChatResultDTOStream));
        return emitter;
    }
}