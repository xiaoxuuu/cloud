package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.entity.Knowledge;
import cc.xiaoxu.cloud.ai.service.*;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeAddCustomDTO;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeAddLocalFileEventDTO;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeAddTableDTO;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeEditStateDTO;
import cc.xiaoxu.cloud.bean.ai.enums.FileStatusEnum;
import cc.xiaoxu.cloud.bean.ai.enums.KnowledgeTypeEnum;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeExpandVO;
import cc.xiaoxu.cloud.bean.dto.PageDTO;
import cc.xiaoxu.cloud.core.utils.enums.EnumUtils;
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
@Tag(name = "AI 知识库服务")
@RequestMapping("/knowledge")
public class KnowledgeController {

    private final ALiYunApiService aLiYunApiService;
    private final KnowledgeService knowledgeService;
    private final KnowledgeSectionService knowledgeSectionService;
    private final TenantService tenantService;
    private final LocalApiService localApiService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @PostMapping("/list/{tenant}")
    @Operation(summary = "知识库查询 - 列表")
    public List<KnowledgeExpandVO> list(@PathVariable("tenant") String tenant) {

        tenantService.checkTenantThrow(tenant);
        return knowledgeService.lists(tenant);
    }

    @Deprecated
    @PostMapping("/add_ali_files/{tenant}")
    @Operation(summary = "新增知识库 - 文件批量 - 阿里")
    public void addALiFiles(@RequestPart(name = "file") MultipartFile[] files, @PathVariable("tenant") String tenant) throws InterruptedException {

        tenantService.checkTenantThrow(tenant);
        for (MultipartFile file : files) {
            addFile(file, KnowledgeTypeEnum.FILE_ALI.getCode(), tenant);
            Thread.sleep(6000);
        }
    }

    @PostMapping("/add_files/{tenant}")
    @Operation(summary = "新增知识库 - 文件批量 - 本地")
    public void addLocalFiles(@RequestPart(name = "file") MultipartFile[] files, @PathVariable("tenant") String tenant) throws InterruptedException {

        tenantService.checkTenantThrow(tenant);
        for (MultipartFile file : files) {
            addFile(file, KnowledgeTypeEnum.FILE_LOCAL.getCode(), tenant);
            Thread.sleep(6000);
        }
    }

    @PostMapping("/add_file/{tenant}")
    @Operation(summary = "新增知识库 - 文件批量 - 本地")
    public void addLocalFile(@RequestPart(name = "file") MultipartFile file, @PathVariable("tenant") String tenant) {

        tenantService.checkTenantThrow(tenant);
        addFile(file, KnowledgeTypeEnum.FILE_LOCAL.getCode(), tenant);
    }

    @PostMapping("/add_file/{type}/{tenant}")
    @Operation(summary = "新增知识库 - 文件")
    public void addFile(@RequestPart(name = "file") MultipartFile file, @PathVariable("type") String type, @PathVariable("tenant") String tenant) {

        tenantService.checkTenantThrow(tenant);
        if (EnumUtils.getByClass(type, KnowledgeTypeEnum.class) == KnowledgeTypeEnum.FILE_ALI) {
            // 阿里
            String fileId = aLiYunApiService.uploadFile(file);
            Knowledge knowledge = knowledgeService.addKnowledge(file.getOriginalFilename(), fileId, tenant, KnowledgeTypeEnum.FILE_ALI);
            knowledgeService.getALiFileUploadResult(knowledge);
        }
        if (EnumUtils.getByClass(type, KnowledgeTypeEnum.class) == KnowledgeTypeEnum.FILE_LOCAL) {
            // 本地文件上传
            String filePath = localApiService.uploadFile(file);
            Knowledge knowledge = knowledgeService.addKnowledge(file.getOriginalFilename(), filePath, tenant, KnowledgeTypeEnum.FILE_LOCAL);

            knowledgeService.lambdaUpdate()
                    .eq(Knowledge::getId, knowledge.getId())
                    .eq(Knowledge::getThreePartyFileId, knowledge.getThreePartyFileId())
                    .set(Knowledge::getStatus, FileStatusEnum.UPLOAD_PARSE_SUCCESS.getCode())
                    .set(Knowledge::getModifyTime, new Date())
                    .update();

            // 异步处理
            applicationEventPublisher.publishEvent(new KnowledgeAddLocalFileEventDTO(knowledge.getId(), tenant));
        }
    }

    @PostMapping("/add_table/{tenant}")
    @Operation(summary = "新增知识库 - 数据表")
    public void addTable(@Valid @RequestBody KnowledgeAddTableDTO dto, @PathVariable("tenant") String tenant) {

        tenantService.checkTenantThrow(tenant);
        knowledgeService.addTable(dto, tenant);
    }

    @PostMapping("/add_custom/{tenant}")
    @Operation(summary = "新增知识库 - 自定义数据")
    public void addCustom(@Valid @RequestBody KnowledgeAddCustomDTO dto, @PathVariable("tenant") String tenant) {

        tenantService.checkTenantThrow(tenant);
        knowledgeService.addCustom(dto, tenant);
    }

    @PostMapping("/edit_state/{tenant}")
    @Operation(summary = "删除知识库")
    public void editState(@Valid @RequestBody KnowledgeEditStateDTO dto, @PathVariable("tenant") String tenant) {

        tenantService.checkTenantThrow(tenant);
        knowledgeService.editState(dto, tenant);
        knowledgeSectionService.editState(dto, tenant);
    }

    @PostMapping("/page/{tenant}")
    @Operation(summary = "知识库 - 分页")
    public Page<KnowledgeExpandVO> page(@Valid @RequestBody PageDTO dto, @PathVariable("tenant") String tenant) {

        tenantService.checkTenantThrow(tenant);
        return knowledgeService.pages(dto, tenant);
    }
}