package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.bean.dto.PointSourceAddOrEditDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.my.dao.PointSourceMapper;
import cc.xiaoxu.cloud.my.entity.PointSource;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Getter
@Slf4j
@Service
public class PointSourceService extends ServiceImpl<PointSourceMapper, PointSource> {

    @Transactional(rollbackFor = Exception.class)
    public void addOrEdit(PointSourceAddOrEditDTO dto) {

        if (null != dto.getId()) {
            // 更新
            lambdaUpdate()
                    .eq(PointSource::getId, dto.getId())
                    .set(PointSource::getAuthorId, dto.getAuthorId())
                    .set(PointSource::getType, dto.getType())
                    .set(PointSource::getTitle, dto.getTitle())
                    .set(PointSource::getContent, dto.getContent())
                    .set(PointSource::getUrl, dto.getUrl())
                    .set(PointSource::getRemark, dto.getRemark())
                    .set(PointSource::getModifyTime, new Date())
                    .update();
        } else {
            PointSource entity = new PointSource();
            BeanUtils.populate(dto, entity);
            entity.setAuthorId(dto.getAuthorId());
            entity.setType(dto.getType());
            entity.setTitle(dto.getTitle());
            entity.setContent(dto.getContent());
            entity.setUrl(dto.getUrl());
            entity.setRemark(dto.getRemark());
            entity.setState(StateEnum.ENABLE.getCode());
            entity.setCreateTime(new Date());
            this.save(entity);
        }
    }
}