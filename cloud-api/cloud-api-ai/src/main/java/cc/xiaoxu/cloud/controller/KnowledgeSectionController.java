package cc.xiaoxu.cloud.controller;

import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.service.KnowledgeSectionService;
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
@Tag(name = "知识服务")
@RequestMapping("/knowledge/section")
public class KnowledgeSectionController {

    private final KnowledgeSectionService knowledgeSectionService;

    @PostMapping("/rebuild")
    @Operation(summary = "重新构建切片")
    public boolean rebuild(@Valid @RequestBody IdDTO dto) {
        return knowledgeSectionService.rebuild(dto);
    }
}