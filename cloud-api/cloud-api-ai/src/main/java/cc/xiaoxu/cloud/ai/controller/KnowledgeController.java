package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.entity.Knowledge;
import cc.xiaoxu.cloud.ai.entity.KnowledgeBase;
import cc.xiaoxu.cloud.ai.service.KnowledgeBaseService;
import cc.xiaoxu.cloud.ai.service.KnowledgeSectionService;
import cc.xiaoxu.cloud.ai.service.KnowledgeService;
import cc.xiaoxu.cloud.ai.service.LocalApiService;
import cc.xiaoxu.cloud.ai.utils.UserUtils;
import cc.xiaoxu.cloud.bean.ai.dto.*;
import cc.xiaoxu.cloud.bean.ai.enums.FileStatusEnum;
import cc.xiaoxu.cloud.bean.ai.enums.KnowledgeTypeEnum;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeExpandVO;
import cc.xiaoxu.cloud.core.exception.CustomException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "知识")
@RequestMapping("/knowledge")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;
    private final KnowledgeBaseService knowledgeBaseService;
    private final KnowledgeSectionService knowledgeSectionService;
    private final LocalApiService localApiService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @PostMapping("/add_files")
    @Operation(summary = "新增知识 - 文件批量 - 本地")
    public void addFiles(@Valid @RequestBody KnowledgeAddFileDTO dto, @RequestPart(name = "file") MultipartFile[] files) throws InterruptedException {

        for (MultipartFile file : files) {
            addFile(dto, file);
            Thread.sleep(6000);
        }
    }

    @PostMapping("/add_file")
    @Operation(summary = "新增知识 - 文件批量 - 本地")
    public void addFile(@Valid @RequestBody KnowledgeAddFileDTO dto, @RequestPart(name = "file") MultipartFile file) {

        if (!knowledgeBaseService.lambdaQuery().eq(KnowledgeBase::getId, dto.getKnowledgeBaseId()).exists()) {
            throw new CustomException("不存在的知识库");
        }

        // TODO (改造为文件服务) 本地文件上传
        log.debug("本地文件上传：{}", file.getOriginalFilename());
        String filePath = localApiService.uploadFile(file);
        log.debug("文件上传结束：{}", filePath);

        Knowledge knowledge = knowledgeService.addKnowledge(dto, file.getOriginalFilename(), filePath, UserUtils.getUserId(), KnowledgeTypeEnum.FILE_LOCAL);

        knowledgeService.lambdaUpdate()
                .eq(Knowledge::getId, knowledge.getId())
                .eq(Knowledge::getFileId, knowledge.getFileId())
                .set(Knowledge::getStatus, FileStatusEnum.UPLOAD_PARSE_SUCCESS.getCode())
                .set(Knowledge::getModifyTime, new Date())
                .update();
        log.debug("文件上传数据更新完成：{}", knowledge.getName());
        // 异步处理
        applicationEventPublisher.publishEvent(new KnowledgeAddLocalFileEventDTO(knowledge.getId(), UserUtils.getUserId()));
    }

    @Deprecated
    @PostMapping("/add_table")
    @Operation(summary = "新增知识 - 数据表")
    public void addTable(@Valid @RequestBody KnowledgeAddTableDTO dto) {

        knowledgeService.addTable(dto, UserUtils.getUserId());
    }

    @Deprecated
    @PostMapping("/add_custom")
    @Operation(summary = "新增知识 - 自定义数据")
    public void addCustom(@Valid @RequestBody KnowledgeAddCustomDTO dto) {

        knowledgeService.addCustom(dto, UserUtils.getUserId());
    }

    @PostMapping("/edit_state")
    @Operation(summary = "删除知识")
    public void editState(@Valid @RequestBody KnowledgeEditStateDTO dto) {

        knowledgeService.editState(dto, UserUtils.getUserId());
        knowledgeSectionService.editState(dto, UserUtils.getUserId());
    }

    @PostMapping("/page")
    @Operation(summary = "知识 - 分页")
    public Page<KnowledgeExpandVO> page(@Valid @RequestBody KnowledgePageDTO dto) {

        return knowledgeService.pages(dto, UserUtils.getUserId());
    }

    @PostMapping("/list")
    @Operation(summary = "列表")
    public List<KnowledgeExpandVO> list(@Valid @RequestBody KnowledgeListDTO dto) {

        return knowledgeService.lists(dto, UserUtils.getUserId());
    }
}