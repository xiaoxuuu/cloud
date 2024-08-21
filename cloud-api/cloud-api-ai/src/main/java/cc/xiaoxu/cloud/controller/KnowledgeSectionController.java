package cc.xiaoxu.cloud.controller;

import cc.xiaoxu.cloud.bean.ai.dto.SplitTxtDTO;
import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.entity.KnowledgeSection;
import cc.xiaoxu.cloud.service.KnowledgeSectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "知识服务")
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

    @PostMapping("/select/{id}")
    @Operation(summary = "读取测试")
    public KnowledgeSection select(@PathVariable("id") Integer id) {
        return knowledgeSectionService.lambdaQuery()
                .eq(KnowledgeSection::getId, id)
                .one();
    }

    @PostMapping("/insert")
    @Operation(summary = "写入测试")
    public KnowledgeSection insert() {

        KnowledgeSection knowledgeSection = knowledgeSectionService.lambdaQuery()
                .last(" LIMIT 1 ")
                .one();
        List<Double> embedding = Stream.generate(Math::random)
                .limit(1536)
                .toList();
        knowledgeSectionService.getBaseMapper().updateEmbedding(String.valueOf(embedding), knowledgeSection.getId());
        return knowledgeSectionService.lambdaQuery()
                .eq(KnowledgeSection::getId, knowledgeSection.getId())
                .one();
    }
}