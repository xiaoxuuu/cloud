package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.manager.AiManager;
import cc.xiaoxu.cloud.bean.ai.dto.AiChatResultDTO;
import cc.xiaoxu.cloud.core.utils.bean.JsonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "测试")
@RequestMapping("/test")
@AllArgsConstructor
public class TestController {

    private final AiManager aiManager;

    @SneakyThrows
    @GetMapping(value = "/check")
    @Operation(summary = "check")
    public String check() {

        AiChatResultDTO aiChatResultDTOStream = aiManager.chat("你是谁", null);
        log.error("res: " + JsonUtils.toString(aiChatResultDTOStream));
        return aiChatResultDTOStream.getResult();
    }
}