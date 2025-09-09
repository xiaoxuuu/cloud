package cc.xiaoxu.cloud.my.controller;

import cc.xiaoxu.cloud.bean.dto.PointMapSearchDTO;
import cc.xiaoxu.cloud.bean.dto.amap.AmapInputTipsRequestDTO;
import cc.xiaoxu.cloud.bean.dto.amap.AmapInputTipsResponseDTO;
import cc.xiaoxu.cloud.bean.dto.amap.AmapPoiSearchRequestDTO;
import cc.xiaoxu.cloud.bean.dto.amap.AmapPoiSearchResponseDTO;
import cc.xiaoxu.cloud.bean.enums.SearchMapTypeEnum;
import cc.xiaoxu.cloud.bean.vo.PointMapSearchAddressVO;
import cc.xiaoxu.cloud.bean.vo.PointMapSearchVO;
import cc.xiaoxu.cloud.core.controller.CloudController;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.my.manager.AmapManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "高德地图", description = "高德地图接口器")
@RequestMapping("/amap")
public class AmapController {

    @Value("${app.config.auth-code}")
    private String authCode;

    @Resource
    private AmapManager amapManager;

    @Operation(summary = "地址搜索", description = "优先调用 POI 接口，数据不足 10 条自动降级输入提示")
    @PostMapping("/search/{code}")
    public @ResponseBody Map<String, List<PointMapSearchVO>> search(@PathVariable("code") String code, @RequestBody PointMapSearchDTO dto) {

        if (!code.equals(CloudController.getCheckCode() + authCode)) {
            throw new CustomException("无权限");
        }

        if (StringUtils.isEmpty(dto.getKeywords())) {
            return Map.of();
        }

        Map<String, List<PointMapSearchVO>> map = new LinkedHashMap<>();

        // TODO 查询已有数据

        // 调用高德 POI 接口
        AmapPoiSearchRequestDTO amapDTO = new AmapPoiSearchRequestDTO();
        amapDTO.setShowFields("business");
        amapDTO.setExtensions("all");
        amapDTO.setKeywords(dto.getKeywords());
        amapDTO.setRegion(dto.getCity());
        AmapPoiSearchResponseDTO amapPoiSearchResponseDTO = amapManager.searchPoi(amapDTO);
        if (CollectionUtils.isNotEmpty(amapPoiSearchResponseDTO.getPois())) {
            map.put(SearchMapTypeEnum.AMAP_POI.getIntroduction(), amapPoiToPointMapSearchVO(amapPoiSearchResponseDTO.getPois()));
        }

        if (amapPoiSearchResponseDTO.getPois().size() <= 10) {
            // 调用高德输入提示接口
            AmapInputTipsRequestDTO inputDTO = new AmapInputTipsRequestDTO();
            inputDTO.setLocation(dto.getCity());
            inputDTO.setKeywords(dto.getKeywords());
            AmapInputTipsResponseDTO amapInputTipsResponseDTO = amapManager.inputTips(inputDTO);
            if (CollectionUtils.isNotEmpty(amapInputTipsResponseDTO.getTips())) {
                map.put(SearchMapTypeEnum.AMAP_INPUT.getIntroduction(), amapInputToPointMapSearchVO(amapInputTipsResponseDTO.getTips()));
            }
        }

        return map;
    }

    private List<PointMapSearchVO> amapPoiToPointMapSearchVO(List<AmapPoiSearchResponseDTO.AmapPoiDTO> poiDTOList) {

        List<PointMapSearchVO> searchVOList = new ArrayList<>();

        for (AmapPoiSearchResponseDTO.AmapPoiDTO poi : poiDTOList) {

            PointMapSearchVO searchVO = new PointMapSearchVO();
            PointMapSearchAddressVO addressVO = new PointMapSearchAddressVO();
            searchVO.setAddressVO(addressVO);

            // 基础字段
            searchVO.setMapId(poi.getId());
            searchVO.setName(poi.getName());
            searchVO.setType(poi.getType());

            // 地址信息
            addressVO.setAddress(poi.getAddress());
            addressVO.setLocation(poi.getLocation());
            addressVO.setProvince(poi.getPname());
            addressVO.setCity(poi.getCityname());
            addressVO.setDistrict(poi.getAdname());
            addressVO.setDistrictCode(poi.getAdcode());
            searchVOList.add(searchVO);
        }
        return searchVOList;
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

    private List<PointMapSearchVO> amapInputToPointMapSearchVO(List<AmapInputTipsResponseDTO.AmapInputTipDTO> tips) {

        List<PointMapSearchVO> searchVOList = new ArrayList<>();

        for (AmapInputTipsResponseDTO.AmapInputTipDTO tip : tips) {

            PointMapSearchVO poi = new PointMapSearchVO();
            PointMapSearchAddressVO addressVO = new PointMapSearchAddressVO();
            poi.setAddressVO(addressVO);

            // 基础字段
            poi.setMapId(tip.getId());
            poi.setName(tip.getName());

            // 地址信息
            addressVO.setAddress(tip.getAddress());
            addressVO.setLocation(tip.getLocation());
            addressVO.setDistrictCode(tip.getAdcode());

            // 拆分 district 字段为 pname、cityname、adname
            if (tip.getDistrict() != null && !tip.getDistrict().isEmpty()) {
                String[] districts = tip.getDistrict().split("省|市|区|县|自治区|特别行政区");

                if (districts.length >= 1 && !districts[0].isEmpty()) {
                    addressVO.setProvince(districts[0] + (tip.getDistrict().contains("省") ? "省" :
                            tip.getDistrict().contains("自治区") ? "自治区" :
                                    tip.getDistrict().contains("特别行政区") ? "特别行政区" : ""));
                }

                if (districts.length >= 2 && !districts[1].isEmpty()) {
                    addressVO.setCity(districts[1] + "市");
                }

                if (districts.length >= 3 && !districts[2].isEmpty()) {
                    addressVO.setDistrict(districts[2] + (tip.getDistrict().contains("区") ? "区" :
                            tip.getDistrict().contains("县") ? "县" : ""));
                }

                // 如果拆分结果不足，尝试更智能的解析
                if (addressVO.getProvince() == null && addressVO.getCity() == null && addressVO.getDistrict() == null) {
                    // 简单情况：直接使用原始 district 作为 adname
                    addressVO.setDistrict(tip.getDistrict());
                }
            }

            searchVOList.add(poi);
        }
        return searchVOList;
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