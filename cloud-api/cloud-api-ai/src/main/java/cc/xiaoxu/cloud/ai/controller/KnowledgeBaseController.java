package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.entity.Knowledge;
import cc.xiaoxu.cloud.ai.entity.KnowledgeBase;
import cc.xiaoxu.cloud.ai.entity.KnowledgeSection;
import cc.xiaoxu.cloud.ai.service.KnowledgeBaseService;
import cc.xiaoxu.cloud.ai.service.KnowledgeSectionService;
import cc.xiaoxu.cloud.ai.service.KnowledgeService;
import cc.xiaoxu.cloud.ai.utils.UserUtils;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeBaseAddDTO;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeBaseEditDTO;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeBasePageDTO;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeBaseVO;
import cc.xiaoxu.cloud.bean.dto.IdsDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.utils.DateUtils;
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
    private final KnowledgeService knowledgeService;
    private final KnowledgeSectionService knowledgeSectionService;

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

        Integer userId = UserUtils.getUserId();
        knowledgeBaseService.lambdaUpdate()
                .eq(KnowledgeBase::getUserId, userId)
                .in(KnowledgeBase::getId, dto.getIdList())
                .set(KnowledgeBase::getState, StateEnum.DELETE.getCode())
                .set(KnowledgeBase::getModifyId, userId)
                .set(KnowledgeBase::getModifyTime, DateUtils.getNowDate())
                .update();
        knowledgeService.lambdaUpdate()
                .eq(Knowledge::getUserId, userId)
                .in(Knowledge::getKnowledgeBaseId, dto.getIdList())
                .set(Knowledge::getState, StateEnum.DELETE.getCode())
                .set(Knowledge::getModifyId, userId)
                .set(Knowledge::getModifyTime, DateUtils.getNowDate())
                .update();
        knowledgeSectionService.lambdaUpdate()
                .eq(KnowledgeSection::getUserId, userId)
                .in(KnowledgeSection::getKnowledgeBaseId, dto.getIdList())
                .set(KnowledgeSection::getState, StateEnum.DELETE.getCode())
                .set(KnowledgeSection::getModifyId, userId)
                .set(KnowledgeSection::getModifyTime, DateUtils.getNowDate())
                .update();
    }

    @PostMapping("/page")
    @Operation(summary = "分页")
    public Page<KnowledgeBaseVO> page(@Valid @RequestBody KnowledgeBasePageDTO dto) {

        return knowledgeBaseService.pages(dto, UserUtils.getUserId());
    }
}