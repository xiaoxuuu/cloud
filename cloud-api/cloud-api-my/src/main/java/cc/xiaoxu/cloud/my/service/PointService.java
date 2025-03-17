package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.bean.dto.PointAddDTO;
import cc.xiaoxu.cloud.bean.dto.PointEditDTO;
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
        point.setState(StateEnum.ENABLE.getCode());
        save(point);
    }

    public void edit(PointEditDTO dto) {

        lambdaUpdate()
                .set(null != dto.getPointType(), Point::getPointType, dto.getPointType())
                .set(StringUtils.isNotBlank(dto.getPointName()), Point::getPointName, dto.getPointName())
                .set(StringUtils.isNotBlank(dto.getDescribe()), Point::getDescribe, dto.getDescribe())
                .set(StringUtils.isNotBlank(dto.getAddress()), Point::getAddress, dto.getAddress())
                .set(StringUtils.isNotBlank(dto.getLongitude()), Point::getLongitude, dto.getLongitude())
                .set(StringUtils.isNotBlank(dto.getLatitude()), Point::getLatitude, dto.getLatitude())
                .set(dto.getCollectTimes() != null, Point::getCollectTimes, dto.getCollectTimes())
                .set(dto.getVisitedTimes() != null, Point::getVisitedTimes, dto.getVisitedTimes())
                .set(StringUtils.isNotBlank(dto.getSource()), Point::getSource, dto.getSource())
                .set(StringUtils.isNotBlank(dto.getAddressCode()), Point::getAddressCode, dto.getAddressCode())
                .set(StringUtils.isNotBlank(dto.getAmapWia()), Point::getAmapWia, dto.getAmapWia())
                .set(StringUtils.isNotBlank(dto.getAmapTag()), Point::getAmapTag, dto.getAmapTag())
                .set(StringUtils.isNotBlank(dto.getAmapRating()), Point::getAmapRating, dto.getAmapRating())
                .set(StringUtils.isNotBlank(dto.getAmapCost()), Point::getAmapCost, dto.getAmapCost())
                .set(StringUtils.isNotBlank(dto.getAmapPoiId()), Point::getAmapPoiId, dto.getAmapPoiId())
                .set(Point::getAmapUpdateTime, DateUtils.getNowDate())
                .set(Point::getState, StateEnum.ENABLE.getCode())
                .eq(Point::getId, dto.getId())
                .update();
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