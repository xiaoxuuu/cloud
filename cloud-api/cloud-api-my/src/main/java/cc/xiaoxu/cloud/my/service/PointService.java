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
import cc.xiaoxu.cloud.my.entity.PointSource;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        return vo;
    }

    public Integer countProgressing() {
        return baseMapper.countProgressing();
    }
}