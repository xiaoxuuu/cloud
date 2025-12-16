package cc.xiaoxu.cloud.my.controller;

import cc.xiaoxu.cloud.bean.dto.PointSourceAuthorAddOrEditDTO;
import cc.xiaoxu.cloud.bean.vo.PointSourceAuthorVO;
import cc.xiaoxu.cloud.my.manager.PointManager;
import cc.xiaoxu.cloud.my.service.PointSourceAuthorService;
import cc.xiaoxu.cloud.my.task.scheduled.PointScheduled;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "点位来源作者", description = "点位来源作者控制器")
@RequestMapping("/point_source_author")
public class PointSourceAuthorController {

    @Resource
    private PointSourceAuthorService pointSourceAuthorService;

    @Resource
    private PointScheduled pointScheduled;

    @Resource
    private PointManager pointManager;

    @Operation(summary = "全部作者列表", description = "全部作者列表")
    @PostMapping("/list")
    public @ResponseBody
    List<PointSourceAuthorVO> pointSourceAuthorList() {

        return pointManager.getPointSourceAuthorList();
    }

    @Operation(summary = "新增或编辑作者", description = "新增或编辑作者")
    @PostMapping("/add_or_edit")
    public @ResponseBody
    void addOrEditAuthor(@RequestBody PointSourceAuthorAddOrEditDTO dto) {

        pointSourceAuthorService.addOrEdit(dto);
        pointScheduled.refreshData();
    }
}