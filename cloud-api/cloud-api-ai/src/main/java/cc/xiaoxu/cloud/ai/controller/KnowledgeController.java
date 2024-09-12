package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.service.ALiYunService;
import cc.xiaoxu.cloud.ai.service.KnowledgeSectionService;
import cc.xiaoxu.cloud.ai.service.KnowledgeService;
import cc.xiaoxu.cloud.ai.service.TenantService;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeAddCustomDTO;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeAddTableDTO;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeEditStateDTO;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeExpandVO;
import cc.xiaoxu.cloud.core.bean.dto.PageDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "AI 知识库服务")
@RequestMapping("/knowledge")
public class KnowledgeController {

    private final ALiYunService aLiYunService;
    private final KnowledgeService knowledgeService;
    private final KnowledgeSectionService knowledgeSectionService;
    private final TenantService tenantService;

    @PostMapping("/list/{tenant}")
    @Operation(summary = "知识库查询 - 列表")
    public List<KnowledgeExpandVO> list(@PathVariable("tenant") String tenant) {

        tenantService.checkTenantThrow(tenant);
        return knowledgeService.lists(tenant);
    }

    @PostMapping("/add_ali_files/{tenant}")
    @Operation(summary = "新增知识库 - 文件批量")
    public void addALiFiles(@RequestPart(name = "file") MultipartFile[] files, @PathVariable("tenant") String tenant) throws InterruptedException {

        tenantService.checkTenantThrow(tenant);
        for (MultipartFile file : files) {
            String fileId = aLiYunService.uploadFile(file);
            knowledgeService.addALiFile(file.getOriginalFilename(), fileId, tenant);
            Thread.sleep(6000);
        }
    }

    @PostMapping("/add_ali_file/{tenant}")
    @Operation(summary = "新增知识库 - 文件")
    public void addALiFile(@RequestPart(name = "file") MultipartFile file, @PathVariable("tenant") String tenant) {

        tenantService.checkTenantThrow(tenant);
        String fileId = aLiYunService.uploadFile(file);
        knowledgeService.addALiFile(file.getOriginalFilename(), fileId, tenant);
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