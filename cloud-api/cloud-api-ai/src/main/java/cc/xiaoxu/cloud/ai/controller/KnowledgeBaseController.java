package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.service.KnowledgeSectionService;
import cc.xiaoxu.cloud.ai.service.KnowledgeService;
import cc.xiaoxu.cloud.ai.utils.UserUtils;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeEditStateDTO;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeExpandVO;
import cc.xiaoxu.cloud.bean.dto.PageDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "知识库")
@RequestMapping("/knowledge_base")
public class KnowledgeBaseController {

    private final KnowledgeService knowledgeService;
    private final KnowledgeSectionService knowledgeSectionService;

    // TODO
    @PostMapping("/list")
    @Operation(summary = "列表")
    public List<KnowledgeExpandVO> list() {

        return knowledgeService.lists(UserUtils.getUserId());
    }

    // TODO
    @PostMapping("/add_files")
    @Operation(summary = "新增")
    public void addFiles(@RequestPart(name = "file") MultipartFile[] files) throws InterruptedException {

    }

    // TODO
    @PostMapping("/edit_state")
    @Operation(summary = "删除")
    public void editState(@Valid @RequestBody KnowledgeEditStateDTO dto) {

        knowledgeService.editState(dto, UserUtils.getUserId());
        knowledgeSectionService.editState(dto, UserUtils.getUserId());
    }

    // TODO
    @PostMapping("/page")
    @Operation(summary = "分页")
    public Page<KnowledgeExpandVO> page(@Valid @RequestBody PageDTO dto) {

        return knowledgeService.pages(dto, UserUtils.getUserId());
    }
}