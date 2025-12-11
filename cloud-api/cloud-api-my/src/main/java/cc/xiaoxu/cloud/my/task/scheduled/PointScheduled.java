package cc.xiaoxu.cloud.my.task.scheduled;

import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.bean.vo.PointTagVO;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.my.entity.*;
import cc.xiaoxu.cloud.my.manager.PointManager;
import cc.xiaoxu.cloud.my.service.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class PointScheduled {

    @Resource
    private AreaService areaService;

    @Resource
    private PointService pointService;

    @Resource
    private PointMapService pointMapService;

    @Resource
    private PointSourceService pointSourceService;

    @Resource
    private PointTagService pointTagService;

    @Resource
    private PointManager pointManager;

    @Scheduled(cron = "${app.config.refresh-data}")
    public void refreshData() {

        updateAreaList();
        updatePointList();
        updatePointMapList();
        updatePointSourceList();
        updatePointTagList();
        updatePointTagUsedList();
    }

    public void updatePointTagUsedList() {
        List<PointTagVO> pointTagList = pointManager.getPointTagList();
        List<PointTemp> pointList = pointManager.getPointList();
        Set<Integer> usedTagIdSet = pointList.stream()
                .map(PointTemp::getTagIdList)
                .filter(Objects::nonNull)
                .map(k -> k.split(","))
                .flatMap(Arrays::stream)
                .filter(StringUtils::isNotBlank)
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
        List<PointTagVO> usedList = pointTagList.stream().filter(k -> usedTagIdSet.contains(k.getId())).toList();
        pointManager.setPointTagUsedList(usedList);
        log.info("查询到 {} 条使用中标签数据...", usedList.size());
    }

    public void updatePointTagList() {

        List<PointTagVO> list = pointTagService.lambdaQuery()
                // 无效数据排除
                .eq(PointTag::getState, StateEnum.ENABLE.getCode())
                .list()
                .stream()
                .map(this::toPointTagVO)
                .sorted(Comparator.comparing(PointTagVO::getSort))
                .toList();
        Map<Integer, PointTagVO> areaMap = list.stream().collect(Collectors.toMap(PointTagVO::getId, a -> a));
        pointManager.setPointTagList(list);
        pointManager.setPointTagMap(areaMap);
        log.info("查询到 {} 条标签数据...", list.size());
    }

    private PointTagVO toPointTagVO(PointTag entity) {
        PointTagVO vo = new PointTagVO();
        vo.setId(entity.getId());
        vo.setColor(entity.getColor());
        vo.setTagName(entity.getTagName());
        vo.setCategory(entity.getCategory());
        vo.setSort(entity.getSort());
        return vo;
    }

    public void updateAreaList() {

        List<Area> areaList = areaService.lambdaQuery()
                // 无效数据排除
                .eq(Area::getState, StateEnum.ENABLE.getCode())
                .list();
        Map<Integer, Area> areaMap = areaList.stream().collect(Collectors.toMap(a -> Integer.parseInt(a.getCode()), a -> a));
        pointManager.setAreaList(areaList);
        pointManager.setAreaMap(areaMap);
        log.info("查询到 {} 条地点数据...", areaList.size());
    }

    public void updatePointList() {

        List<PointTemp> pointList = pointService.lambdaQuery()
                // 异常数据排除
                .isNotNull(Point::getLongitude)
                .isNotNull(Point::getLatitude)
                // 无效数据排除
                .ne(Point::getState, StateEnum.DELETE.getCode())
                .list()
                .stream()
                .map(this::tran)
                .toList();
        Map<Integer, PointTemp> pointMap = pointList.stream().collect(Collectors.toMap(Point::getId, a -> a));
        Map<String, PointTemp> pointMapCode = pointList.stream().collect(Collectors.toMap(Point::getCode, a -> a));
        pointManager.setPointList(pointList);
        pointManager.setPointMap(pointMap);
        pointManager.setPointMapCode(pointMapCode);
        log.info("查询到 {} 条点位数据...", pointList.size());
    }

    public void updatePointMapList() {

        List<PointMap> pointMapList = pointMapService.lambdaQuery()
                // 无效数据排除
                .eq(PointMap::getState, StateEnum.ENABLE.getCode())
                .list();
        Map<Integer, PointMap> pointMapMap = pointMapList.stream().collect(Collectors.toMap(PointMap::getPointId, a -> a));
        pointManager.setPointMapList(pointMapList);
        pointManager.setPointMapMap(pointMapMap);
        log.info("查询到 {} 条点位地图数据...", pointMapList.size());
    }

    public void updatePointSourceList() {

        List<PointSource> pointSourceList = pointSourceService.lambdaQuery()
                // 无效数据排除
                .eq(PointSource::getState, StateEnum.ENABLE.getCode())
                .list();
        Map<Integer, PointSource> pointSourceMap = pointSourceList.stream().collect(Collectors.toMap(PointSource::getPointId, a -> a));
        pointManager.setPointSourceList(pointSourceList);
        pointManager.setPointSourceMap(pointSourceMap);
        log.info("查询到 {} 条点位来源数据...", pointSourceList.size());
    }

    private PointTemp tran(Point point) {
        PointTemp pointTemp = new PointTemp();
        BeanUtils.populate(point, pointTemp);
        if (StringUtils.isNotEmpty(point.getTagIdList())) {
            pointTemp.setTagIdSet(Arrays.stream(point.getTagIdList().split(",")).collect(Collectors.toSet()));
        }
        return pointTemp;
    }
}