package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.service.KnowledgeBaseService;
import cc.xiaoxu.cloud.ai.utils.UserUtils;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeBaseAddDTO;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeBaseEditDTO;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeBasePageDTO;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeBaseVO;
import cc.xiaoxu.cloud.bean.dto.IdsDTO;
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
@Tag(name = "知识库")
@RequestMapping("/knowledge_base")
public class KnowledgeBaseController {

    private final KnowledgeBaseService knowledgeBaseService;

    @PostMapping("/add")
    @Operation(summary = "新增")
    public void add(@Valid @RequestBody KnowledgeBaseAddDTO dto) {

        knowledgeBaseService.add(dto, UserUtils.getUserId());
    }

    @PostMapping("/edit")
    @Operation(summary = "编辑")
    public void edit(@Valid @RequestBody KnowledgeBaseEditDTO dto) {

        knowledgeBaseService.edit(dto, UserUtils.getUserId());
    }

    @PostMapping("/del")
    @Operation(summary = "删除")
    public void del(@Valid @RequestBody IdsDTO dto) {

        knowledgeBaseService.del(dto, UserUtils.getUserId());
    }

    @PostMapping("/page")
    @Operation(summary = "分页")
    public Page<KnowledgeBaseVO> page(@Valid @RequestBody KnowledgeBasePageDTO dto) {

        return knowledgeBaseService.pages(dto, UserUtils.getUserId());
    }
}