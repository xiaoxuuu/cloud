package cc.xiaoxu.cloud.ai.service;

import cc.xiaoxu.cloud.ai.dao.ConversationKnowledgeMapper;
import cc.xiaoxu.cloud.ai.entity.ConversationKnowledge;
import cc.xiaoxu.cloud.bean.ai.dto.ConversationEditKnowledgeBaseDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.bean.entity.BaseEntity;
import cc.xiaoxu.cloud.core.utils.DateUtils;
import cc.xiaoxu.cloud.core.utils.enums.EnumUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@AllArgsConstructor
public class ConversationKnowledgeService extends ServiceImpl<ConversationKnowledgeMapper, ConversationKnowledge> {

    @Transactional(rollbackFor = Exception.class)
    public void editKnowledge(ConversationEditKnowledgeBaseDTO dto, Integer userId) {

        if (EnumUtils.getByClass(dto.getState(), StateEnum.class) == StateEnum.DELETE) {
            lambdaUpdate()
                    .eq(ConversationKnowledge::getConversationId, dto.getId())
                    .eq(ConversationKnowledge::getKnowledgeBaseId, dto.getKnowledgeBaseId())
                    .set(ConversationKnowledge::getState, StateEnum.DELETE.getCode())
                    .set(ConversationKnowledge::getModifyId, userId)
                    .set(ConversationKnowledge::getModifyTime, DateUtils.getNowDate())
                    .update();
        }

        if (EnumUtils.getByClass(dto.getState(), StateEnum.class) == StateEnum.ENABLE) {

            ConversationKnowledge entity = new ConversationKnowledge();
            entity.setConversationId(dto.getId());
            entity.setKnowledgeBaseId(dto.getKnowledgeBaseId());
            BaseEntity.buildCreate(entity, userId);
            save(entity);
        }
    }
}