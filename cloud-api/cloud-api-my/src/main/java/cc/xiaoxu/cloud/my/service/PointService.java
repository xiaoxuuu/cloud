package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.bean.dto.*;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.bean.vo.PointFullVO;
import cc.xiaoxu.cloud.bean.vo.PointSimpleVO;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.my.dao.PointMapper;
import cc.xiaoxu.cloud.my.dao.PointSourceMapper;
import cc.xiaoxu.cloud.my.entity.Point;
import cc.xiaoxu.cloud.my.entity.PointSource;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Slf4j
@Service
@AllArgsConstructor
public class PointService extends ServiceImpl<PointMapper, Point> {

    private final PointSourceMapper pointSourceMapper;

    public void add(PointAddDTO dto) {

        Point point = new Point();
        BeanUtils.populate(dto, point);
        point.setState(StateEnum.ENABLE.getCode());
        save(point);

        List<PointSourceAddDTO> sourceAddDTOList = dto.getSource();
        addSource(sourceAddDTOList);
    }

    private void addSource(List<PointSourceAddDTO> sourceAddDTOList) {
        if (CollectionUtils.isNotEmpty(sourceAddDTOList)) {
            List<PointSource> list = sourceAddDTOList.stream().map(source -> {
                PointSource pointSource = new PointSource();
                BeanUtils.populate(source, pointSource);
                return pointSource;
            }).toList();
            for (PointSource pointSource : list) {
                pointSourceMapper.insert(pointSource);
            }
        }
    }

    public void edit(PointEditDTO dto) {

        // TODO 编辑来源

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
                .eq(Point::getId, dto.getId())
                .update();
    }

    public List<? extends PointSimpleVO> lists(PointSearchDTO dto) {

        // 来源搜索
        List<Point> pointList = lambdaQuery()
                .and(wrapper -> wrapper.or(orWrapper -> orWrapper
                        .like(Point::getPointFullName, dto.getPointName())
                        .or().like(Point::getPointFullName, dto.getPointName())
                        .or().like(Point::getDescribe, dto.getPointName())
                        .or().like(Point::getAddress, dto.getPointName())
                        .or().like(Point::getLongitude, dto.getPointName())
                        .or().like(Point::getLatitude, dto.getPointName())
                ))
                .in(CollectionUtils.isNotEmpty(dto.getPointType()), Point::getPointType, dto.getPointType())
                .in(CollectionUtils.isNotEmpty(dto.getStateList()), Point::getState, dto.getStateList())
                .ge(null != dto.getVisit() && dto.getVisit(), Point::getVisitedTimes, 1)
                // 异常数据排除
                .isNotNull(Point::getLongitude)
                .isNotNull(Point::getLatitude)
                // 删除数据排除
                .ne(Point::getState, StateEnum.DELETE.getCode())
                .list();

        return pointList.stream().map(k -> {
            PointSimpleVO vo = new PointSimpleVO();
            BeanUtils.populate(k, vo);
            // TODO 排序
            return vo;
        }).toList();
    }

    private void handle(PointSearchDTO dto, List<? extends PointSimpleVO> pointVOList) {

    }

    public PointFullVO get(IdDTO dto) {

        // TODO 来源展示
        Point point;
        if (dto.getId() < 0) {
            point = lambdaQuery()
                    .eq(Point::getState, StateEnum.PROGRESSING)
                    .last(" LIMIT 1 ")
                    .one();
        } else {
            point = getById(dto.getId());
        }
        if (null == point) {
            throw new CustomException("无数据");
        }
        PointFullVO vo = new PointFullVO();
        BeanUtils.populate(point, vo);
        return vo;
    }

    public Integer countProgressing() {
        return baseMapper.countProgressing();
    }
}