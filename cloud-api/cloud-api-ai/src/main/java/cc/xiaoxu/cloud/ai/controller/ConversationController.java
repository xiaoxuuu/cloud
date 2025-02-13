package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.entity.Conversation;
import cc.xiaoxu.cloud.ai.service.ConversationKnowledgeService;
import cc.xiaoxu.cloud.ai.service.ConversationService;
import cc.xiaoxu.cloud.ai.service.KnowledgeSectionService;
import cc.xiaoxu.cloud.ai.utils.UserUtils;
import cc.xiaoxu.cloud.bean.ai.dto.*;
import cc.xiaoxu.cloud.bean.ai.vo.ConversationVO;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeSectionExpandVO;
import cc.xiaoxu.cloud.bean.dto.IdsDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.annotation.Wrap;
import cc.xiaoxu.cloud.core.utils.DateUtils;
import cc.xiaoxu.cloud.core.utils.StopWatchUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    private final ConversationKnowledgeService conversationKnowledgeService;

    // TODO 修改 新增会话
    @Wrap(disabled = true)
    @PostMapping(value = "/talk", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "聊天")
    public SseEmitter talk(@Valid @RequestBody ConversationAddDTO dto, HttpServletResponse response) {

        Integer userId = UserUtils.getUserId();

        StopWatchUtil sw = new StopWatchUtil("知识库提问");

        setResponseHeader(response);
        SseEmitter emitter = new SseEmitter();

        sw.start("获取知识数据");
        List<KnowledgeSectionExpandVO> similarityDataList = knowledgeSectionService.getKnowledgeSectionDataList(dto, userId, sw);
        if (CollectionUtils.isEmpty(similarityDataList)) {
            conversationService.defaultAnswer(emitter);
            return emitter;
        }

        // 提问
        conversationService.talk(emitter, sw, dto, similarityDataList, userId);
        sw.print();
        return emitter;
    }

    public static void setResponseHeader(HttpServletResponse response) {
        // 设置响应的字符编码为 UTF-8
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("X-Accel-Buffering", "no");
    }

    @PostMapping("/del")
    @Operation(summary = "删除")
    public void del(@Valid @RequestBody IdsDTO dto) {

        Integer userId = UserUtils.getUserId();
        conversationService.lambdaUpdate()
                .eq(Conversation::getUserId, userId)
                .in(Conversation::getId, dto.getIdList())
                .set(Conversation::getState, StateEnum.DELETE.getCode())
                .set(Conversation::getModifyId, userId)
                .set(Conversation::getModifyTime, DateUtils.getNowDate())
                .update();
    }

    @PostMapping("/page")
    @Operation(summary = "分页 - 对话")
    public Page<ConversationVO> page(@Valid @RequestBody ConversationPageDTO dto) {

        return conversationService.pages(dto, UserUtils.getUserId());
    }

    @PostMapping("/list")
    @Operation(summary = "列表 - 对话")
    public List<ConversationVO> list(@Valid @RequestBody ConversationListDTO dto) {

        return conversationService.lists(dto, UserUtils.getUserId());
    }

    @PostMapping("/edit")
    @Operation(summary = "编辑")
    public void edit(@Valid @RequestBody ConversationEditDTO dto) {

        conversationService.edit(dto, UserUtils.getUserId());
    }

    @PostMapping("/edit_knowledge")
    @Operation(summary = "编辑知识库关系")
    public void editKnowledge(@Valid @RequestBody ConversationEditKnowledgeBaseDTO dto) {

        conversationKnowledgeService.editKnowledge(dto, UserUtils.getUserId());
    }
}