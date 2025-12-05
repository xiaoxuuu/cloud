package cc.xiaoxu.cloud.my.controller;

import cc.xiaoxu.cloud.bean.dto.PointTagAddOrEditDTO;
import cc.xiaoxu.cloud.bean.vo.PointTagVO;
import cc.xiaoxu.cloud.my.manager.PointManager;
import cc.xiaoxu.cloud.my.service.PointTagService;
import cc.xiaoxu.cloud.my.task.scheduled.PointScheduled;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "点位标签", description = "点位标签控制器")
@RequestMapping("/point_tag")
public class PointTagController {

    @Resource
    private PointTagService pointTagService;

    @Resource
    private PointScheduled pointScheduled;

    @Resource
    private PointManager pointManager;

    @Operation(summary = "全部标签列表", description = "全部标签列表")
    @PostMapping("/list_all")
    public @ResponseBody
    List<PointTagVO> pointTagList() {

        return pointManager.getPointTagList();
    }

    @Operation(summary = "标签列表", description = "标签列表")
    @PostMapping("/list")
    public @ResponseBody
    List<PointTagVO> pointTagUsedList() {

        return pointManager.getPointTagList();
    }

    @Operation(summary = "新增或编辑标签", description = "新增或编辑标签")
    @PostMapping("/add_or_edit")
    public @ResponseBody
    void addOrEditTag(@RequestBody PointTagAddOrEditDTO dto) {

        pointTagService.addOrEdit(dto);
        pointScheduled.refreshData();
    }
}