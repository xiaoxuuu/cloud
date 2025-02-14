package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.entity.Knowledge;
import cc.xiaoxu.cloud.ai.service.KnowledgeSectionService;
import cc.xiaoxu.cloud.ai.service.KnowledgeService;
import cc.xiaoxu.cloud.ai.utils.UserUtils;
import cc.xiaoxu.cloud.bean.ai.enums.FileStatusEnum;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeSectionVO;
import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.bean.dto.PageDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
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
@Tag(name = "知识数据")
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
    @Operation(summary = "知识向量计算")
    public boolean calcVector(@Valid @RequestBody IdDTO dto) {

        boolean b = knowledgeSectionService.calcVector(dto);
        if (!b) {
            return false;
        }
        knowledgeService.changeStatus(dto.getId(), FileStatusEnum.ALL_COMPLETED);

        knowledgeService.lambdaUpdate()
                .set(Knowledge::getState, StateEnum.ENABLE.getCode())
                .eq(Knowledge::getId, dto.getId())
                .eq(Knowledge::getState, StateEnum.PROGRESSING.getCode())
                .update();
        return true;
    }

    @PostMapping("/page")
    @Operation(summary = "分页")
    public Page<KnowledgeSectionVO> page(@Valid @RequestBody PageDTO dto) {

        return knowledgeSectionService.pages(dto, UserUtils.getUserId());
    }
}