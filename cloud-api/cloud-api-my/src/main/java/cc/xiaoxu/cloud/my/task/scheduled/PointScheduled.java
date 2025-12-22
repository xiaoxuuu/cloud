package cc.xiaoxu.cloud.my.task.scheduled;

import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.bean.vo.PointSourceAuthorVO;
import cc.xiaoxu.cloud.bean.vo.PointSourceVO;
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
    private PointSourceService pointSourceService;

    @Resource
    private PointTagService pointTagService;

    @Resource
    private PointSourceAuthorService pointSourceAuthorService;

    @Resource
    private PointManager pointManager;

    @Scheduled(cron = "${app.config.refresh-data}")
    public void refreshData() {

        // 地区
        updateAreaList();
        // 标签
        updatePointTagList();
        // 来源作者
        updatePointSourceAuthorList();
        // 来源
        updatePointSourceList();
        // 点位
        updatePointList();
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

    public void updatePointSourceList() {

        List<PointSourceVO> pointSourceList = pointSourceService.lambdaQuery()
                // 无效数据排除
                .eq(PointSource::getState, StateEnum.ENABLE.getCode())
                .list()
                .stream().map(this::toPointSourceVO)
                .toList();
        Map<Integer, PointSourceVO> pointSourceMap = pointSourceList.stream().collect(Collectors.toMap(PointSourceVO::getId, a -> a));
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

    public void updatePointSourceAuthorList() {

        List<PointSourceAuthorVO> list = pointSourceAuthorService.lambdaQuery()
                // 无效数据排除
                .eq(PointSourceAuthor::getState, StateEnum.ENABLE.getCode())
                .list()
                .stream()
                .map(this::toPointSourceAuthorVO)
                .toList();
        Map<Integer, PointSourceAuthorVO> authorMap = list.stream().collect(Collectors.toMap(PointSourceAuthorVO::getId, a -> a));
        pointManager.setPointSourceAuthorList(list);
        pointManager.setPointSourceAuthorMap(authorMap);
        log.info("查询到 {} 条来源作者数据...", list.size());
    }

    private PointSourceAuthorVO toPointSourceAuthorVO(PointSourceAuthor entity) {
        PointSourceAuthorVO vo = new PointSourceAuthorVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setTiktokUrl(entity.getTiktokUrl());
        vo.setRedbookUrl(entity.getRedbookUrl());
        vo.setBilibiliUrl(entity.getBilibiliUrl());
        vo.setContent(entity.getContent());
        return vo;
    }

    private PointSourceVO toPointSourceVO(PointSource entity) {
        PointSourceVO vo = new PointSourceVO();
        vo.setId(entity.getId());
        vo.setType(entity.getType());
        vo.setTitle(entity.getTitle());
        vo.setUrl(entity.getUrl());
        return vo;
    }
}