package cc.xiaoxu.cloud.my.controller;

import cc.xiaoxu.cloud.bean.dto.IdsDTO;
import cc.xiaoxu.cloud.bean.dto.SignLanguageWordSearchDTO;
import cc.xiaoxu.cloud.bean.vo.SignLanguageWordVO;
import cc.xiaoxu.cloud.my.service.SignLanguageWordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "手语词汇", description = "网页信息控制器")
@RequestMapping("/sign_language_word")
public class SignLanguageWordController {

    private final SignLanguageWordService signLanguageWordService;

    @Operation(summary = "列表", description = "查询列表")
    @PostMapping("/list")
    public @ResponseBody List<SignLanguageWordVO> list(@RequestBody SignLanguageWordSearchDTO dto) {

        return signLanguageWordService.lists(dto);
    }

    @Operation(summary = "关联列表", description = "查询关联列表")
    @PostMapping("/list_rela")
    public @ResponseBody List<SignLanguageWordVO> listRela(@RequestBody IdsDTO dto) {

        return signLanguageWordService.lists(dto);
    }
}