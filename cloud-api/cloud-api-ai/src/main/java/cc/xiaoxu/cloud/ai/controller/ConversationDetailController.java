package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.service.ConversationDetailService;
import cc.xiaoxu.cloud.ai.utils.UserUtils;
import cc.xiaoxu.cloud.bean.ai.dto.ConversationDetailPageDTO;
import cc.xiaoxu.cloud.bean.ai.vo.ConversationDetailVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "对话")
@RequestMapping("/conversation_detail")
public class ConversationDetailController {

    private final ConversationDetailService conversationDetailService;

    @PostMapping("/page")
    @Operation(summary = "分页")
    public Page<ConversationDetailVO> page(@Valid @RequestBody ConversationDetailPageDTO dto) {

        return conversationDetailService.pages(dto, UserUtils.getUserId());
    }
}