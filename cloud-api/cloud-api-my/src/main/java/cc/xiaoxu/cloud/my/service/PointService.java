package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.bean.dto.PointAddDTO;
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

        // TODO 高德地图查询地点信息
        // TODO 构建地图数据
        // TODO 构建来源
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