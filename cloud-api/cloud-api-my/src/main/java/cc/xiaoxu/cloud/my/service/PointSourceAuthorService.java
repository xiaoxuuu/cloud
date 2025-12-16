package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.bean.dto.PointSourceAuthorAddOrEditDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.my.dao.PointSourceAuthorMapper;
import cc.xiaoxu.cloud.my.entity.PointSourceAuthor;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Getter
@Slf4j
@Service
@AllArgsConstructor
public class PointSourceAuthorService extends ServiceImpl<PointSourceAuthorMapper, PointSourceAuthor> {

    @Transactional(rollbackFor = Exception.class)
    public void addOrEdit(PointSourceAuthorAddOrEditDTO dto) {

        if (null != dto.getId()) {
            // 更新
            lambdaUpdate()
                    .eq(PointSourceAuthor::getId, dto.getId())
                    .set(PointSourceAuthor::getName, dto.getName())
                    .set(PointSourceAuthor::getTiktokUrl, dto.getTiktokUrl())
                    .set(PointSourceAuthor::getRedbookUrl, dto.getRedbookUrl())
                    .set(PointSourceAuthor::getBilibiliUrl, dto.getBilibiliUrl())
                    .set(PointSourceAuthor::getContent, dto.getContent())
//                    .set(PointSourceAuthor::getState, dto.getState())
                    .set(PointSourceAuthor::getRemark, dto.getRemark())
                    .set(PointSourceAuthor::getModifyTime, new Date())
                    .update();
        } else {
            PointSourceAuthor entity = new PointSourceAuthor();
            BeanUtils.populate(dto, entity);
            entity.setName(dto.getName());
            entity.setTiktokUrl(dto.getTiktokUrl());
            entity.setRedbookUrl(dto.getRedbookUrl());
            entity.setBilibiliUrl(dto.getBilibiliUrl());
            entity.setContent(dto.getContent());
            entity.setRemark(dto.getRemark());
            entity.setState(StateEnum.ENABLE.getCode());
            entity.setCreateTime(new Date());
            this.save(entity);
        }
    }
}