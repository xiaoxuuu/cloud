package cc.xiaoxu.cloud.my.controller;

import cc.xiaoxu.cloud.bean.dto.amap.AmapInputTipsRequestDTO;
import cc.xiaoxu.cloud.bean.dto.amap.AmapInputTipsResponseDTO;
import cc.xiaoxu.cloud.bean.dto.amap.AmapPoiSearchRequestDTO;
import cc.xiaoxu.cloud.bean.dto.amap.AmapPoiSearchResponseDTO;
import cc.xiaoxu.cloud.core.controller.CloudController;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.my.manager.AmapManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "高德地图", description = "高德地图接口器")
@RequestMapping("/amap")
public class AmapController {

    @Value("${app.config.auth-code}")
    private String authCode;

    @Resource
    private AmapManager amapManager;

    @Operation(summary = "输入提示", description = "输入提示，用于 POI 接口无法搜索的数据降级")
    @PostMapping("/input_tips/{code}")
    public @ResponseBody AmapInputTipsResponseDTO inputTips(@PathVariable("code") String code, @RequestBody AmapInputTipsRequestDTO dto) {

        if (!code.equals(CloudController.getCheckCode() + authCode)) {
            throw new CustomException("无权限");
        }
        return amapManager.inputTips(dto);
    }

    @Operation(summary = "poi 搜索", description = "poi 搜索")
    @PostMapping("/search_poi/{code}")
    public @ResponseBody AmapPoiSearchResponseDTO searchPoi(@PathVariable("code") String code, @RequestBody AmapPoiSearchRequestDTO dto) {

        if (!code.equals(CloudController.getCheckCode() + authCode)) {
            throw new CustomException("无权限");
        }
        dto.setShowFields("business");
        dto.setExtensions("all");
        return amapManager.searchPoi(dto);
    }
}