package cc.xiaoxu.cloud.my.manager;

import cc.xiaoxu.cloud.bean.dto.PointSearchDTO;
import cc.xiaoxu.cloud.bean.enums.PointTypeEnum;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.bean.vo.PointSimpleVO;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.core.utils.enums.EnumUtils;
import cc.xiaoxu.cloud.my.entity.Constant;
import cc.xiaoxu.cloud.my.entity.Point;
import cc.xiaoxu.cloud.my.entity.PointMap;
import cc.xiaoxu.cloud.my.entity.PointSource;
import cc.xiaoxu.cloud.my.service.ConstantService;
import cc.xiaoxu.cloud.my.service.PointMapService;
import cc.xiaoxu.cloud.my.service.PointService;
import cc.xiaoxu.cloud.my.service.PointSourceService;
import cc.xiaoxu.cloud.my.utils.SearchUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PointManager {

    @Resource
    private ConstantService constantService;

    @Resource
    private PointService pointService;

    @Resource
    private PointMapService pointMapService;

    @Resource
    private PointSourceService pointSourceService;

    private List<Point> pointList = new ArrayList<>();
    private Map<Integer, Point> pointMap = new HashMap<>();

    private List<PointMap> pointMapList = new ArrayList<>();
    private Map<Integer, PointMap> pointMapMap = new HashMap<>();

    private List<PointSource> pointSourceList = new ArrayList<>();
    private Map<Integer, PointSource> pointSourceMap = new HashMap<>();


    public List<? extends PointSimpleVO> lists(PointSearchDTO dto) {

        // 来源
        Set<Integer> pointSourceSet = pointSourceList.stream()
                .filter(k -> {
                    if (StringUtils.isNotBlank(dto.getPointName())) {
                        return SearchUtils.containsValue(k.getTitle(), dto.getPointName()) ||
                                SearchUtils.containsValue(k.getContent(), dto.getPointName());
                    }
                    return true;
                })
                .map(PointSource::getPointId)
                .collect(Collectors.toSet());

        Set<PointTypeEnum> pointTypeSet;
        if (CollectionUtils.isNotEmpty(dto.getPointType())) {
            pointTypeSet = dto.getPointType().stream()
                    .map(k -> EnumUtils.getByClass(dto.getPointType(), PointTypeEnum.class))
                    .collect(Collectors.toSet());
        } else {
            pointTypeSet = new HashSet<>();
        }

        // 搜索
        List<Point> filterPointList = pointList.stream()
                .filter(k -> {
                    // 模糊匹配
                    return pointSourceSet.contains(k.getId()) ||
                            SearchUtils.containsValue(k.getPointFullName(), dto.getPointName()) ||
                            SearchUtils.containsValue(k.getPointShortName(), dto.getPointName()) ||
                            SearchUtils.containsValue(k.getAddress(), dto.getPointName()) ||
                            SearchUtils.containsValue(k.getAddressCode(), dto.getPointName()) ||
                            SearchUtils.containsValue(k.getDescribe(), dto.getPointName());
                })
                .filter(k -> {
                    // 点位类型
                    if (CollectionUtils.isEmpty(pointTypeSet)) {
                        return true;
                    }
                    return pointTypeSet.contains(k.getPointType());
                })
                .filter(k -> {
                    // 作者访问过
                    if (null == dto.getVisit()) {
                        return true;
                    }
                    if (dto.getVisit()) {
                        return k.getVisitedTimes() > 1;
                    } else {
                        return true;
                    }
                })
                .toList();

        Constant pointRemoveKm = constantService.lambdaQuery()
                .eq(Constant::getName, "point_remove_km")
                .one();
        double removeKm = Double.parseDouble(pointRemoveKm.getValue());
        Constant pointScale = constantService.lambdaQuery()
                .eq(Constant::getName, "point_scale")
                .one();
        double scale = Double.parseDouble(pointScale.getValue());

        return filterPointList.stream()
                // scale 小于一定数值，移除距离中心点指定距离外的数据
                .filter(k -> removeByScale(k, dto, scale, removeKm))
                .map(k -> {
                    PointSimpleVO vo = new PointSimpleVO();
                    BeanUtils.populate(k, vo);
                    vo.setPointName(k.getPointShortName());
                    // scale 大于一定数值，移除距离中心点指定距离外的数据
                    rebuildLatitudeAndLongitude(dto, vo, scale);
                    return vo;
                }).toList();
    }

    private boolean removeByScale(Point k, PointSearchDTO dto, double scale, double removeKm) {
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

    private static void rebuildLatitudeAndLongitude(PointSearchDTO dto, PointSimpleVO k, double scale) {
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

    public void updatePointList() {

        pointList = pointService.lambdaQuery()
                // 异常数据排除
                .isNotNull(Point::getLongitude)
                .isNotNull(Point::getLatitude)
                // 无效数据排除
                .ne(Point::getState, StateEnum.DELETE.getCode())
                .list();
        pointMap = pointList.stream().collect(Collectors.toMap(Point::getId, a -> a));
        log.debug("查询到 {} 条点位数据...", pointList.size());
    }

    public void updatePointMapList() {

        pointMapList = pointMapService.lambdaQuery()
                // 无效数据排除
                .ne(PointMap::getState, StateEnum.DELETE.getCode())
                .list();
        pointMapMap = pointMapList.stream().collect(Collectors.toMap(PointMap::getId, a -> a));
        log.debug("查询到 {} 条点位地图数据...", pointMapList.size());
    }

    public void updatePointSourceList() {

        pointSourceList = pointSourceService.lambdaQuery()
                // 无效数据排除
                .ne(PointSource::getState, StateEnum.DELETE.getCode())
                .list();
        pointSourceMap = pointSourceList.stream().collect(Collectors.toMap(PointSource::getPointId, a -> a));
        log.debug("查询到 {} 条点位来源数据...", pointSourceList.size());
    }
}