package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.bean.dto.PointAddOrEditDTO;
import cc.xiaoxu.cloud.bean.dto.PointGetDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.bean.vo.PointFullVO;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.core.exception.CustomShowException;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.core.utils.text.MD5Utils;
import cc.xiaoxu.cloud.my.dao.PointMapper;
import cc.xiaoxu.cloud.my.entity.Point;
import cc.xiaoxu.cloud.my.manager.CacheManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Slf4j
@Service
@AllArgsConstructor
public class PointService extends ServiceImpl<PointMapper, Point> {

    private final PointSourceService pointSourceService;
    private final PointMapService pointMapService;
    private final CacheManager cacheManager;

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
                    .set(Point::getAmapId, dto.getAmapId())
                    .set(Point::getTagIdList, dto.getTagIdList())
                    .set(Point::getSourceIdList, dto.getSourceIdList())
                    .set(Point::getModifyTime, new Date())
                    .update();
        } else {
            Point point = new Point();
            BeanUtils.populate(dto, point);
            point.setTagIdList(dto.getTagIdList());
            point.setCode(getCode());
            point.setState(StateEnum.ENABLE.getCode());
            point.setCreateTime(new Date());
            this.save(point);
        }
    }

    private String getCode() {

        for (int i = 0; i < 3; i++) {
            String code = MD5Utils.toMd5(ThreadLocalRandom.current().nextInt(100000000, 999999999 + 1) + "-" + System.currentTimeMillis() + "-" + LocalDateTime.now());
            if (lambdaQuery().eq(Point::getCode, code).exists()) {
                continue;
            }
            return code;
        }
        throw new CustomShowException("code 重复");
    }

    public PointFullVO get(PointGetDTO dto) {

        PointFullVO vo = cacheManager.getPointMapCode().get(dto.getCode());

        if (null == vo) {
            throw new CustomException("未查询到数据");
        }
        return vo;
    }

    public Integer countProgressing() {
        return baseMapper.countProgressing();
    }
}