package cc.xiaoxu.cloud.ai.service;

import cc.xiaoxu.cloud.ai.dao.KnowledgeBaseMapper;
import cc.xiaoxu.cloud.ai.entity.KnowledgeBase;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeBaseAddDTO;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeBaseEditDTO;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeBasePageDTO;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeBaseVO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.bean.entity.BaseEntityForPostgres;
import cc.xiaoxu.cloud.core.utils.DateUtils;
import cc.xiaoxu.cloud.core.utils.PageUtils;
import cc.xiaoxu.cloud.core.utils.text.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class KnowledgeBaseService extends ServiceImpl<KnowledgeBaseMapper, KnowledgeBase> {

    public void add(KnowledgeBaseAddDTO dto, Integer userId) {

        KnowledgeBase entity = getKnowledgeBase(dto, userId);
        save(entity);
    }

    @NotNull
    private static KnowledgeBase getKnowledgeBase(KnowledgeBaseAddDTO dto, Integer userId) {
        KnowledgeBase entity = new KnowledgeBase();
        entity.setUserId(userId);
        entity.setName(dto.getName());
        BaseEntityForPostgres.buildCreate(entity, userId);
        return entity;
    }

    public void edit(KnowledgeBaseEditDTO dto, Integer userId) {

        lambdaUpdate()
                .eq(KnowledgeBase::getUserId, userId)
                .in(KnowledgeBase::getId, dto.getId())
                .set(KnowledgeBase::getName, dto.getName())
                .set(KnowledgeBase::getModifyId, userId)
                .set(KnowledgeBase::getModifyTime, DateUtils.getNowDate())
                .update();
    }

    public Page<KnowledgeBaseVO> pages(KnowledgeBasePageDTO dto, Integer userId) {

        Page<KnowledgeBase> entityPage = lambdaQuery()
                .eq(KnowledgeBase::getUserId, userId)
                .ne(KnowledgeBase::getState, StateEnum.DELETE.getCode())
                .like(!StringUtils.isBlank(dto.getName()), KnowledgeBase::getName, dto.getName())
                .page(PageUtils.getPageCondition(dto));

        return PageUtils.getPage(entityPage, KnowledgeBaseVO.class);
    }
}