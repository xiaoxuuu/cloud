package cc.xiaoxu.cloud.my.controller;

import cc.xiaoxu.cloud.bean.dto.PointAddOrEditDTO;
import cc.xiaoxu.cloud.bean.dto.PointGetDTO;
import cc.xiaoxu.cloud.bean.dto.PointSearchDTO;
import cc.xiaoxu.cloud.bean.enums.PointTypeEnum;
import cc.xiaoxu.cloud.bean.vo.PointFullVO;
import cc.xiaoxu.cloud.bean.vo.PointShowVO;
import cc.xiaoxu.cloud.bean.vo.PointSimpleVO;
import cc.xiaoxu.cloud.bean.vo.PointTypeVO;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.core.utils.bean.JsonUtils;
import cc.xiaoxu.cloud.my.manager.PointManager;
import cc.xiaoxu.cloud.my.manager.PointSearchManager;
import cc.xiaoxu.cloud.my.service.PointService;
import cc.xiaoxu.cloud.my.task.scheduled.PointScheduled;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "点位", description = "点位控制器")
@RequestMapping("/point")
public class PointController {

    private final PointService pointService;
    private final PointSearchManager pointSearchManager;
    private final PointScheduled pointScheduled;
    private final PointManager pointManager;

    @Operation(summary = "新增或编辑", description = "新增或编辑地点")
    @PostMapping("/add_or_edit")
    public @ResponseBody
    void addOrEdit(@RequestBody PointAddOrEditDTO dto) {

        pointService.addOrEdit(dto);
        pointScheduled.refreshData();
    }

    @Operation(summary = "搜索", description = "搜索地点列表")
    @PostMapping("/search")
    public @ResponseBody
    List<? extends PointSimpleVO> search(@RequestBody PointSearchDTO dto) {

        if (StringUtils.isBlank(dto.getScale()) || StringUtils.isBlank(dto.getCenterLatitude()) || StringUtils.isBlank(dto.getCenterLongitude())) {
            log.error(JsonUtils.toString(dto));
            throw new CustomException("参数错误");
        }
        return pointSearchManager.search(dto);
    }

    @Operation(summary = "获取待处理数量", description = "获取待处理数量")
    @PostMapping("/count_progressing")
    public @ResponseBody
    Integer countProgressing() {

        return pointService.countProgressing();
    }

    @Operation(summary = "详情", description = "获取地点列表")
    @PostMapping("/get")
    public @ResponseBody
    PointShowVO get(@RequestBody PointGetDTO dto) {

        PointShowVO vo = pointManager.getPointShowMapCode().get(dto.getCode());
        if (null == vo) {
            throw new CustomException("未查询到数据");
        }
        return vo;
    }

    @Operation(summary = "详情", description = "获取地点列表")
    @PostMapping("/get_full")
    public @ResponseBody
    PointFullVO getFull(@RequestBody PointGetDTO dto) {

        PointFullVO vo = pointManager.getPointMapCode().get(dto.getCode());
        if (null == vo) {
            throw new CustomException("未查询到数据");
        }
        return vo;
    }

    @Operation(summary = "地点类型", description = "获取地点类型列表，用于条件查询")
    @PostMapping("/get_type")
    public @ResponseBody
    List<PointTypeVO> getType() {

        return Arrays.stream(PointTypeEnum.values()).map(PointTypeVO::new).toList();
    }
}