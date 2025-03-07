package cc.xiaoxu.cloud.my.controller;

import cc.xiaoxu.cloud.bean.enums.PointTypeEnum;
import cc.xiaoxu.cloud.bean.vo.PointTypeVO;
import cc.xiaoxu.cloud.my.dao.FoodMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "点位", description = "点位控制器")
@RequestMapping("/point")
public class PointController {

    private final FoodMapper commonMapper;

    @Operation(summary = "获取美食", description = "美食")
    @GetMapping("/get")
    public @ResponseBody
    List index() {

        return commonMapper.getFood();
    }

    @Operation(summary = "获取地点类型", description = "美食")
    @GetMapping("/get_type")
    public @ResponseBody
    List<PointTypeVO> getType() {

        return Arrays.stream(PointTypeEnum.values()).map(PointTypeVO::new).toList();
    }
}