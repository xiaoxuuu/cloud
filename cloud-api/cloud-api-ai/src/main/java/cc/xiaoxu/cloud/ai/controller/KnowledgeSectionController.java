package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.service.KnowledgeSectionService;
import cc.xiaoxu.cloud.bean.ai.dto.SplitTxtDTO;
import cc.xiaoxu.cloud.bean.dto.IdDTO;
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
@Tag(name = "知识库数据服务")
@RequestMapping("/knowledge/section")
public class KnowledgeSectionController {

    private final KnowledgeSectionService knowledgeSectionService;

    @PostMapping("/rebuild")
    @Operation(summary = "重新构建切片")
    public boolean rebuild(@Valid @RequestBody SplitTxtDTO dto) {
        return knowledgeSectionService.rebuild(dto);
    }

    @PostMapping("/calc_vector")
    @Operation(summary = "向量计算")
    public boolean calcVector(@Valid @RequestBody IdDTO vo) {
        return knowledgeSectionService.calcVector(vo);
    }
}