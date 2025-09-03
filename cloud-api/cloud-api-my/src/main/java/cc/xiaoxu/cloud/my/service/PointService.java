package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.bean.dto.*;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.bean.vo.PointFullVO;
import cc.xiaoxu.cloud.bean.vo.PointSimpleVO;
import cc.xiaoxu.cloud.bean.vo.PointSourceVO;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.my.dao.PointMapper;
import cc.xiaoxu.cloud.my.entity.Point;
import cc.xiaoxu.cloud.my.entity.PointMap;
import cc.xiaoxu.cloud.my.entity.PointSource;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Getter
@Slf4j
@Service
@AllArgsConstructor
public class PointService extends ServiceImpl<PointMapper, Point> {

    private final PointSourceService pointSourceService;
    private final PointMapService pointMapService;

    public void add(PointAddDTO dto) {

        Point point = new Point();
        BeanUtils.populate(dto, point);
        point.setState(StateEnum.ENABLE.getCode());
        save(point);

        List<PointSourceAddDTO> sourceAddDTOList = dto.getSource();
        addSource(sourceAddDTOList);
    }

    private void addSource(List<? extends PointSourceAddDTO> sourceAddDTOList) {
        if (CollectionUtils.isNotEmpty(sourceAddDTOList)) {
            List<PointSource> list = sourceAddDTOList.stream().map(source -> {
                PointSource pointSource = new PointSource();
                BeanUtils.populate(source, pointSource);
                return pointSource;
            }).toList();
            pointSourceService.saveBatch(list);
        }
    }

    public void edit(PointEditDTO dto) {

        lambdaUpdate()
                .set(null != dto.getPointType(), Point::getPointType, dto.getPointType())
                .set(StringUtils.isNotBlank(dto.getPointShortName()), Point::getPointShortName, dto.getPointShortName())
                .set(StringUtils.isNotBlank(dto.getPointFullName()), Point::getPointFullName, dto.getPointFullName())
                .set(StringUtils.isNotBlank(dto.getDescribe()), Point::getDescribe, dto.getDescribe())
                .set(StringUtils.isNotBlank(dto.getAddress()), Point::getAddress, dto.getAddress())
                .set(StringUtils.isNotBlank(dto.getLongitude()), Point::getLongitude, dto.getLongitude())
                .set(StringUtils.isNotBlank(dto.getLatitude()), Point::getLatitude, dto.getLatitude())
                .set(dto.getCollectTimes() != null, Point::getCollectTimes, dto.getCollectTimes())
                .set(dto.getVisitedTimes() != null, Point::getVisitedTimes, dto.getVisitedTimes())
                .set(StringUtils.isNotBlank(dto.getAddressCode()), Point::getAddressCode, dto.getAddressCode())
                .set(null != dto.getState(), Point::getState, dto.getState())
                .set(null == dto.getState(), Point::getState, StateEnum.ENABLE)
                .eq(Point::getId, Integer.parseInt(dto.getId()))
                .update();

        List<PointSourceEditDTO> sourceList = dto.getSourceEdit();
        // 新增
        List<PointSourceEditDTO> addSourceList = sourceList.stream().filter(k -> StringUtils.isEmpty(k.getId())).toList();
        addSource(addSourceList);
        // 编辑
        List<PointSourceEditDTO> editSourceList = sourceList.stream().filter(k -> StringUtils.isNotEmpty(k.getId())).toList();
        for (PointSourceEditDTO sourceDTO : editSourceList) {
            pointSourceService.lambdaUpdate()
                    .set(null != sourceDTO.getType(), PointSource::getType, sourceDTO.getType())
                    .set(StringUtils.isNotBlank(sourceDTO.getSource()), PointSource::getSource, sourceDTO.getSource())
                    .set(StringUtils.isNotBlank(sourceDTO.getTitle()), PointSource::getTitle, sourceDTO.getTitle())
                    .set(StringUtils.isNotBlank(sourceDTO.getContent()), PointSource::getContent, sourceDTO.getContent())
                    .set(StringUtils.isNotBlank(sourceDTO.getUrl()), PointSource::getUrl, sourceDTO.getUrl())
                    .eq(PointSource::getId, sourceDTO.getId())
                    .update();
        }
    }

