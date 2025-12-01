package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.bean.dto.PointTagAddOrEditDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.my.dao.PointTagMapper;
import cc.xiaoxu.cloud.my.entity.PointTag;
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
public class PointTagService extends ServiceImpl<PointTagMapper, PointTag> {

    @Transactional(rollbackFor = Exception.class)
    public void addOrEdit(PointTagAddOrEditDTO dto) {

        if (null != dto.getId()) {
            // 更新
            lambdaUpdate()
                    .eq(PointTag::getId, dto.getId())
                    .set(PointTag::getTagName, dto.getTagName())
                    .set(PointTag::getColor, dto.getColor())
                    .set(PointTag::getCategory, dto.getCategory())
                    .set(PointTag::getSort, dto.getSort())
//                    .set(StringUtils.isNotBlank(dto.getState()), PointTag::getState, dto.getState())
                    .set(PointTag::getModifyTime, new Date())
                    .update();
        } else {
            PointTag entity = new PointTag();
            BeanUtils.populate(dto, entity);
            entity.setTagName(dto.getTagName());
            entity.setColor(dto.getColor());
            entity.setCategory(dto.getCategory());
            entity.setSort(dto.getSort());
            entity.setState(StateEnum.ENABLE.getCode());
            entity.setCreateTime(new Date());
            this.save(entity);
        }
    }
}