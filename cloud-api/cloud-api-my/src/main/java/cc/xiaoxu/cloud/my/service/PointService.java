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

    private final PointSourceService pointSourceService;

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
        List<Integer> idList = sourceList.stream().map(PointSource::getPointId).distinct().map(Integer::parseInt).toList();

        // 搜索
        List<Point> pointList = lambdaQuery()
                .and(wrapper -> wrapper.or(orWrapper -> orWrapper
                        .like(Point::getPointFullName, dto.getPointName())
                        .or().like(Point::getPointFullName, dto.getPointName())
                        .or().like(Point::getDescribe, dto.getPointName())
                        .or().like(Point::getAddress, dto.getPointName())
                        .or().like(Point::getLongitude, dto.getPointName())
                        .or().like(Point::getLatitude, dto.getPointName())
                        .or().in(Point::getId, idList)
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
            return vo;
        }).toList();
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
        return vo;
    }

    public Integer countProgressing() {
        return baseMapper.countProgressing();
    }
}