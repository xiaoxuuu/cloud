package cc.xiaoxu.cloud.my.manager;

import cc.xiaoxu.cloud.bean.dto.PointSearchDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.bean.vo.PointSimpleVO;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.my.entity.Point;
import cc.xiaoxu.cloud.my.entity.PointMap;
import cc.xiaoxu.cloud.my.entity.PointSource;
import cc.xiaoxu.cloud.my.service.PointMapService;
import cc.xiaoxu.cloud.my.service.PointService;
import cc.xiaoxu.cloud.my.service.PointSourceService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PointManager {

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

        // 来源搜索
        List<PointSource> sourceList = pointSourceService.lambdaQuery()
                .and(wrapper -> wrapper.or(orWrapper -> orWrapper
                        .like(PointSource::getSource, dto.getPointName())
                        .or().like(PointSource::getTitle, dto.getPointName())
                        .or().like(PointSource::getContent, dto.getPointName())
                        .or().like(PointSource::getUrl, dto.getPointName())
                ))
                // 删除数据排除
                .ne(PointSource::getState, StateEnum.DELETE.getCode())
                .list();
        List<Integer> idList = sourceList.stream().map(PointSource::getPointId).distinct().map(Integer::parseInt).toList();

        // 搜索
        boolean or = CollectionUtils.isNotEmpty(idList) || StringUtils.isNotEmpty(dto.getPointName());
//        List<Point> pointList = lambdaQuery()
//                .and(or, wrapper -> wrapper.or(orWrapper -> orWrapper
//                        .like(Point::getPointFullName, dto.getPointName())
//                        .or().like(Point::getPointFullName, dto.getPointName())
//                        .or().like(Point::getDescribe, dto.getPointName())
//                        .or().like(Point::getAddress, dto.getPointName())
//                        .or().like(Point::getLongitude, dto.getPointName())
//                        .or().like(Point::getLatitude, dto.getPointName())
//                        .or().in(CollectionUtils.isNotEmpty(idList), Point::getId, idList)
//                ))
//                .in(CollectionUtils.isNotEmpty(dto.getPointType()), Point::getPointType, dto.getPointType())
//                .in(Point::getState, List.of(StateEnum.ENABLE.getCode(), StateEnum.PROGRESSING.getCode()))
//                .ge(null != dto.getVisit() && dto.getVisit(), Point::getVisitedTimes, 1)
//                // 异常数据排除
//                .isNotNull(Point::getLongitude)
//                .isNotNull(Point::getLatitude)
//                // 删除数据排除
//                .ne(Point::getState, StateEnum.DELETE.getCode())
//                .list();

        return pointList.stream().map(k -> {
            PointSimpleVO vo = new PointSimpleVO();
            BeanUtils.populate(k, vo);
            vo.setPointName(k.getPointShortName());
            return vo;
        }).toList();
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
        log.debug("查询到 {} 条点位来源数据...", pointSourceList.size());
    }
}