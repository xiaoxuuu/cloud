package cc.xiaoxu.cloud.my.task.scheduled;

import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.bean.vo.*;
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
        // 来源作者 - 使用中
        updatePointSourceAuthorListUsed();
    }

    private void updatePointSourceAuthorListUsed() {

        Set<Integer> authorIdSet = pointManager.getPointList().stream().map(PointFullVO::getAuthorIdSet).flatMap(Collection::stream).collect(Collectors.toSet());
        List<PointSourceAuthorVO> list = pointManager.getPointSourceAuthorList().stream()
                .filter(k -> authorIdSet.contains(k.getId()))
                .sorted(Comparator.comparing(PointSourceAuthorVO::getId))
                .toList();
        pointManager.setPointSourceAuthorListAll(list);
        log.info("查询到 {} 条使用中来源作者数据...", list.size());
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

        List<PointFullVO> pointList = pointService.lambdaQuery()
                // 异常数据排除
                .isNotNull(Point::getLongitude)
                .isNotNull(Point::getLatitude)
                // 无效数据排除
                .ne(Point::getState, StateEnum.DELETE.getCode())
                .list()
                .stream()
                .map(this::toPointFullVO)
                .toList();
        Map<String, PointFullVO> pointMapCode = pointList.stream().collect(Collectors.toMap(PointFullVO::getCode, a -> a));
        pointManager.setPointList(pointList);
        pointManager.setPointMapCode(pointMapCode);

        // 精简参数
        Map<String, PointShowVO> pointShowMapCode = pointList.stream()
                .map(k -> (PointShowVO) BeanUtils.populate(k, PointShowVO.class))
                .collect(Collectors.toMap(PointShowVO::getCode, a -> a));
        pointManager.setPointShowMapCode(pointShowMapCode);
        log.info("查询到 {} 条点位数据...", pointList.size());
    }

    public void updatePointSourceList() {

        List<PointSourceVO> pointSourceList = pointSourceService.lambdaQuery()
                // 无效数据排除
                .eq(PointSource::getState, StateEnum.ENABLE.getCode())
                .list()
                .stream().map(this::toPointSourceVO)
                .sorted(Comparator.comparing(PointSourceVO::getId))
                .toList();
        Map<Integer, PointSourceVO> pointSourceMap = pointSourceList.stream().collect(Collectors.toMap(PointSourceVO::getId, a -> a));
        pointManager.setPointSourceList(pointSourceList);
        pointManager.setPointSourceMap(pointSourceMap);
        log.info("查询到 {} 条点位来源数据...", pointSourceList.size());
    }

    private PointFullVO toPointFullVO(Point point) {

        PointFullVO vo = new PointFullVO();
        BeanUtils.populate(point, vo);

        // 来源
        if (StringUtils.isNotBlank(point.getSourceIdList())) {
            List<PointSourceVO> list = Arrays.stream(vo.getSourceIdList().split(","))
                    .map(k -> pointManager.getPointSourceMap().get(Integer.parseInt(k)))
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingInt(k -> k.getType().getSortingWeight() + k.getId()))
                    .toList();
            vo.setSourceList(list.stream().map(this::toPointSourceShowVO).toList());
            vo.setSourceIdSet(list.stream().map(PointSourceVO::getId).collect(Collectors.toSet()));
            vo.setAuthorIdSet(list.stream().map(PointSourceVO::getAuthorId).collect(Collectors.toSet()));
        } else {
            vo.setSourceList(Collections.emptyList());
            vo.setSourceIdSet(Collections.emptySet());
            vo.setAuthorIdSet(Collections.emptySet());
        }

        // 标签
        if (StringUtils.isNotBlank(point.getTagIdList())) {
            List<PointTagVO> list = Arrays.stream(point.getTagIdList().split(","))
                    .map(k -> pointManager.getPointTagMap().get(Integer.parseInt(k)))
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparingInt(PointTagVO::getSort))
                    .toList();
            vo.setTagList(list.stream().map(this::toPointTagShowVO).toList());
            vo.setTagIdSet(list.stream().map(PointTagVO::getId).collect(Collectors.toSet()));
        } else {
            vo.setTagList(Collections.emptyList());
            vo.setTagIdSet(Collections.emptySet());
        }

        // 电话
        if (StringUtils.isNotBlank(point.getTelephone())) {
            vo.setTelList(Arrays.stream(point.getTelephone().split(",")).toList());
        } else {
            vo.setTelList(Collections.emptyList());
        }

        // 模糊匹配字段
        List<String> searchValueList = new ArrayList<>();
        searchValueList.add(vo.getPointFullName());
        searchValueList.add(vo.getAddress());
        searchValueList.add(vo.getDescribe());
        searchValueList.addAll(vo.getTagList().stream().map(PointTagShowVO::getTagName).toList());
        searchValueList.addAll(vo.getSourceList().stream().map(PointSourceShowVO::getTitle).toList());
        searchValueList.addAll(vo.getSourceList().stream().map(PointSourceShowVO::getAuthorName).toList());
        vo.setSearchValue(String.join(",", searchValueList));
        return vo;
    }

    private PointSourceShowVO toPointSourceShowVO(PointSourceVO pointSourceVO) {

        PointSourceShowVO vo = new PointSourceShowVO();
        PointSourceAuthorVO pointSourceAuthorVO = pointManager.getPointSourceAuthorMap().get(pointSourceVO.getAuthorId());
        if (pointSourceAuthorVO != null) {
            vo.setAuthorName(pointSourceAuthorVO.getName());
        }
        vo.setType(pointSourceVO.getType());
        vo.setTitle(pointSourceVO.getTitle());
        vo.setUrl(pointSourceVO.getUrl());
        return vo;
    }

    private PointTagShowVO toPointTagShowVO(PointTagVO pointTagVO) {

        PointTagShowVO vo = new PointTagShowVO();
        vo.setTagName(pointTagVO.getTagName());
        vo.setColor(pointTagVO.getColor());
        return vo;
    }

    public void updatePointSourceAuthorList() {

        List<PointSourceAuthorVO> list = pointSourceAuthorService.lambdaQuery()
                // 无效数据排除
                .eq(PointSourceAuthor::getState, StateEnum.ENABLE.getCode())
                .list()
                .stream()
                .map(this::toPointSourceAuthorVO)
                .sorted(Comparator.comparing(PointSourceAuthorVO::getId))
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
        vo.setAuthorId(entity.getAuthorId());
        vo.setType(entity.getType());
        vo.setContent(entity.getContent());
        vo.setTitle(entity.getTitle());
        vo.setUrl(entity.getUrl());
        return vo;
    }
}