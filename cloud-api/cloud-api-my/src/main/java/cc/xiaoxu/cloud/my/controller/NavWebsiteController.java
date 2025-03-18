package cc.xiaoxu.cloud.my.controller;

import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.bean.dto.NavWebsitePageDTO;
import cc.xiaoxu.cloud.bean.vo.NavWebsiteAddVO;
import cc.xiaoxu.cloud.bean.vo.NavWebsiteSearchVO;
import cc.xiaoxu.cloud.bean.vo.NavWebsiteShowVO;
import cc.xiaoxu.cloud.core.controller.CloudController;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.my.service.NavWebsiteService;
import cc.xiaoxu.cloud.my.task.scheduled.WebsiteCheckScheduled;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "导航", description = "导航控制器")
@RestController
@RequestMapping("/nav")
public class NavWebsiteController {

    @Value("${app.config.auth-code}")
    private String authCode;

    @Resource
    private NavWebsiteService navWebsiteService;

    @Resource
    private WebsiteCheckScheduled websiteCheckScheduled;

    @Operation(summary = "新增", description = "新增数据")
    @PostMapping("/add/{code}")
    public @ResponseBody void add(@PathVariable("code") String code, @RequestBody NavWebsiteAddVO vo) {

        if (!code.equals(CloudController.getCheckCode() + authCode)) {
            throw new CustomException("无权限");
        }
        navWebsiteService.add(vo);
    }

    @Operation(summary = "搜索", description = "获取列表")
    @PostMapping("/search")
    public @ResponseBody
    List<NavWebsiteShowVO> search(@RequestBody NavWebsiteSearchVO vo) {

        return navWebsiteService.search(vo);
    }

    @Operation(summary = "分页", description = "分页查询")
    @PostMapping("/page")
    public @ResponseBody
    Page<NavWebsiteShowVO> page(@RequestBody NavWebsitePageDTO dto) {

        return navWebsiteService.pages(dto);
    }

    @Operation(summary = "添加访问次数", description = "添加访问次数")
    @PostMapping("/add-visit-num")
    public @ResponseBody
    void addVisitNum(@RequestBody IdDTO dto) {

        navWebsiteService.addVisitNum(dto);
    }

    @Operation(summary = "刷新数据", description = "手动刷新缓存数据")
    @PostMapping("/refresh-data")
    public @ResponseBody
    void refreshData() {

        websiteCheckScheduled.refreshData();
    }
}