package cc.xiaoxu.cloud.my.manager;

import cc.xiaoxu.cloud.core.bean.entity.BaseIdEntity;
import cc.xiaoxu.cloud.my.dao.PointMapMapper;
import cc.xiaoxu.cloud.my.entity.Point;
import cc.xiaoxu.cloud.my.entity.PointTag;
import cc.xiaoxu.cloud.my.service.PointMapService;
import cc.xiaoxu.cloud.my.service.PointService;
import cc.xiaoxu.cloud.my.service.PointTagService;
import cc.xiaoxu.cloud.my.utils.TagUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class PointDataManager {

    private final PointMapService pointMapService;
    private final PointService pointService;
    private final PointTagService pointTagService;

    public void reloadDataFromMap() {

        List<Point> pointList = pointService.list();

        List<PointTag> pointTagList = pointTagService.list();
        Map<String, Integer> tagMap = pointTagList.stream().collect(Collectors.toMap(PointTag::getTagName, BaseIdEntity::getId));

        List<PointMapMapper.MapData> mapData = pointMapService.getBaseMapper().loadData();
        Map<String, PointMapMapper.MapData> dataMap = mapData.stream().collect(Collectors.toMap(a -> a.name() + a.location(), a -> a));

        for (Point point : pointList) {
            PointMapMapper.MapData data = dataMap.get(point.getPointShortName() + point.getLongitude() + "," + point.getLatitude());
            if (null == data) {
                continue;
            }
            // TODO 缺 tag
            Set<String> tagSet = TagUtils.getTagSet(data.keytag(), data.rectag()).stream()
                    .map(tagMap::get)
                    .filter(Objects::nonNull)
                    .map(String::valueOf)
                    .collect(Collectors.toCollection(HashSet::new));

            if (StringUtils.isNotBlank(point.getTagIdList())) {
                tagSet.addAll(Arrays.asList(point.getTagIdList().split(",")));
            }
            String tagIdSet = tagSet.stream().sorted().map(String::valueOf).collect(Collectors.joining(","));

            if (StringUtils.isNotBlank(tagIdSet)) {
                pointService.lambdaUpdate()
                        .eq(Point::getId, point.getId())
                        .set(StringUtils.isNotBlank(tagIdSet), Point::getTagIdList, tagIdSet)
                        .set(StringUtils.isNotBlank(data.address()), Point::getAddress, data.address())
//                        .set(StringUtils.isNotBlank(data.tel()), Point::getTelephone, data.tel().replace(";", ","))
                        .set(StringUtils.isNotBlank(data.cost()), Point::getCost, data.cost())
                        .set(StringUtils.isNotBlank(data.openTime()), Point::getOpeningHours, data.openTime())
                        .update();
            }

            log.error("数据处理完成");
        }
    }
}