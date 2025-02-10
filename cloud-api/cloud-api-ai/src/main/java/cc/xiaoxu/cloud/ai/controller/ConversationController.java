package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.manager.TalkManager;
import cc.xiaoxu.cloud.ai.utils.UserUtils;
import cc.xiaoxu.cloud.bean.ai.dto.AskDTO;
import cc.xiaoxu.cloud.core.annotation.Wrap;
import cc.xiaoxu.cloud.core.utils.StopWatchUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@Tag(name = "对话")
@RequestMapping("/conversation")
public class ConversationController {

    @Resource
    private TalkManager talkManager;

    // TODO 修改 新增会话
    // TODO 会话列表查询
    // TODO 会话历史记录查询
    // TODO 删除会话

    @Parameters({
            @Parameter(required = true, name = "question", description = "问题", in = ParameterIn.PATH),
    })
    @Wrap(disabled = true)
    @GetMapping(value = "/ask/{question}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "提问 - 简洁参数")
    public SseEmitter ask(@PathVariable("question") String question, HttpServletResponse response) {

        return ask(null, 0.7, 10, "1", question, response);
    }

    @Parameters({
            @Parameter(required = true, name = "knowledgeId", description = "选用知识分类，留空则不限制", in = ParameterIn.PATH),
            @Parameter(required = true, name = "question", description = "问题", in = ParameterIn.PATH),
            @Parameter(required = true, name = "similarity", description = "相似度，越小越好，越大越不相似", in = ParameterIn.PATH),
            @Parameter(required = true, name = "modelInfoId", description = "选择的模型", in = ParameterIn.PATH),
            @Parameter(required = true, name = "similarityContentNum", description = "引用分段数，取最相似的前 n 条", in = ParameterIn.PATH)
    })
    @Wrap(disabled = true)
    @GetMapping(value = "/ask/{knowledgeId}/{similarity}/{similarityContentNum}/{modelInfoId}/{question}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "提问 - 全参数")
    public SseEmitter ask(@PathVariable("knowledgeId") String knowledgeId,
                          @PathVariable("similarity") Double similarity, @PathVariable("similarityContentNum") Integer similarityContentNum,
                          @PathVariable("modelInfoId") String modelInfoId,
                          @PathVariable("question") String question, HttpServletResponse response) {

        String userId = UserUtils.getUserId();

        StopWatchUtil sw = new StopWatchUtil("知识库提问");

        sw.start("构建必备类");
        setResponseHeader(response);
        AskDTO vo = new AskDTO(question, similarity, similarityContentNum, knowledgeId);
        SseEmitter emitter = new SseEmitter();

        // 提问
        // TODO 传入模型信息
        talkManager.talk(vo, emitter, userId, sw, null);
        return emitter;
    }

    public static void setResponseHeader(HttpServletResponse response) {
        // 设置响应的字符编码为 UTF-8
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("X-Accel-Buffering", "no");
    }
}