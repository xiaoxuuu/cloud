package cc.xiaoxu.cloud.my.controller;

import cc.xiaoxu.cloud.my.entity.Constant;
import cc.xiaoxu.cloud.my.service.ConstantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "常量", description = "网页信息控制器")
@RequestMapping("/constant")
public class ConstantController {

    private final ConstantService constantService;

    @Operation(summary = "读取一个", description = "网站首页信息")
    @GetMapping("/get/{name}")
    public @ResponseBody
    List<String> index(@PathVariable("name") String name) {

        return constantService.lambdaQuery()
                .eq(Constant::getName, name)
                .list()
                .stream()
                .map(Constant::getValue)
                .toList();
    }
}