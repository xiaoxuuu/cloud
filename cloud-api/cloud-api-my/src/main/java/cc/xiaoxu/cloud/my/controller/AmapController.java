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

import java.util.ArrayList;
import java.util.List;

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
    public @ResponseBody AmapPoiSearchResponseDTO inputTips(@PathVariable("code") String code, @RequestBody AmapInputTipsRequestDTO dto) {

        if (!code.equals(CloudController.getCheckCode() + authCode)) {
            throw new CustomException("无权限");
        }

        AmapInputTipsResponseDTO amapInputTipsResponseDTO = amapManager.inputTips(dto);
        // 将 AmapInputTipsResponseDTO 转为 AmapPoiSearchResponseDTO
        //   AmapInputTipsResponseDTO.tips.district 为 省市区 数据，拆分为 AmapPoiSearchResponseDTO.pois.pname\cityname\adname 三个字段
        return convertInputTipsToPoiSearch(amapInputTipsResponseDTO);
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

    /**
     * 将 AmapInputTipsResponseDTO 转换为 AmapPoiSearchResponseDTO
     * 主要处理 district 字段的拆分：省市区 -> pname/cityname/adname
     */
    private AmapPoiSearchResponseDTO convertInputTipsToPoiSearch(AmapInputTipsResponseDTO inputTips) {
        AmapPoiSearchResponseDTO result = new AmapPoiSearchResponseDTO();

        // 复制基础字段
        result.setStatus(inputTips.getStatus());
        result.setInfo(inputTips.getInfo());
        result.setInfocode(inputTips.getInfocode());
        result.setCount(inputTips.getCount());

        // 转换 tips 为 pois
        if (inputTips.getTips() != null) {
            List<AmapPoiSearchResponseDTO.AmapPoiDTO> pois = new ArrayList<>();

            for (AmapInputTipsResponseDTO.AmapInputTipDTO tip : inputTips.getTips()) {
                AmapPoiSearchResponseDTO.AmapPoiDTO poi = new AmapPoiSearchResponseDTO.AmapPoiDTO();

                // 复制基础字段
                poi.setId(tip.getId());
                poi.setName(tip.getName());
                poi.setAddress(tip.getAddress());
                poi.setLocation(tip.getLocation());
                poi.setTypecode(tip.getTypecode());
                poi.setAdcode(tip.getAdcode());

                // 拆分 district 字段为 pname、cityname、adname
                if (tip.getDistrict() != null && !tip.getDistrict().isEmpty()) {
                    String[] districts = tip.getDistrict().split("省|市|区|县|自治区|特别行政区");

                    if (districts.length >= 1 && !districts[0].isEmpty()) {
                        poi.setPname(districts[0] + (tip.getDistrict().contains("省") ? "省" :
                                tip.getDistrict().contains("自治区") ? "自治区" :
                                        tip.getDistrict().contains("特别行政区") ? "特别行政区" : ""));
                    }

                    if (districts.length >= 2 && !districts[1].isEmpty()) {
                        poi.setCityname(districts[1] + "市");
                    }

                    if (districts.length >= 3 && !districts[2].isEmpty()) {
                        poi.setAdname(districts[2] + (tip.getDistrict().contains("区") ? "区" :
                                tip.getDistrict().contains("县") ? "县" : ""));
                    }

                    // 如果拆分结果不足，尝试更智能的解析
                    if (poi.getPname() == null && poi.getCityname() == null && poi.getAdname() == null) {
                        // 简单情况：直接使用原始 district 作为 adname
                        poi.setAdname(tip.getDistrict());
                    }
                }

                pois.add(poi);
            }

            result.setPois(pois);
        }

        return result;
    }
}