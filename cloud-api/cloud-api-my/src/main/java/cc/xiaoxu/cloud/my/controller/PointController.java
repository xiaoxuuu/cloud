package cc.xiaoxu.cloud.my.controller;

import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.bean.dto.PointAddDTO;
import cc.xiaoxu.cloud.bean.dto.PointEditDTO;
import cc.xiaoxu.cloud.bean.dto.PointSearchDTO;
import cc.xiaoxu.cloud.bean.enums.PointTypeEnum;
import cc.xiaoxu.cloud.bean.vo.PointFullVO;
import cc.xiaoxu.cloud.bean.vo.PointSimpleVO;
import cc.xiaoxu.cloud.bean.vo.PointTypeVO;
import cc.xiaoxu.cloud.core.controller.CloudController;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.my.manager.PointManager;
import cc.xiaoxu.cloud.my.service.PointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@Tag(name = "点位", description = "点位控制器")
@RequestMapping("/point")
public class PointController {

    @Value("${app.config.auth-code}")
    private String authCode;

    @Resource
    private PointService pointService;

    @Resource
    private PointManager pointManager;

    @Operation(summary = "新增", description = "新增地点")
    @PostMapping("/add/{code}")
    public @ResponseBody
    void add(@PathVariable("code") String code, @RequestBody PointAddDTO dto) {

        if (!code.equals(CloudController.getCheckCode() + authCode)) {
            throw new CustomException("无权限");
        }
        pointService.add(dto);
    }

    @Operation(summary = "编辑", description = "编辑地点")
    @PostMapping("/edit/{code}")
    public @ResponseBody
    void edit(@PathVariable("code") String code, @RequestBody PointEditDTO dto) {

        if (!code.equals(CloudController.getCheckCode() + authCode)) {
            throw new CustomException("无权限");
        }
        pointService.edit(dto);
    }

    @Operation(summary = "列表", description = "获取地点列表")
    @PostMapping("/list")
    public @ResponseBody
    List<? extends PointSimpleVO> list(@RequestBody PointSearchDTO dto) {

        if (null == dto.getScale() || null == dto.getCenterLatitude() || null == dto.getCenterLongitude()) {
            throw new CustomException("参数错误");
        }
        return pointManager.lists(dto);
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