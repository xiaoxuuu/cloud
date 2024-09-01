package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.service.ALiYunService;
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

    @PostMapping("/addFile")
    @Operation(summary = "新增文件")
    public boolean addFile(@RequestPart(name = "file") MultipartFile file) {

        String s = aLiYunService.uploadFile(file);
        log.error(s);
        return false;
    }

    @PostMapping("/addTable")
    @Operation(summary = "新增数据表")
    public boolean addTable(@Valid @RequestBody SplitTxtDTO dto) {
        return false;
    }

    @PostMapping("/addCustom")
    @Operation(summary = "新增自定义数据")
    public boolean addCustom(@Valid @RequestBody SplitTxtDTO dto) {
        return false;
    }
}