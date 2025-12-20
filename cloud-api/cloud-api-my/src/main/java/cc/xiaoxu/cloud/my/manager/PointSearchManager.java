package cc.xiaoxu.cloud.my.manager;

import cc.xiaoxu.cloud.bean.dto.PointSearchDTO;
import cc.xiaoxu.cloud.bean.enums.OperatingStatusEnum;
import cc.xiaoxu.cloud.bean.enums.PointTypeEnum;
import cc.xiaoxu.cloud.bean.vo.PointSimpleVO;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.core.utils.enums.EnumUtils;
import cc.xiaoxu.cloud.core.utils.math.MathUtils;
import cc.xiaoxu.cloud.my.entity.*;
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
    private final PointManager pointManager;

    public List<? extends PointSimpleVO> search(PointSearchDTO dto) {

        Constant pointScale = constantService.lambdaQuery()
                .eq(Constant::getName, "point_scale")
                .one();
        double scale = Double.parseDouble(pointScale.getValue());
        Constant count = constantService.lambdaQuery()
                .eq(Constant::getName, "point_count_per")
                .one();
        int countConstant = Integer.parseInt(count.getValue());

        // 营业状态
        Set<OperatingStatusEnum> operatingStatusSet = CollectionUtils.isNotEmpty(dto.getOperatingStatusSet())
                ? dto.getOperatingStatusSet()
                : Set.of(OperatingStatusEnum.OPEN, OperatingStatusEnum.ING);

        List<PointTemp> pointFilterList = pointManager.getPointList()
                .stream()
                // 模糊匹配
                .filter(k -> pointNameLike(dto, k))
                // 点位类型
                .filter(k -> pointType(dto, k))
                // 标签
                .filter(k -> tag(dto, k))
                // 营业状态
                .filter(k -> operatingStatusSet.contains(k.getOperatingStatus()))
                .toList();
        Stream<? extends PointSimpleVO> pointStream;
        if (Double.parseDouble(dto.getScale()) > scale || pointFilterList.size() < countConstant) {
            // 缩放达到一定程度 或 点位数量小于一定数量 按正常返回数据
            pointStream = pointFilterList.stream().map(this::tran);
        } else {
            // 区县
            Map<Integer, Area> areaMap = pointManager.getAreaMap();
            pointStream = pointFilterList.stream()
                    // 转换区县数据
                    .map(k -> {
                        Area area = areaMap.get(k.getAddressCode());
                        if (null == area) {
                            log.error("{}", k.getAddressCode());
                        }
                        return buildDistrict(area, k);
                    })
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
                .limit(countConstant)
                .toList();
    }

    private boolean tag(PointSearchDTO dto, PointTemp k) {
        if (CollectionUtils.isEmpty(dto.getTagIdList())) {
            return true;
        }
        if (CollectionUtils.isEmpty(k.getTagIdSet())) {
            return false;
        }
        for (String id : dto.getTagIdList()) {
            if (k.getTagIdSet().contains(id)) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    private PointSimpleVO buildDistrict(Area area, PointTemp pointTemp) {

        PointSimpleVO vo = new PointSimpleVO();
        vo.setCode(area.getCode());
        vo.setPointShortName(area.getName());
        vo.setPointType(PointTypeEnum.DISTRICT);
        vo.setLongitude(pointTemp.getLongitude());
        vo.setLatitude(pointTemp.getLatitude());
        return vo;
    }

    @NotNull
    private PointSimpleVO tran(Point k) {

        PointSimpleVO vo = new PointSimpleVO();
        BeanUtils.populate(k, vo);
        vo.setPointShortName(k.getPointShortName());
        return vo;
    }

    private boolean pointType(PointSearchDTO dto, Point k) {

        Set<PointTypeEnum> pointTypeSet = getPointTypeFilter(dto);
        if (CollectionUtils.isEmpty(pointTypeSet)) {
            return true;
        }
        return pointTypeSet.contains(k.getPointType());
    }

    private boolean pointNameLike(PointSearchDTO dto, Point k) {

        if (StringUtils.isBlank(dto.getPointName())) {
            return true;
        }
        Set<Integer> pointSourceSet = pointManager.getPointSourceList().stream()
                .filter(v -> {
                    if (StringUtils.isNotBlank(dto.getPointName())) {
                        return SearchUtils.containsValue(v.getTitle(), dto.getPointName()) ||
                                SearchUtils.containsValue(v.getContent(), dto.getPointName());
                    }
                    return true;
                })
                .map(PointSource::getId)
                .collect(Collectors.toSet());
        return pointSourceSet.contains(k.getId()) ||
                SearchUtils.containsValue(k.getPointFullName(), dto.getPointName()) ||
                SearchUtils.containsValue(k.getPointShortName(), dto.getPointName()) ||
                SearchUtils.containsValue(k.getAddress(), dto.getPointName()) ||
                SearchUtils.containsValue(k.getDescribe(), dto.getPointName());
    }

    @NotNull
    private static Set<PointTypeEnum> getPointTypeFilter(PointSearchDTO dto) {
        Set<PointTypeEnum> pointTypeSet;
        if (CollectionUtils.isNotEmpty(dto.getPointType())) {
            pointTypeSet = dto.getPointType().stream()
                    .map(k -> EnumUtils.getByClass(dto.getPointType(), PointTypeEnum.class))
                    .collect(Collectors.toSet());
        } else {
            pointTypeSet = new HashSet<>();
        }
        return pointTypeSet;
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