    private void handle(PointSearchDTO dto, List<? extends PointSimpleVO> pointVOList) {

    }

    public PointFullVO get(IdDTO dto) {

        Point point;
        int id = Integer.parseInt(dto.getId());
        if (id < 0) {
            point = lambdaQuery()
                    .eq(Point::getState, StateEnum.PROGRESSING)
                    .last(" LIMIT 1 ")
                    .one();
        } else {
            point = getById(id);
        }
        if (null == point) {
            throw new CustomException("无数据");
        }
        PointFullVO vo = new PointFullVO();
        BeanUtils.populate(point, vo);

        // 来源
        List<PointSource> sourceList = pointSourceService.lambdaQuery().eq(PointSource::getPointId, point.getId()).list();
        List<PointSourceVO> pointSourceList = BeanUtils.populateList(sourceList, PointSourceVO.class);
        vo.setPointSourceList(pointSourceList);
        vo.setPointName(point.getPointFullName());

        // 地图数据
        PointMap pointMap = pointMapService.lambdaQuery()
                .eq(PointMap::getPointId, point.getId())
                .eq(PointMap::getState, StateEnum.ENABLE.getCode())
                .orderBy(true, false, PointMap::getId)
                .last(" LIMIT 1 ")
                .one();
        buildAmap(vo, pointMap);

        return vo;
    }

    private void buildAmap(PointFullVO point, PointMap pointMap) {

        if (pointMap == null) {
            return;
        }
        Object amapResult = pointMap.getAmapResult();
        if (null == amapResult) {
            return;
        }
        JSONObject amapJson = JSONObject.parseObject((String) amapResult);
        if (amapJson.containsKey("business")) {
            JSONObject businessJson = (JSONObject) amapJson.get("business");
            if (businessJson.containsKey("tag")) {
                String tag = (String) businessJson.get("tag");
                if (StringUtils.isNotEmpty(tag)) {
                    point.setTagList(Arrays.stream(tag.split(",")).toList());
                }
            }
            if (businessJson.containsKey("tel")) {
                String tel = (String) businessJson.get("tel");
                if (StringUtils.isNotEmpty(tel)) {
                    point.setTelList(Arrays.stream(tel.split(",")).toList());
                }
            }
            if (businessJson.containsKey("cost")) {
                point.setCost((String) businessJson.get("cost"));
            }
            if (businessJson.containsKey("rating")) {
                point.setRating((String) businessJson.get("rating"));
            }
            if (businessJson.containsKey("opentime_week")) {
                point.setOpenTime((String) businessJson.get("opentime_week"));
            }
        }
    }

    public Integer countProgressing() {
        return baseMapper.countProgressing();
    }

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
        List<Integer> idList = sourceList.stream().map(PointSource::getPointId).distinct().toList();

        // 搜索
        boolean or = CollectionUtils.isNotEmpty(idList) || StringUtils.isNotEmpty(dto.getPointName());
        List<Point> pointList = lambdaQuery()
                .and(or, wrapper -> wrapper.or(orWrapper -> orWrapper
                        .like(Point::getPointFullName, dto.getPointName())
                        .or().like(Point::getPointFullName, dto.getPointName())
                        .or().like(Point::getDescribe, dto.getPointName())
                        .or().like(Point::getAddress, dto.getPointName())
                        .or().like(Point::getLongitude, dto.getPointName())
                        .or().like(Point::getLatitude, dto.getPointName())
                        .or().in(CollectionUtils.isNotEmpty(idList), Point::getId, idList)
                ))
//                .in(CollectionUtils.isNotEmpty(dto.getPointType()), Point::getPointType, dto.getPointType())
                .in(Point::getState, List.of(StateEnum.ENABLE.getCode(), StateEnum.PROGRESSING.getCode()))
                .ge(null != dto.getVisit() && dto.getVisit(), Point::getVisitedTimes, 1)
                // 异常数据排除
                .isNotNull(Point::getLongitude)
                .isNotNull(Point::getLatitude)
                // 删除数据排除
                .ne(Point::getState, StateEnum.DELETE.getCode())
                .list();

        double scale = 14.5;
        double removeKm = 2;

        return pointList.stream()
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
        if (null == dto.getScale() || dto.getScale() <= scale) {
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
        if (null != dto.getScale() && dto.getScale() <= scale) {
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
}