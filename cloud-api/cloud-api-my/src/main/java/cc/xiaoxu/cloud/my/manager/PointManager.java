package cc.xiaoxu.cloud.my.manager;

import cc.xiaoxu.cloud.bean.dto.PointSearchDTO;
import cc.xiaoxu.cloud.bean.enums.PointTypeEnum;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.bean.vo.PointSimpleVO;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.core.utils.enums.EnumUtils;
import cc.xiaoxu.cloud.my.entity.Point;
import cc.xiaoxu.cloud.my.entity.PointMap;
import cc.xiaoxu.cloud.my.entity.PointSource;
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

        Set<PointTypeEnum> pointTypeSet = dto.getPointType().stream()
                .map(k -> EnumUtils.getByClass(dto.getPointType(), PointTypeEnum.class))
                .collect(Collectors.toSet());

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
                    if (CollectionUtils.isNotEmpty(pointTypeSet)) {
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

        return filterPointList.stream().map(k -> {
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
        pointSourceMap = pointSourceList.stream().collect(Collectors.toMap(PointSource::getPointId, a -> a));
        log.debug("查询到 {} 条点位来源数据...", pointSourceList.size());
    }
}