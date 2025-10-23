package cc.xiaoxu.cloud.my.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Tag(name = "点位版本", description = "点位版本控制器")
@RequestMapping("/point_version")
public class PointVersionController {

    @Operation(summary = "获取检测版本", description = "获取检测版本")
    @GetMapping("/get")
    public @ResponseBody
    String getPointVersion() {

        return "0.0.9";
    }
}