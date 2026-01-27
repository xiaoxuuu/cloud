package cc.xiaoxu.cloud.my.manager;

import cc.xiaoxu.cloud.bean.dto.PointSearchDTO;
import cc.xiaoxu.cloud.bean.enums.OperatingStatusEnum;
import cc.xiaoxu.cloud.bean.enums.PointTypeEnum;
import cc.xiaoxu.cloud.bean.vo.PointFullVO;
import cc.xiaoxu.cloud.bean.vo.PointSimpleVO;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.core.utils.math.MathUtils;
import cc.xiaoxu.cloud.core.utils.set.SetUtils;
import cc.xiaoxu.cloud.my.entity.Area;
import cc.xiaoxu.cloud.my.service.ConstantService;
import cc.xiaoxu.cloud.my.utils.DistanceUtils;
import cc.xiaoxu.cloud.my.utils.SearchUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class PointSearchManager {

    private final ConstantService constantService;
    private final DataCacheManager dataCacheManager;

    private enum LevelType {
        DISTRICT,
        CITY,
        PROVINCE
    }

    public List<? extends PointSimpleVO> search(PointSearchDTO dto) {

        // 小于此数值的缩放，转为区县、市、省
        double pointScaleToDistrict = Double.parseDouble(constantService.getValue("point_scale_to_district"));
        double pointScaleToCity = Double.parseDouble(constantService.getValue("point_scale_to_city"));
        double pointScaleToProvince = Double.parseDouble(constantService.getValue("point_scale_to_province"));
        // 点位最大展示数量
        int pointMaxNum = Integer.parseInt(constantService.getValue("point_max_num"));

        // 营业状态
        Set<OperatingStatusEnum> operatingStatusSet = CollectionUtils.isNotEmpty(dto.getOperatingStatusSet())
                ? dto.getOperatingStatusSet()
                : Set.of(OperatingStatusEnum.OPEN, OperatingStatusEnum.ING);

        List<PointFullVO> pointFilterList = dataCacheManager.getPointList()
                .stream()
                // 模糊匹配
                .filter(k -> pointNameLike(dto, k))
                // 营业状态
                .filter(k -> operatingStatusSet.contains(k.getOperatingStatus()))
                // 筛选作者
                .filter(k -> CollectionUtils.isEmpty(dto.getAuthorIdSet()) || SetUtils.hasCommonElements(k.getAuthorIdSet(), dto.getAuthorIdSet()))
                .toList();
        Stream<? extends PointSimpleVO> pointStream;

        if (Double.parseDouble(dto.getScale()) > pointScaleToDistrict || pointFilterList.size() < pointMaxNum) {
            pointStream = pointFilterList.stream().map(this::tran);
        } else if (Double.parseDouble(dto.getScale()) > pointScaleToCity) {
            Map<Integer, Area> areaMap = dataCacheManager.getAreaMap();
            pointStream = buildByLevel(pointFilterList, areaMap, LevelType.DISTRICT);
        } else if (Double.parseDouble(dto.getScale()) > pointScaleToProvince) {
            Map<Integer, Area> areaMap = dataCacheManager.getAreaMap();
            pointStream = buildByLevel(pointFilterList, areaMap, LevelType.CITY);
        } else {
            Map<Integer, Area> areaMap = dataCacheManager.getAreaMap();
            pointStream = buildByLevel(pointFilterList, areaMap, LevelType.PROVINCE);
        }
        return pointStream
                .peek(k -> addDistance(dto, k))
                .sorted(Comparator.comparingDouble(PointSimpleVO::getDistance))
                .limit(pointMaxNum)
                .toList();
    }

    private Stream<PointSimpleVO> buildByLevel(List<PointFullVO> pointFilterList, Map<Integer, Area> areaMap, LevelType levelType) {

        return pointFilterList.stream()
                .map(k -> {
                    Area area = areaMap.get(k.getAddressCode());
                    if (null == area) {
                        log.error("{}", k.getAddressCode());
                        return null;
                    }
                    return buildByLevelType(area, k, levelType, areaMap);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(PointSimpleVO::getPointShortName, Collectors.collectingAndThen(
                        Collectors.toList(),
                        list -> list.stream()
                                .sorted(Comparator.comparing(PointSimpleVO::getSort, Comparator.naturalOrder()))
                                .toList()
                )))
                .values()
                .stream()
                .map(k -> {
                    PointSimpleVO pointSimpleVO = k.getFirst();
                    String newName = pointSimpleVO.getPointShortName() + ": " + k.size() + " 个";
                    pointSimpleVO.setPointShortName(newName);
                    return pointSimpleVO;
                });
    }

    private PointSimpleVO buildByLevelType(Area area, PointFullVO pointTemp, LevelType levelType, Map<Integer, Area> areaMap) {

        PointSimpleVO vo = new PointSimpleVO();
        String code = area.getCode();
        String groupCode;
        String name;
        PointTypeEnum pointType;

        switch (levelType) {
            case CITY -> {
                groupCode = code.substring(0, 4) + "00";
                name = getAreaName(areaMap, groupCode, area.getName());
                // TODO 暂时使用区县类型，后续可以增加市、省类型
                pointType = PointTypeEnum.DISTRICT;
            }
            case PROVINCE -> {
                groupCode = code.substring(0, 2) + "0000";
                name = getAreaName(areaMap, groupCode, area.getName());
                // TODO 暂时使用区县类型，后续可以增加市、省类型
                pointType = PointTypeEnum.DISTRICT;
            }
            default -> {
                groupCode = code;
                name = area.getName();
                pointType = PointTypeEnum.DISTRICT;
            }
        }

        vo.setCode(groupCode);
        vo.setPointShortName(name);
        vo.setPointType(pointType);
        vo.setLongitude(pointTemp.getLongitude());
        vo.setLatitude(pointTemp.getLatitude());
        vo.setSort(pointTemp.getDistanceToDistrict());
        return vo;
    }

    private String getAreaName(Map<Integer, Area> areaMap, String code, String defaultName) {

        Area area = areaMap.get(Integer.parseInt(code));
        return area != null ? area.getName() : defaultName;
    }

    @NotNull
    private PointSimpleVO tran(PointFullVO k) {

        PointSimpleVO vo = new PointSimpleVO();
        BeanUtils.populate(k, vo);
        vo.setPointShortName(k.getPointShortName());
        return vo;
    }

    private boolean pointNameLike(PointSearchDTO dto, PointFullVO k) {

        if (StringUtils.isBlank(dto.getPointName())) {
            return true;
        }
        if (",".equals(dto.getPointName())) {
            return false;
        }

        return SearchUtils.containsValue(k.getSearchValue(), dto.getPointName());
    }

    private void addDistance(PointSearchDTO dto, PointSimpleVO k) {

        // 优化，不同省份数据可以移除
        double distance = DistanceUtils.calculateDistanceSimple(
                Double.parseDouble(k.getLatitude()),
                Double.parseDouble(k.getLongitude()),
                Double.parseDouble(dto.getCenterLatitude()),
                Double.parseDouble(dto.getCenterLongitude())
        );
        k.setDistance(MathUtils.toInteger(distance));
        k.setSort(MathUtils.toInteger(distance));
    }
}