package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.bean.dto.*;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.bean.vo.PointFullVO;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Getter
@Slf4j
@Service
@AllArgsConstructor
public class PointService extends ServiceImpl<PointMapper, Point> {

    private final PointSourceService pointSourceService;
    private final PointMapService pointMapService;

    @Transactional(rollbackFor = Exception.class)
    public void add(PointAddDTO dto) {

        Point point = new Point();
        BeanUtils.populate(dto, point);
        point.setState(StateEnum.ENABLE.getCode());
        save(point);

        // 地图
        addOrEditPointMap(point.getId(), dto.getAmapId());

        // 来源
        List<PointSourceAddDTO> sourceAddDTOList = dto.getSource();
        addSource(sourceAddDTOList, point.getId());
    }

    private void addOrEditPointMap(Integer id, String amapId) {

        // 查询数据
        List<PointMap> pointMapList = pointMapService.lambdaQuery()
                .eq(PointMap::getPointId, id)
                .eq(PointMap::getState, StateEnum.ENABLE.getCode())
                .orderByDesc(PointMap::getId)
                .list();

        PointMap first = pointMapList.getFirst();

        // 未变动，编辑
        if (amapId.equals(first.getAmapId())) {
            pointMapList.removeFirst();
        }
        // 变动，删除
        pointMapService.lambdaUpdate()
                .in(PointMap::getPointId, pointMapList.stream().map(PointMap::getId).toList())
                .eq(PointMap::getState, StateEnum.ENABLE.getCode())
                .set(PointMap::getState, StateEnum.DELETE.getCode())
                .update();
        // 再新增
        PointMap pointMap = new PointMap();
        pointMap.setPointId(id);
        pointMap.setAmapId(amapId);

        // TODO 触发自动更新地图任务
    }

    private void addSource(List<? extends PointSourceAddDTO> sourceAddDTOList, Integer pointId) {
        if (CollectionUtils.isNotEmpty(sourceAddDTOList)) {
            List<PointSource> list = sourceAddDTOList.stream().map(source -> {
                PointSource pointSource = new PointSource();
                BeanUtils.populate(source, pointSource);
                pointSource.setPointId(pointId);
                return pointSource;
            }).toList();
            pointSourceService.saveBatch(list);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void edit(PointEditDTO dto) {

        lambdaUpdate()
                .set(null != dto.getPointType(), Point::getPointType, dto.getPointType())
                .set(StringUtils.isNotBlank(dto.getPointShortName()), Point::getPointShortName, dto.getPointShortName())
                .set(StringUtils.isNotBlank(dto.getPointFullName()), Point::getPointFullName, dto.getPointFullName())
                .set(StringUtils.isNotBlank(dto.getDescribe()), Point::getDescribe, dto.getDescribe())
                .set(StringUtils.isNotBlank(dto.getAddress()), Point::getAddress, dto.getAddress())
                .set(StringUtils.isNotBlank(dto.getLongitude()), Point::getLongitude, dto.getLongitude())
                .set(StringUtils.isNotBlank(dto.getLatitude()), Point::getLatitude, dto.getLatitude())
                .set(dto.getVisitedTimes() != null, Point::getVisitedTimes, dto.getVisitedTimes())
                .set(StringUtils.isNotBlank(dto.getAddressCode()), Point::getAddressCode, dto.getAddressCode())
                .set(null != dto.getState(), Point::getState, dto.getState())
                .set(null == dto.getState(), Point::getState, StateEnum.ENABLE)
                .eq(Point::getId, dto.getId())
                .update();

        // 地图
        addOrEditPointMap(dto.getId(), dto.getAmapId());

        // 来源
        List<PointSourceEditDTO> sourceList = dto.getSourceEdit();
        // 新增
        List<PointSourceEditDTO> addSourceList = sourceList.stream().filter(k -> StringUtils.isEmpty(k.getId())).toList();
        addSource(addSourceList, dto.getId());

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
}