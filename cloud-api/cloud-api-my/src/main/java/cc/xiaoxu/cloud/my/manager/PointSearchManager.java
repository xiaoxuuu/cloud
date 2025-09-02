package cc.xiaoxu.cloud.my.manager;

import cc.xiaoxu.cloud.bean.dto.PointSearchDTO;
import cc.xiaoxu.cloud.bean.enums.PointTypeEnum;
import cc.xiaoxu.cloud.bean.vo.PointSimpleVO;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.core.utils.enums.EnumUtils;
import cc.xiaoxu.cloud.my.entity.Constant;
import cc.xiaoxu.cloud.my.entity.Point;
import cc.xiaoxu.cloud.my.entity.PointSource;
import cc.xiaoxu.cloud.my.service.ConstantService;
import cc.xiaoxu.cloud.my.utils.SearchUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class PointSearchManager {

    private final ConstantService constantService;
    private final PointManager pointManager;

    public List<? extends PointSimpleVO> lists(PointSearchDTO dto) {

        Constant pointRemoveKm = constantService.lambdaQuery()
                .eq(Constant::getName, "point_remove_km")
                .one();
        double removeKm = Double.parseDouble(pointRemoveKm.getValue());
        Constant pointScale = constantService.lambdaQuery()
                .eq(Constant::getName, "point_scale")
                .one();
        double scale = Double.parseDouble(pointScale.getValue());

        // 搜索
        return pointManager.getPointList().stream()
                // 模糊匹配
                .filter(k -> pointNameLike(dto, k))
                // 点位类型
                .filter(k -> pointType(dto, k))
                // 作者访问过
                .filter(k -> authorVisit(dto, k))
                // scale 小于一定数值，移除距离中心点指定距离外的数据
                .filter(k -> removeByScale(dto, k, scale, removeKm))
                .map(this::tran)
                // scale 大于一定数值，移除距离中心点指定距离外的数据
                .peek(k -> rebuildLatitudeAndLongitude(dto, k, scale))
                .toList();
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

    private boolean removeByScale(PointSearchDTO dto, Point k, double scale, double removeKm) {
        if (dto.getScale() <= scale) {
            return true;
        }
        double distance = calculateDistance(
                Double.parseDouble(k.getLatitude()),
                Double.parseDouble(k.getLongitude()),
                dto.getCenterLatitude(),
                dto.getCenterLongitude()
        );
        return distance <= removeKm;
    }

    private void rebuildLatitudeAndLongitude(PointSearchDTO dto, PointSimpleVO k, double scale) {
        if (dto.getScale() <= scale) {
            log.error("偏移经纬度");
            // 对经纬度进行偏移，保护隐私，控制在2公里范围内
            // 15 0.0005
            // 14
            // 13
            // 12
            // 11
            // 10
            //  9
            //  8
            //  7
            //  6
            k.setLatitude(offsetLatitude(k.getLatitude(), 0.0005));
            k.setLongitude(offsetLongitude(k.getLongitude(), k.getLatitude(), 0.0005));
        }
    }

    /**
     * 对纬度进行偏移
     * @param lat 原始纬度
     * @param maxOffset 最大偏移量（度）
     * @return 偏移后的纬度
     */
    private static String offsetLatitude(String lat, double maxOffset) {
        double latitude = Double.parseDouble(lat);
        // 添加随机偏移，范围在 -maxOffset 到 +maxOffset 之间，控制在约2公里内
        double offsetValue = (Math.random() * 2 - 1) * maxOffset;
        return String.valueOf(latitude + offsetValue);
    }

    /**
     * 对经度进行偏移
     * @param lon 原始经度
     * @param lat 纬度（用于计算经度偏移）
     * @param maxOffset 最大偏移量（度）
     * @return 偏移后的经度
     */
    private static String offsetLongitude(String lon, String lat, double maxOffset) {
        double longitude = Double.parseDouble(lon);
        double latitude = Double.parseDouble(lat);
        // 根据纬度调整经度偏移量，保证偏移距离大致相同
        double offsetFactor = Math.cos(Math.toRadians(latitude));
        double offsetValue = (Math.random() * 2 - 1) * maxOffset * offsetFactor;
        return String.valueOf(longitude + offsetValue);
    }

    /**
     * 计算两个经纬度点之间的距离（单位：公里）
     * 使用 Haversine 公式
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