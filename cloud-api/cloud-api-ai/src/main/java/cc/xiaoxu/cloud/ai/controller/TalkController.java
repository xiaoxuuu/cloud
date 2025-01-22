package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.manager.TalkManager;
import cc.xiaoxu.cloud.ai.service.TenantService;
import cc.xiaoxu.cloud.bean.ai.dto.AskDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiModelEnum;
import cc.xiaoxu.cloud.core.annotation.Wrap;
import cc.xiaoxu.cloud.core.utils.StopWatchUtil;
import cc.xiaoxu.cloud.core.utils.enums.EnumUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "AI 知识库聊天服务")
@RequestMapping("/talk")
public class TalkController {

    @Resource
    private TenantService tenantService;

    @Resource
    private TalkManager talkManager;

    @PostMapping(value = "/get_model")
    @Operation(summary = "获取模型")
    public List<AiModelEnum> getModel() {

        return List.of(AiModelEnum.LOCAL_QWEN2_5_14B_INSTRUCT_AWQ, AiModelEnum.LOCAL_QWEN2_5_32B_INSTRUCT_AWQ, AiModelEnum.LOCAL, AiModelEnum.MOONSHOT_V1_128K);
    }

    @Parameters({
            @Parameter(required = true, name = "userId", description = "用户", in = ParameterIn.PATH),
            @Parameter(required = true, name = "question", description = "问题", in = ParameterIn.PATH),
    })
    @Wrap(disabled = true)
    @GetMapping(value = "/ask/{userId}/{question}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "提问 - 简洁参数")
    public SseEmitter ask(@PathVariable("userId") String userId, @PathVariable("question") String question, HttpServletResponse response) {

        return ask(userId, null, 0.7, 10, "LOCAL_QWEN2_5_32B_INSTRUCT_AWQ", question, response);
    }

    @Parameters({
            @Parameter(required = true, name = "userId", description = "用户", in = ParameterIn.PATH),
            @Parameter(required = true, name = "knowledgeId", description = "选用知识分类，留空则不限制", in = ParameterIn.PATH),
            @Parameter(required = true, name = "question", description = "问题", in = ParameterIn.PATH),
            @Parameter(required = true, name = "similarity", description = "相似度，越小越好，越大越不相似", in = ParameterIn.PATH),
            @Parameter(required = true, name = "modelTypeEnum", description = "选择的模型", in = ParameterIn.PATH),
            @Parameter(required = true, name = "similarityContentNum", description = "引用分段数，取最相似的前 n 条", in = ParameterIn.PATH)
    })
    @Wrap(disabled = true)
    @GetMapping(value = "/ask/{userId}/{knowledgeId}/{similarity}/{similarityContentNum}/{modelTypeEnum}/{question}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "提问 - 全参数")
    public SseEmitter ask(@PathVariable("userId") String userId, @PathVariable("knowledgeId") String knowledgeId,
                          @PathVariable("similarity") Double similarity, @PathVariable("similarityContentNum") Integer similarityContentNum,
                          @PathVariable("modelTypeEnum") String modelTypeEnum,
                          @PathVariable("question") String question, HttpServletResponse response) {

        // TODO 校验用户

        StopWatchUtil sw = new StopWatchUtil("知识库提问");

        sw.start("构建必备类");
        AiKimiController.setResponseHeader(response);
        AskDTO vo = new AskDTO(question, similarity, similarityContentNum, knowledgeId);
        SseEmitter emitter = new SseEmitter();

        // 提问
        talkManager.talk(vo, emitter, userId, sw, EnumUtils.getByClass(modelTypeEnum, AiModelEnum.class));
        return emitter;
    }
}