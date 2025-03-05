package cc.xiaoxu.cloud.my.controller;

import cc.xiaoxu.cloud.my.dao.FoodMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "美食", description = "美食控制器")
@RequestMapping("/food")
public class FoodController {

    private final FoodMapper commonMapper;

    @Operation(summary = "获取美食", description = "美食")
    @GetMapping("/get")
    public @ResponseBody
    List index() {

        return commonMapper.getFood();
    }
}