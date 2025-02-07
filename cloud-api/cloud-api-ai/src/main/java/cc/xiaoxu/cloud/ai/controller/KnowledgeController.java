package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.entity.Knowledge;
import cc.xiaoxu.cloud.ai.service.KnowledgeSectionService;
import cc.xiaoxu.cloud.ai.service.KnowledgeService;
import cc.xiaoxu.cloud.ai.service.LocalApiService;
import cc.xiaoxu.cloud.ai.utils.UserUtils;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeAddCustomDTO;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeAddLocalFileEventDTO;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeAddTableDTO;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeEditStateDTO;
import cc.xiaoxu.cloud.bean.ai.enums.FileStatusEnum;
import cc.xiaoxu.cloud.bean.ai.enums.KnowledgeTypeEnum;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeExpandVO;
import cc.xiaoxu.cloud.bean.dto.PageDTO;
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
    private final KnowledgeSectionService knowledgeSectionService;
    private final LocalApiService localApiService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @PostMapping("/list")
    @Operation(summary = "列表")
    public List<KnowledgeExpandVO> list() {

        return knowledgeService.lists(UserUtils.getUserId());
    }

    @PostMapping("/add_files")
    @Operation(summary = "新增知识 - 文件批量 - 本地")
    public void addFiles(@RequestPart(name = "file") MultipartFile[] files) throws InterruptedException {

        for (MultipartFile file : files) {
            addFile(file);
            Thread.sleep(6000);
        }
    }

    @PostMapping("/add_file")
    @Operation(summary = "新增知识 - 文件批量 - 本地")
    public void addFile(@RequestPart(name = "file") MultipartFile file) {

        // 本地文件上传
        log.debug("本地文件上传：{}", file.getOriginalFilename());
        String filePath = localApiService.uploadFile(file);
        log.debug("文件上传结束：{}", filePath);
        Knowledge knowledge = knowledgeService.addKnowledge(file.getOriginalFilename(), filePath, UserUtils.getUserId(), KnowledgeTypeEnum.FILE_LOCAL);

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

    @PostMapping("/add_table")
    @Operation(summary = "新增知识 - 数据表")
    public void addTable(@Valid @RequestBody KnowledgeAddTableDTO dto) {

        knowledgeService.addTable(dto, UserUtils.getUserId());
    }

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
    public Page<KnowledgeExpandVO> page(@Valid @RequestBody PageDTO dto) {

        return knowledgeService.pages(dto, UserUtils.getUserId());
    }
}