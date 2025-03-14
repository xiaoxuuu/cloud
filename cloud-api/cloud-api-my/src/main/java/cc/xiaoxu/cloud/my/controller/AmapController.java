package cc.xiaoxu.cloud.my.controller;

import cc.xiaoxu.cloud.bean.dto.amap.AmapInputTipsRequestDTO;
import cc.xiaoxu.cloud.bean.dto.amap.AmapInputTipsResponseDTO;
import cc.xiaoxu.cloud.bean.dto.amap.AmapPoiSearchRequestDTO;
import cc.xiaoxu.cloud.bean.dto.amap.AmapPoiSearchResponseDTO;
import cc.xiaoxu.cloud.my.manager.AmapManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Tag(name = "高德地图", description = "高德地图接口器")
@RequestMapping("/amap")
public class AmapController {

    private final AmapManager amapManager;

    @Operation(summary = "输入提示", description = "inputTips")
    @PostMapping("/inputTips")
    public @ResponseBody
    AmapInputTipsResponseDTO inputTips(@RequestBody AmapInputTipsRequestDTO dto) {

        return amapManager.inputTips(dto);
    }

    @Operation(summary = "poi 搜索", description = "poi 搜索")
    @PostMapping("/searchPoi")
    public @ResponseBody
    AmapPoiSearchResponseDTO searchPoi(@RequestBody AmapPoiSearchRequestDTO dto) {

        dto.setShowFields("business");
        dto.setExtensions("all");
        return amapManager.searchPoi(dto);
    }
}