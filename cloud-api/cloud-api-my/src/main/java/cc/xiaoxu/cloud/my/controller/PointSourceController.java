package cc.xiaoxu.cloud.my.controller;

import cc.xiaoxu.cloud.bean.dto.PointSourceAddOrEditDTO;
import cc.xiaoxu.cloud.bean.vo.PointSourceVO;
import cc.xiaoxu.cloud.my.manager.CacheManager;
import cc.xiaoxu.cloud.my.service.PointSourceService;
import cc.xiaoxu.cloud.my.task.scheduled.CacheScheduled;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Tag(name = "点位来源", description = "点位来源控制器")
@RequestMapping("/point_source")
public class PointSourceController {

    @Resource
    private PointSourceService pointSourceService;

    @Resource
    private CacheScheduled cacheScheduled;

    @Resource
    private CacheManager cacheManager;

    @Operation(summary = "全部来源列表", description = "全部来源列表")
    @PostMapping("/list")
    public @ResponseBody
    List<PointSourceVO> pointSourceList() {

        return cacheManager.getPointSourceList();
    }

    @Operation(summary = "新增或编辑来源", description = "新增或编辑来源")
    @PostMapping("/add_or_edit")
    public @ResponseBody
    void addOrEditSource(@RequestBody PointSourceAddOrEditDTO dto) {

        pointSourceService.addOrEdit(dto);
        cacheScheduled.refreshData();
    }
}