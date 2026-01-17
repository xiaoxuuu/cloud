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

    public List<? extends PointSimpleVO> search(PointSearchDTO dto) {

        // 小于此数值的缩放，转为区县
        double pointScaleToDistrict = Double.parseDouble(constantService.getValue("point_scale_to_district"));
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
            // 缩放达到一定程度 或 点位数量小于一定数量 按正常返回数据
            pointStream = pointFilterList.stream().map(this::tran);
        } else {
            // 区县
            Map<Integer, Area> areaMap = dataCacheManager.getAreaMap();
            pointStream = pointFilterList.stream()
                    // 转换区县数据
                    .map(k -> {
                        Area area = areaMap.get(k.getAddressCode());
                        if (null == area) {
                            log.error("{}", k.getAddressCode());
                            return null;
                        }
                        return buildDistrict(area, k);
                    })
                    .filter(Objects::nonNull)
                    //分组，只保留一条
                    .collect(Collectors.groupingBy(PointSimpleVO::getPointShortName))
                    .values()
                    .stream()
                    .map(k -> {
                        PointSimpleVO pointSimpleVO = k.getFirst();
                        String newName = pointSimpleVO.getPointShortName() + ": " + k.size() + " 个";
                        pointSimpleVO.setPointShortName(newName);
                        pointSimpleVO.setPointShortName(newName);
                        return pointSimpleVO;
                    });
        }
        return pointStream
                .peek(k -> addDistance(dto, k))
                .sorted(Comparator.comparingDouble(PointSimpleVO::getDistance))
                .limit(pointMaxNum)
                .toList();
    }

    private PointSimpleVO buildDistrict(Area area, PointFullVO pointTemp) {

        PointSimpleVO vo = new PointSimpleVO();
        vo.setCode(area.getCode());
        vo.setPointShortName(area.getName());
        vo.setPointType(PointTypeEnum.DISTRICT);
        vo.setLongitude(pointTemp.getLongitude());
        vo.setLatitude(pointTemp.getLatitude());
        return vo;
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