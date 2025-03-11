package cc.xiaoxu.cloud.my.controller;

import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.bean.dto.PointSearchDTO;
import cc.xiaoxu.cloud.bean.enums.PointTypeEnum;
import cc.xiaoxu.cloud.bean.vo.PointFullVO;
import cc.xiaoxu.cloud.bean.vo.PointSimpleVO;
import cc.xiaoxu.cloud.bean.vo.PointTypeVO;
import cc.xiaoxu.cloud.my.service.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "点位", description = "点位控制器")
@RequestMapping("/point")
public class PointController {

    private final PointService pointService;

    @Operation(summary = "列表", description = "获取地点列表")
    @PostMapping("/list")
    public @ResponseBody
    List<? extends PointSimpleVO> list(@RequestBody PointSearchDTO dto) {

        return pointService.lists(dto);
    }

    @Operation(summary = "详情", description = "获取地点列表")
    @PostMapping("/get")
    public @ResponseBody
    PointFullVO get(@RequestBody IdDTO dto) {

        return pointService.get(dto);
    }

    @Operation(summary = "地点类型", description = "获取地点类型列表，用于条件查询")
    @PostMapping("/get_type")
    public @ResponseBody
    List<PointTypeVO> getType() {

        return Arrays.stream(PointTypeEnum.values()).map(PointTypeVO::new).toList();
    }
}