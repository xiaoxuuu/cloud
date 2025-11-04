package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.bean.dto.PointAddDTO;
import cc.xiaoxu.cloud.bean.dto.PointGetDTO;
import cc.xiaoxu.cloud.bean.vo.PointFullVO;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.my.dao.PointMapper;
import cc.xiaoxu.cloud.my.entity.Point;
import cc.xiaoxu.cloud.my.entity.PointTemp;
import cc.xiaoxu.cloud.my.manager.PointManager;
import cc.xiaoxu.cloud.my.utils.DistanceUtils;
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
    private final PointManager pointManager;

    @Transactional(rollbackFor = Exception.class)
    public void add(PointAddDTO dto) {

        // TODO 高德地图查询地点信息
        // TODO 构建地图数据
        // TODO 构建来源
    }

    public PointFullVO get(PointGetDTO dto) {

        PointTemp pointTemp = pointManager.getPointMap().get(Integer.parseInt(dto.getId()));

        if (null == pointTemp) {
            throw new CustomException("未查询到数据");
        }
        PointFullVO vo = new PointFullVO();
        BeanUtils.populate(pointTemp, vo);

        // 标签
        if (vo.getVisitedTimes() != null && vo.getVisitedTimes() > 0) {
            vo.setTagList(List.of("作者去过"));
        }

        // 距离
        if (StringUtils.isNotBlank(dto.getLatitude()) && StringUtils.isNotBlank(dto.getLongitude())) {
            double distance = DistanceUtils.calculateDistance(
                    Double.parseDouble(pointTemp.getLatitude()),
                    Double.parseDouble(pointTemp.getLongitude()),
                    Double.parseDouble(dto.getLatitude()),
                    Double.parseDouble(dto.getLongitude())
            );
            vo.setDistance(distance);
        }
        // 电话
        if (StringUtils.isNotBlank(pointTemp.getTelephone())) {
            vo.setTelList(Arrays.stream(pointTemp.getTelephone().split(",")).toList());
        }

        return vo;
    }

    public Integer countProgressing() {
        return baseMapper.countProgressing();
    }
}