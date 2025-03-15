package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.bean.dto.PointAddDTO;
import cc.xiaoxu.cloud.bean.dto.PointSearchDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.bean.vo.PointFullVO;
import cc.xiaoxu.cloud.bean.vo.PointSimpleVO;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.core.utils.DateUtils;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.my.dao.PointMapper;
import cc.xiaoxu.cloud.my.entity.Point;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Slf4j
@Service
public class PointService extends ServiceImpl<PointMapper, Point> {

    public void add(PointAddDTO dto) {

        Point point = new Point();
        BeanUtils.populate(dto, point);
        point.setAmapUpdateTime(DateUtils.getNowDate());
        save(point);
    }

    public List<? extends PointSimpleVO> lists(PointSearchDTO dto) {

        List<Point> pointList = lambdaQuery()
                .like(StringUtils.isNotBlank(dto.getPointName()), Point::getPointName, dto.getPointName())
                .in(CollectionUtils.isNotEmpty(dto.getPointType()), Point::getPointType, dto.getPointType())
                .in(CollectionUtils.isNotEmpty(dto.getStateList()), Point::getState, dto.getStateList())
                // 异常数据排除
                .isNotNull(Point::getLongitude)
                .isNotNull(Point::getLatitude)
                // 删除数据排除
                .ne(Point::getState, StateEnum.DELETE.getCode())
                .list();

        return BeanUtils.populateList(pointList, PointSimpleVO.class);
    }

    private void handle(PointSearchDTO dto, List<? extends PointSimpleVO> pointVOList) {

    }

    public PointFullVO get(IdDTO dto) {

        Point point = getById(dto.getId());
        if (null == point) {
            throw new CustomException("无数据");
        }
        PointFullVO vo = new PointFullVO();
        BeanUtils.populate(point, vo);
        return vo;
    }
}