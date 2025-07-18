package cc.xiaoxu.cloud.my.controller;

import cc.xiaoxu.cloud.my.task.AmapTask;
import cc.xiaoxu.cloud.my.task.scheduled.WebsiteCheckScheduled;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@Tag(name = "测试", description = "测试控制器")
@RequestMapping("/test_my")
public class TestMyController {

    private final WebsiteCheckScheduled websiteCheckScheduled;
    private final AmapTask amapTask;

    @Operation(summary = "获取描述", description = "网站首页信息")
    @GetMapping("/get")
    public @ResponseBody
    void index() {

        websiteCheckScheduled.getWebsiteDesc();
    }

    @Operation(summary = "更新高德数据", description = "更新高德数据")
    @GetMapping("/updateAmapData")
    public @ResponseBody
    void updateAmapData() {

        amapTask.updateAmapData();
    }
}