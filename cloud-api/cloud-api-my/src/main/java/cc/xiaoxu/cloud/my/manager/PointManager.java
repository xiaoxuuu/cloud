package cc.xiaoxu.cloud.my.manager;

import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.my.entity.*;
import cc.xiaoxu.cloud.my.service.*;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Service
public class PointManager {

    @Resource
    private ConstantService constantService;

    @Resource
    private AreaService areaService;

    @Resource
    private PointService pointService;

    @Resource
    private PointMapService pointMapService;

    @Resource
    private PointSourceService pointSourceService;

    private List<Area> areaList = new ArrayList<>();
    private Map<String, Area> areaMap = new HashMap<>();

    private List<PointTemp> pointList = new ArrayList<>();
    private Map<Integer, PointTemp> pointMap = new HashMap<>();

    private List<PointMap> pointMapList = new ArrayList<>();
    private Map<Integer, PointMap> pointMapMap = new HashMap<>();

    private List<PointSource> pointSourceList = new ArrayList<>();
    private Map<Integer, PointSource> pointSourceMap = new HashMap<>();

    public void updatePointList() {

        pointList = pointService.lambdaQuery()
                // 异常数据排除
                .isNotNull(Point::getLongitude)
                .isNotNull(Point::getLatitude)
                // 无效数据排除
                .ne(Point::getState, StateEnum.DELETE.getCode())
                .list().stream()
                .map(this::tranFake)
                .toList();
        pointMap = pointList.stream().collect(Collectors.toMap(Point::getId, a -> a));
        log.debug("查询到 {} 条点位数据...", pointList.size());
    }

    public void updatePointMapList() {

        pointMapList = pointMapService.lambdaQuery()
                // 无效数据排除
                .eq(PointMap::getState, StateEnum.ENABLE.getCode())
                .list();
        pointMapMap = pointMapList.stream().collect(Collectors.toMap(PointMap::getPointId, a -> a));
        log.debug("查询到 {} 条点位地图数据...", pointMapList.size());
    }

    public void updatePointSourceList() {

        pointSourceList = pointSourceService.lambdaQuery()
                // 无效数据排除
                .eq(PointSource::getState, StateEnum.ENABLE.getCode())
                .list();
        pointSourceMap = pointSourceList.stream().collect(Collectors.toMap(PointSource::getPointId, a -> a));
        log.debug("查询到 {} 条点位来源数据...", pointSourceList.size());
    }

    public void updateAreaList() {

        areaList = areaService.lambdaQuery()
                // 无效数据排除
                .eq(Area::getState, StateEnum.ENABLE.getCode())
                .list();
        areaMap = areaList.stream().collect(Collectors.toMap(Area::getCode, a -> a));
        log.debug("查询到 {} 条地点数据...", areaList.size());
    }

    private PointTemp tranFake(Point point) {
        PointTemp pointTemp = new PointTemp();
        BeanUtils.populate(point, pointTemp);
        return pointTemp;
    }

    /**
     * 对纬度进行偏移
     *
     * @param lat       原始纬度
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
     *
     * @param lon       原始经度
     * @param lat       纬度（用于计算经度偏移）
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
}