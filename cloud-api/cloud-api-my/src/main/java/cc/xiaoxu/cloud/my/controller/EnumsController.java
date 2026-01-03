package cc.xiaoxu.cloud.my.controller;

import cc.xiaoxu.cloud.bean.enums.PointSourceTypeEnum;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Tag(name = "枚举", description = "枚举控制器")
@RequestMapping("/emums")
@AllArgsConstructor
public class EnumsController {

    @Operation(summary = "获取点位来源类型", description = "获取点位来源类型")
    @PostMapping("/get_point_source_type")
    public @ResponseBody Map<String, String> getTree() {

        return Arrays.stream(PointSourceTypeEnum.values()).collect(Collectors.toMap(PointSourceTypeEnum::getCode, PointSourceTypeEnum::getIntroduction));
    }
}