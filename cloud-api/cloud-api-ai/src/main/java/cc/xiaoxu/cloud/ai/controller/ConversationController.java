package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.entity.Conversation;
import cc.xiaoxu.cloud.ai.service.ConversationService;
import cc.xiaoxu.cloud.ai.service.KnowledgeSectionService;
import cc.xiaoxu.cloud.ai.utils.UserUtils;
import cc.xiaoxu.cloud.bean.ai.dto.AskDTO;
import cc.xiaoxu.cloud.bean.ai.dto.ConversationAddDTO;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeSectionExpandVO;
import cc.xiaoxu.cloud.core.annotation.Wrap;
import cc.xiaoxu.cloud.core.utils.StopWatchUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "对话")
@RequestMapping("/conversation")
public class ConversationController {

    private final ConversationService conversationService;
    private final KnowledgeSectionService knowledgeSectionService;

    @Wrap(disabled = true)
    @PostMapping(value = "/talk", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "聊天")
    public SseEmitter talk(@Valid @RequestBody ConversationAddDTO dto, HttpServletResponse response) {

        Integer userId = UserUtils.getUserId();

        StopWatchUtil sw = new StopWatchUtil("知识库提问");

        sw.start("获取对话数据");
        Conversation conversation = conversationService.getOrCreateConversation(dto.getConversationId(), dto.getQuestion(), userId, dto.getModelId());

        sw.start("构建必备类");
        setResponseHeader(response);
        AskDTO vo = new AskDTO(dto.getQuestion(), 0.7, 5, dto.getKnowledgeBaseId());
        SseEmitter emitter = new SseEmitter();

        sw.start("获取知识数据");
        List<KnowledgeSectionExpandVO> similarityDataList = knowledgeSectionService.getKnowledgeSectionDataList(vo, userId, sw);
        if (CollectionUtils.isEmpty(similarityDataList)) {
            conversationService.defaultAnswer(emitter);
            return emitter;
        }

        // 提问
        conversationService.talk(vo, emitter, sw, dto, similarityDataList, userId);
        return emitter;
    }

    // TODO 修改 新增会话
    // TODO 会话列表查询
    // TODO 会话历史记录查询
    // TODO 删除会话

    public static void setResponseHeader(HttpServletResponse response) {
        // 设置响应的字符编码为 UTF-8
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("X-Accel-Buffering", "no");
    }
}