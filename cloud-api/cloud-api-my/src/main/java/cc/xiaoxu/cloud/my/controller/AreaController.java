package cc.xiaoxu.cloud.my.controller;

import cc.xiaoxu.cloud.bean.vo.AreaTreeVO;
import cc.xiaoxu.cloud.my.service.AreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "地区", description = "地区控制器")
@RequestMapping("/area")
@AllArgsConstructor
public class AreaController {

    private final AreaService areaService;

    @Operation(summary = "地区树", description = "获取地区树")
    @PostMapping("/tree")
    public @ResponseBody List<AreaTreeVO> getTree() {

        return areaService.tree();
    }
}