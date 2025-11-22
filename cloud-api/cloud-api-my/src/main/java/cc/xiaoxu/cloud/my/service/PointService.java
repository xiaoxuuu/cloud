package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.bean.dto.PointAddOrEditDTO;
import cc.xiaoxu.cloud.bean.dto.PointGetDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.bean.vo.PointFullVO;
import cc.xiaoxu.cloud.bean.vo.PointTagVO;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.core.utils.text.MD5Utils;
import cc.xiaoxu.cloud.my.dao.PointMapper;
import cc.xiaoxu.cloud.my.entity.Point;
import cc.xiaoxu.cloud.my.entity.PointTag;
import cc.xiaoxu.cloud.my.entity.PointTemp;
import cc.xiaoxu.cloud.my.manager.PointManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Slf4j
@Service
@AllArgsConstructor
public class PointService extends ServiceImpl<PointMapper, Point> {

    private final PointSourceService pointSourceService;
    private final PointMapService pointMapService;
    private final PointManager pointManager;

    @Transactional(rollbackFor = Exception.class)
    public void addOrEdit(PointAddOrEditDTO dto) {

        if (null != dto.getCode()) {
            // 更新
            lambdaUpdate()
                    .eq(Point::getCode, dto.getCode())
                    .set(Point::getPointType, dto.getPointType())
                    .set(Point::getPointShortName, dto.getPointShortName())
                    .set(Point::getPointFullName, dto.getPointFullName())
                    .set(Point::getDescribe, dto.getDescribe())
                    .set(Point::getAddress, dto.getAddress())
                    .set(Point::getLongitude, dto.getLongitude())
                    .set(Point::getLatitude, dto.getLatitude())
                    .set(Point::getParentId, dto.getParentId())
                    .set(Point::getPhoto, dto.getPhoto())
                    .set(Point::getVisitedTimes, dto.getVisitedTimes())
                    .set(Point::getAddressCode, dto.getAddressCode())
                    .set(Point::getOperatingStatus, dto.getOperatingStatus())
                    .set(Point::getRecommendedDistance, dto.getRecommendedDistance())
                    .set(Point::getOpeningHours, dto.getOpeningHours())
                    .set(Point::getTelephone, dto.getTelephone())
                    .set(Point::getCost, dto.getCost())
                    .set(Point::getRemark, dto.getRemark())
                    .set(Point::getTagIdList, dto.getTagList().stream().distinct().collect(Collectors.joining(",")))
                    .set(Point::getModifyTime, new Date())
                    .update();
        } else {
            Point point = new Point();
            BeanUtils.populate(dto, point);
            point.setTagIdList(dto.getTagList().stream().distinct().collect(Collectors.joining(",")));
            point.setCode(MD5Utils.toMd5(dto.getPointFullName() + dto.getAddress() + new Random().nextInt()).toLowerCase());
            point.setState(StateEnum.ENABLE.getCode());
            point.setCreateTime(new Date());
            this.save(point);
        }
    }

    public PointFullVO get(PointGetDTO dto) {

        PointTemp pointTemp = pointManager.getPointMapCode().get(dto.getCode());

        if (null == pointTemp) {
            throw new CustomException("未查询到数据");
        }
        PointFullVO vo = new PointFullVO();
        BeanUtils.populate(pointTemp, vo);

        // 标签
        if (StringUtils.isNotBlank(vo.getTagIdList())) {
            List<PointTagVO> list = Arrays.stream(vo.getTagIdList().split(","))
                    .map(k -> pointManager.getPointTagMap().get(Integer.parseInt(k)))
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(PointTag::getId))
                    .map(this::toPointTagVO)
                    .toList();
            vo.setTagList(list);
        }

        // 电话
        if (StringUtils.isNotBlank(pointTemp.getTelephone())) {
            vo.setTelList(Arrays.stream(pointTemp.getTelephone().split(",")).toList());
        }

        return vo;
    }

    private PointTagVO toPointTagVO(PointTag entity) {
        PointTagVO vo = new PointTagVO();
        vo.setId(entity.getId());
        vo.setColor(entity.getColor());
        vo.setTagName(entity.getTagName());
        return vo;
    }

    public Integer countProgressing() {
        return baseMapper.countProgressing();
    }
}