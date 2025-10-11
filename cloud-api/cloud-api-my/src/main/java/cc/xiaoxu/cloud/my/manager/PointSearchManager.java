package cc.xiaoxu.cloud.my.manager;

import cc.xiaoxu.cloud.bean.dto.PointSearchDTO;
import cc.xiaoxu.cloud.bean.enums.PointTypeEnum;
import cc.xiaoxu.cloud.bean.vo.PointSimpleVO;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.core.utils.enums.EnumUtils;
import cc.xiaoxu.cloud.my.entity.*;
import cc.xiaoxu.cloud.my.service.ConstantService;
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

        Stream<PointTemp> pointFilterStream = pointManager.getPointList()
                .stream()
                // 模糊匹配
                .filter(k -> pointNameLike(dto, k))
                // 点位类型
                .filter(k -> pointType(dto, k))
                // 作者访问过
                .filter(k -> authorVisit(dto, k));
        Stream<PointSimpleVO> pointStream;
        if (dto.getScale() > scale) {
            // 正常返回数据
            pointStream = pointFilterStream.map(this::tran);
        } else {
            // 区县
            // TODO 标记自身点位数量
            Map<String, Area> areaMap = pointManager.getAreaMap();
            pointStream = pointFilterStream
                    // 转换区县数据
                    .map(k -> {
                        Area area = areaMap.get(k.getAddressCode());
                        return buildDistrict(area);
                    })
                    //分组，只保留一条
                    .collect(Collectors.groupingBy(PointSimpleVO::getPointName))
                    .values()
                    .stream()
                    .map(k -> {
                        PointSimpleVO pointSimpleVO = k.getFirst();
                        String newName = pointSimpleVO.getPointName() + " " + k.size() + " 个";
                        pointSimpleVO.setPointName(newName);
                        pointSimpleVO.setPointShortName(newName);
                        return pointSimpleVO;
                    });
        }
        return pointStream
//                    // 计算距离中心点位置
                .peek(k -> addDistance(dto, k))
                .sorted(Comparator.comparingDouble(PointSimpleVO::getDistance))
//                    // 限制返回数据
                .limit(countConstant)
                .toList();
    }

    @NotNull
    private PointSimpleVO buildDistrict(Area area) {

        PointSimpleVO vo = new PointSimpleVO();
        vo.setPointName(area.getName());
        vo.setPointShortName(area.getName());
        vo.setPointType(PointTypeEnum.DISTRICT);
        String[] split = area.getLocation().split(",");
        vo.setLongitude(split[0]);
        vo.setLatitude(split[1]);
        return vo;
    }

    @NotNull
    private PointSimpleVO tran(Point k) {

        PointSimpleVO vo = new PointSimpleVO();
        BeanUtils.populate(k, vo);
        vo.setPointName(k.getPointShortName());
        return vo;
    }

    private boolean authorVisit(PointSearchDTO dto, Point k) {
        if (null == dto.getVisit()) {
            return true;
        }
        if (dto.getVisit()) {
            if (null == k.getVisitedTimes()) {
                return false;
            }
            return k.getVisitedTimes() > 1;
        } else {
            return true;
        }
    }

    private boolean pointType(PointSearchDTO dto, Point k) {

        Set<PointTypeEnum> pointTypeSet = getPointTypeFilter(dto);
        if (CollectionUtils.isEmpty(pointTypeSet)) {
            return true;
        }
        return pointTypeSet.contains(k.getPointType());
    }

    private boolean pointNameLike(PointSearchDTO dto, Point k) {

        Set<Integer> pointSourceSet = pointManager.getPointSourceList().stream()
                .filter(v -> {
                    if (StringUtils.isNotBlank(dto.getPointName())) {
                        return SearchUtils.containsValue(v.getTitle(), dto.getPointName()) ||
                                SearchUtils.containsValue(v.getContent(), dto.getPointName());
                    }
                    return true;
                })
                .map(PointSource::getPointId)
                .collect(Collectors.toSet());
        return pointSourceSet.contains(k.getId()) ||
                SearchUtils.containsValue(k.getPointFullName(), dto.getPointName()) ||
                SearchUtils.containsValue(k.getPointShortName(), dto.getPointName()) ||
                SearchUtils.containsValue(k.getAddress(), dto.getPointName()) ||
                SearchUtils.containsValue(k.getAddressCode(), dto.getPointName()) ||
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
        double distance = calculateDistance(
                Double.parseDouble(k.getLatitude()),
                Double.parseDouble(k.getLongitude()),
                dto.getCenterLatitude(),
                dto.getCenterLongitude()
        );
        k.setDistance(distance);
    }

    /**
     * 计算两个经纬度点之间的距离（单位：公里）
     * 使用 Haversine 公式
     *
     * @param lat1 点1纬度
     * @param lon1 点1经度
     * @param lat2 点2纬度
     * @param lon2 点2经度
     * @return 距离（公里）
     */
    private static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double EARTH_RADIUS = 6371; // 地球半径（公里）

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}