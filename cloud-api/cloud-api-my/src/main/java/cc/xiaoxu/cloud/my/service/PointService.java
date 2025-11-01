package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.bean.dto.PointAddDTO;
import cc.xiaoxu.cloud.bean.vo.PointFullVO;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.my.dao.PointMapper;
import cc.xiaoxu.cloud.my.entity.Point;
import cc.xiaoxu.cloud.my.entity.PointTemp;
import cc.xiaoxu.cloud.my.manager.PointManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public PointFullVO get(IdDTO dto) {

        PointTemp pointTemp = pointManager.getPointMap().get(Integer.parseInt(dto.getId()));

        if (null == pointTemp) {
            throw new CustomException("未查询到数据");
        }
        PointFullVO vo = new PointFullVO();
        BeanUtils.populate(pointTemp, vo);

        // TODO 标签

        return vo;
    }

    public Integer countProgressing() {
        return baseMapper.countProgressing();
    }
}