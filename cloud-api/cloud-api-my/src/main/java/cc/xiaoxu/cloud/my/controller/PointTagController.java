package cc.xiaoxu.cloud.my.controller;

import cc.xiaoxu.cloud.bean.dto.PointTagAddOrEditDTO;
import cc.xiaoxu.cloud.my.service.PointTagService;
import cc.xiaoxu.cloud.my.task.scheduled.PointScheduled;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@Tag(name = "点位标签", description = "点位标签控制器")
@RequestMapping("/point_tag")
public class PointTagController {

    @Resource
    private PointTagService pointTagService;

    @Resource
    private PointScheduled pointScheduled;

    @Operation(summary = "新增或编辑标签", description = "新增或编辑地点")
    @PostMapping("/add_or_edit")
    public @ResponseBody
    void addOrEditTag(@RequestBody PointTagAddOrEditDTO dto) {

        pointTagService.addOrEdit(dto);
//        pointScheduled.updatePointTagList();
    }
}