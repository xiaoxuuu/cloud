package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.entity.Knowledge;
import cc.xiaoxu.cloud.ai.service.KnowledgeSectionService;
import cc.xiaoxu.cloud.ai.service.KnowledgeService;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeSectionVO;
import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.bean.dto.PageDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "AI 知识库数据服务")
@RequestMapping("/knowledge/section")
public class KnowledgeSectionController {

    private final KnowledgeService knowledgeService;
    private final KnowledgeSectionService knowledgeSectionService;

    @PostMapping("/rebuild_section")
    @Operation(summary = "重新构建文件切片")
    public boolean rebuildSection(@Valid @RequestBody IdDTO dto) {

        Knowledge knowledge = knowledgeService.lambdaQuery().eq(Knowledge::getId, dto.getId()).one();
        return knowledgeSectionService.rebuildSection(knowledge);
    }

    @PostMapping("/calc_vector")
    @Operation(summary = "知识库向量计算")
    public boolean calcVector(@Valid @RequestBody IdDTO vo) {
        return knowledgeSectionService.calcVector(vo);
    }

    @PostMapping("/page")
    @Operation(summary = "知识库数据 - 分页")
    public Page<KnowledgeSectionVO> page(@Valid @RequestBody PageDTO dto, @PathVariable("tenant") String tenant) {

        return knowledgeSectionService.pages(dto, tenant);
    }
}