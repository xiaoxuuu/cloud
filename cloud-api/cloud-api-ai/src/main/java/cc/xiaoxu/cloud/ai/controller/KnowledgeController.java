package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.service.ALiYunService;
import cc.xiaoxu.cloud.ai.service.KnowledgeService;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeAddTableDTO;
import cc.xiaoxu.cloud.bean.ai.dto.SplitTxtDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "知识库服务")
@RequestMapping("/knowledge")
public class KnowledgeController {

    private final ALiYunService aLiYunService;
    private final KnowledgeService knowledgeService;

    @PostMapping("/addALiFile")
    @Operation(summary = "新增文件")
    public void addALiFile(@RequestPart(name = "file") MultipartFile file) {

        String fileId = aLiYunService.uploadFile(file);
        knowledgeService.addALiFile(file.getOriginalFilename(), fileId);
    }

    // TODO
    @PostMapping("/addTable")
    @Operation(summary = "新增数据表")
    public boolean addTable(@Valid @RequestBody KnowledgeAddTableDTO dto) {
        return false;
    }

    // TODO
    @PostMapping("/addCustom")
    @Operation(summary = "新增自定义数据")
    public boolean addCustom(@Valid @RequestBody SplitTxtDTO dto) {
        return false;
    }
}