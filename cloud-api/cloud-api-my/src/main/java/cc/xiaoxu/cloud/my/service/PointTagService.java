package cc.xiaoxu.cloud.my.service;

import cc.xiaoxu.cloud.bean.dto.PointTagAddOrEditDTO;
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

        PointTag entity = new PointTag();
        BeanUtils.populate(dto, entity);
        entity.setState(dto.getState());
        entity.setCreateTime(new Date());
        if (null != dto.getId()) {
            entity.setModifyTime(new Date());
        }
        this.saveOrUpdate(entity);
    }
}