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

    public void updateAreaList() {

        areaList = areaService.lambdaQuery()
                // 无效数据排除
                .eq(Area::getState, StateEnum.ENABLE.getCode())
                .list();
        areaMap = areaList.stream().collect(Collectors.toMap(Area::getCode, a -> a));
        log.debug("查询到 {} 条地点数据...", areaList.size());
    }

    public void updatePointList() {

        pointList = pointService.lambdaQuery()
                // 异常数据排除
                .isNotNull(Point::getLongitude)
                .isNotNull(Point::getLatitude)
                // 无效数据排除
                .ne(Point::getState, StateEnum.DELETE.getCode())
                .list()
                .stream()
                .map(this::tran)
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

    private PointTemp tran(Point point) {
        PointTemp pointTemp = new PointTemp();
        BeanUtils.populate(point, pointTemp);
        return pointTemp;
    }
}