package cc.xiaoxu.cloud.my.task;

import cc.xiaoxu.cloud.bean.dto.amap.AmapPoiSearchRequestDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.my.entity.Point;
import cc.xiaoxu.cloud.my.entity.PointMap;
import cc.xiaoxu.cloud.my.manager.AmapManager;
import cc.xiaoxu.cloud.my.service.PointMapService;
import cc.xiaoxu.cloud.my.service.PointService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class AmapTask {

    @Resource
    private AmapManager amapManager;

    @Resource
    private PointService pointService;

    @Resource
    private PointMapService pointMapService;

    @Scheduled(cron = "0 0 2 * * *")
    public void refreshData() {

        updateAmapData();
    }

    public void updateAmapData() {

        List<Point> pointList = pointService.lambdaQuery()
                .ne(Point::getState, StateEnum.DELETE.getCode())
                .list();

        List<PointMap> pointMapList = pointMapService.lambdaQuery()
                .ne(PointMap::getState, StateEnum.DELETE.getCode())
                .list();
        Map<Integer, PointMap> pointMapMap = pointMapList.stream().collect(Collectors.toMap(PointMap::getPointId, k -> k));
        Set<Integer> existsDataSet = pointMapList.stream()
                .filter(k -> StringUtils.isNotBlank(k.getAmapResult()))
                .map(PointMap::getPointId)
                .collect(Collectors.toSet());

        List<Point> needHandleList = pointList.stream().filter(k -> !existsDataSet.contains(k.getId())).toList();

        for (Point point : needHandleList) {

            AmapPoiSearchRequestDTO amapDTO = new AmapPoiSearchRequestDTO();
            amapDTO.setShowFields("business");
            amapDTO.setExtensions("all");
            amapDTO.setKeywords(point.getPointFullName());
            amapDTO.setRegion(point.getAddressCode());
            String amapResponse = amapManager.searchPoiString(amapDTO);
            JSONObject jsonObject = JSON.parseObject(amapResponse);
            Object poiListObj = jsonObject.get("pois");
            String poiListString = JSON.toJSONString(poiListObj);
            List<JSONObject> poiList = JSON.parseArray(poiListString, JSONObject.class);
            if (poiList.isEmpty()) {
                continue;
            }
            JSONObject firstData = poiList.getFirst();
            String id = (String) firstData.get("id");
            PointMap pointMap = pointMapMap.get(point.getId());
            if (pointMap == null) {
                pointMap = new PointMap();
                pointMap.setPointId(point.getId());
            }
            pointMap.setAmapId(id);
            pointMap.setAmapResult(JSONObject.toJSONString(firstData));
            pointMapService.saveOrUpdate(pointMap);
        }
    }
}