package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.service.ALiYunService;
import cc.xiaoxu.cloud.ai.service.KnowledgeSectionService;
import cc.xiaoxu.cloud.ai.service.KnowledgeService;
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

    @PostMapping("/list")
    @Operation(summary = "知识库查询 - 列表")
    public List<KnowledgeExpandVO> list() {

        return knowledgeService.lists();
    }

    @PostMapping("/add_ali_files")
    @Operation(summary = "新增知识库 - 文件批量")
    public void addALiFiles(@RequestPart(name = "file") MultipartFile[] files) throws InterruptedException {

        for (MultipartFile file : files) {
            String fileId = aLiYunService.uploadFile(file);
            knowledgeService.addALiFile(file.getOriginalFilename(), fileId);
            Thread.sleep(6000);
        }
    }

    @PostMapping("/add_ali_file")
    @Operation(summary = "新增知识库 - 文件")
    public void addALiFile(@RequestPart(name = "file") MultipartFile file) {

        String fileId = aLiYunService.uploadFile(file);
        knowledgeService.addALiFile(file.getOriginalFilename(), fileId);
    }

    @PostMapping("/add_table")
    @Operation(summary = "新增知识库 - 数据表")
    public void addTable(@Valid @RequestBody KnowledgeAddTableDTO dto) {

        knowledgeService.addTable(dto);
    }

    @PostMapping("/add_custom")
    @Operation(summary = "新增知识库 - 自定义数据")
    public void addCustom(@Valid @RequestBody KnowledgeAddCustomDTO dto) {

        knowledgeService.addCustom(dto);
    }

    @PostMapping("/edit_state")
    @Operation(summary = "删除知识库")
    public void editState(@Valid @RequestBody KnowledgeEditStateDTO dto) {

        knowledgeService.editState(dto);
        knowledgeSectionService.editState(dto);
    }

    @PostMapping("/page")
    @Operation(summary = "知识库 - 分页")
    public Page<KnowledgeExpandVO> page(@Valid @RequestBody PageDTO dto) {

        return knowledgeService.pages(dto);
    }
}