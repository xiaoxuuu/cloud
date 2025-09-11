package cc.xiaoxu.cloud.my.controller;

import cc.xiaoxu.cloud.bean.dto.PointMapSearchDTO;
import cc.xiaoxu.cloud.bean.dto.PointSearchDTO;
import cc.xiaoxu.cloud.bean.dto.amap.AmapInputTipsRequestDTO;
import cc.xiaoxu.cloud.bean.dto.amap.AmapInputTipsResponseDTO;
import cc.xiaoxu.cloud.bean.dto.amap.AmapPoiSearchRequestDTO;
import cc.xiaoxu.cloud.bean.dto.amap.AmapPoiSearchResponseDTO;
import cc.xiaoxu.cloud.bean.enums.SearchMapTypeEnum;
import cc.xiaoxu.cloud.bean.vo.PointMapSearchVO;
import cc.xiaoxu.cloud.core.controller.CloudController;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.my.entity.PointMap;
import cc.xiaoxu.cloud.my.entity.PointTemp;
import cc.xiaoxu.cloud.my.manager.AmapManager;
import cc.xiaoxu.cloud.my.manager.PointManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
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

    @Resource
    private PointManager pointManager;

    @Operation(summary = "地点搜索", description = "查询顺序：已有数据、POI 接口、输入提示")
    @PostMapping("/search/{code}")
    public @ResponseBody List<PointMapSearchVO> search(@PathVariable("code") String code, @RequestBody PointMapSearchDTO dto) {

        if (!code.equals(CloudController.getCheckCode() + authCode)) {
//            throw new CustomException("无权限");
        }

        if (StringUtils.isEmpty(dto.getKeywords())) {
            return List.of();
        }

        // 返回数据
        List<PointMapSearchVO> list = new ArrayList<>();

        // 查询已有数据
        list.addAll(getExistsData(dto));

        // 高德 POI
        list.addAll(getPoiData(dto));

        // 高德输入提示
        if (list.size() <= 20) {
            list.addAll(getTipData(dto));
        }

        return list;
    }

    @NotNull
    private List<PointMapSearchVO> getTipData(PointMapSearchDTO dto) {
        AmapInputTipsRequestDTO inputDTO = new AmapInputTipsRequestDTO();
        inputDTO.setLocation(dto.getCity());
        inputDTO.setKeywords(dto.getKeywords());
        AmapInputTipsResponseDTO amapInputTipsResponseDTO = amapManager.inputTips(inputDTO);
        return amapInputToPointMapSearchVO(amapInputTipsResponseDTO.getTips());
    }

    @NotNull
    private List<PointMapSearchVO> getPoiData(PointMapSearchDTO dto) {
        AmapPoiSearchRequestDTO amapDTO = new AmapPoiSearchRequestDTO();
        amapDTO.setShowFields("business");
        amapDTO.setExtensions("all");
        amapDTO.setKeywords(dto.getKeywords());
        amapDTO.setRegion(dto.getCity());
        AmapPoiSearchResponseDTO amapPoiSearchResponseDTO = amapManager.searchPoi(amapDTO);
        return amapPoiToPointMapSearchVO(amapPoiSearchResponseDTO.getPois());
    }

    @NotNull
    private List<PointMapSearchVO> getExistsData(PointMapSearchDTO dto) {
        PointSearchDTO pointSearchDTO = new PointSearchDTO();
        pointSearchDTO.setPointName(dto.getKeywords());
        return pointManager.getPointList()
                .stream()
                .filter(v -> v.getPointShortName().contains(dto.getKeywords()) || v.getPointFullName().contains(dto.getKeywords()))
                .map(this::pointToPointMapSearchVO)
                .limit(20)
                .toList();
    }

    private PointMapSearchVO pointToPointMapSearchVO(PointTemp pointTemp) {

        PointMap pointMap = pointManager.getPointMapMap().get(pointTemp.getId());
        PointMapSearchVO vo = new PointMapSearchVO();
        vo.setSearchMapType(SearchMapTypeEnum.EXISTS_DATA);
        if (null != pointMap) {
            vo.setMapId(pointMap.getAmapId());
        }
        vo.setName(pointTemp.getPointFullName());
        vo.setLocation(pointTemp.getLongitude() + ","+ pointTemp.getLatitude());
        vo.setProvince(pointTemp.getProvince());
        vo.setCity(pointTemp.getCity());
        vo.setDistrict(pointTemp.getDistrict());
        vo.setDistrictCode(pointTemp.getAddressCode());
        vo.setAddress(pointTemp.getAddress());
        return null;
    }

    private List<PointMapSearchVO> amapPoiToPointMapSearchVO(List<AmapPoiSearchResponseDTO.AmapPoiDTO> poiDTOList) {

        List<PointMapSearchVO> searchVOList = new ArrayList<>();

        for (AmapPoiSearchResponseDTO.AmapPoiDTO poi : poiDTOList) {

            PointMapSearchVO searchVO = new PointMapSearchVO();
            searchVO.setSearchMapType(SearchMapTypeEnum.AMAP_POI);

            // 基础字段
            searchVO.setMapId(poi.getId());
            searchVO.setName(poi.getName());

            // 地址信息
            searchVO.setAddress(poi.getAddress());
            searchVO.setLocation(poi.getLocation());
            searchVO.setProvince(poi.getPname());
            searchVO.setCity(poi.getCityname());
            searchVO.setDistrict(poi.getAdname());
            searchVO.setDistrictCode(poi.getAdcode());
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

            // 基础字段
            poi.setMapId(tip.getId());
            poi.setName(tip.getName());

            // 地址信息
            poi.setAddress(tip.getAddress());
            poi.setLocation(tip.getLocation());
            poi.setDistrictCode(tip.getAdcode());

            // 拆分 district 字段为 pname、cityname、adname
            if (tip.getDistrict() != null && !tip.getDistrict().isEmpty()) {
                String[] districts = tip.getDistrict().split("省|市|区|县|自治区|特别行政区");

                if (districts.length >= 1 && !districts[0].isEmpty()) {
                    poi.setProvince(districts[0] + (tip.getDistrict().contains("省") ? "省" :
                            tip.getDistrict().contains("自治区") ? "自治区" :
                                    tip.getDistrict().contains("特别行政区") ? "特别行政区" : ""));
                }

                if (districts.length >= 2 && !districts[1].isEmpty()) {
                    poi.setCity(districts[1] + "市");
                }

                if (districts.length >= 3 && !districts[2].isEmpty()) {
                    poi.setDistrict(districts[2] + (tip.getDistrict().contains("区") ? "区" :
                            tip.getDistrict().contains("县") ? "县" : ""));
                }

                // 如果拆分结果不足，尝试更智能的解析
                if (poi.getProvince() == null && poi.getCity() == null && poi.getDistrict() == null) {
                    // 简单情况：直接使用原始 district 作为 adname
                    poi.setDistrict(tip.getDistrict());
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