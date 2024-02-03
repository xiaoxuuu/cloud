package cc.xiaoxu.cloud.my.navigation.controller;

import cc.xiaoxu.cloud.my.navigation.bean.vo.NavWebsiteAddVisitNumVO;
import cc.xiaoxu.cloud.my.navigation.bean.vo.NavWebsiteSearchVO;
import cc.xiaoxu.cloud.my.navigation.bean.vo.NavWebsiteShowVO;
import cc.xiaoxu.cloud.my.navigation.service.NavWebsiteService;
import cc.xiaoxu.cloud.my.navigation.task.WebsiteCheckTask;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "导航", description = "导航控制器")
@RestController
@RequestMapping("/nav")
public class NavWebsiteController {

    @Resource
    private NavWebsiteService navWebsiteService;

    @Resource
    private WebsiteCheckTask websiteCheckTask;

    @Operation(summary = "搜索", description = "获取列表")
    @PostMapping("/search")
    public @ResponseBody
    List<NavWebsiteShowVO> search(@RequestBody NavWebsiteSearchVO vo) {

        return navWebsiteService.search(vo);
    }

    @Operation(summary = "添加访问次数", description = "添加访问次数")
    @PostMapping("/add-visit-num")
    public @ResponseBody
    void addVisitNum(@RequestBody NavWebsiteAddVisitNumVO vo) {

        navWebsiteService.addVisitNum(vo);
    }

    @Operation(summary = "刷新数据", description = "手动刷新缓存数据")
    @PostMapping("/refresh-data")
    public @ResponseBody
    void refreshData() {

        websiteCheckTask.refreshData();
    }
}