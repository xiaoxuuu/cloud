package cc.xiaoxu.cloud.ai.service;

import cc.xiaoxu.cloud.ai.dao.KnowledgeBaseMapper;
import cc.xiaoxu.cloud.ai.entity.KnowledgeBase;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeBaseAddDTO;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeBaseEditDTO;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeBasePageDTO;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeBaseVO;
import cc.xiaoxu.cloud.bean.dto.IdsDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.utils.PageUtils;
import cc.xiaoxu.cloud.core.utils.text.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class KnowledgeBaseService extends ServiceImpl<KnowledgeBaseMapper, KnowledgeBase> {

    public void add(KnowledgeBaseAddDTO dto, String userId) {

        KnowledgeBase entity = new KnowledgeBase();
        entity.setUserId(userId);
        entity.setName(dto.getName());
        save(entity);
    }

    public void edit(KnowledgeBaseEditDTO dto, String userId) {

        KnowledgeBase entity = lambdaQuery()
                .eq(KnowledgeBase::getId, dto.getId())
                .eq(KnowledgeBase::getUserId, userId)
                .one();
        entity.setName(dto.getName());
        updateById(entity);
    }

    public void del(IdsDTO dto, String userId) {

        lambdaUpdate()
                .eq(KnowledgeBase::getUserId, userId)
                .in(KnowledgeBase::getId, dto.getIdList())
                .set(KnowledgeBase::getState, StateEnum.DELETE.getCode())
                .update();
    }

    public Page<KnowledgeBaseVO> pages(KnowledgeBasePageDTO dto, String userId) {

        Page<KnowledgeBase> entityPage = lambdaQuery()
                .eq(KnowledgeBase::getUserId, userId)
                .ne(KnowledgeBase::getState, StateEnum.DELETE.getCode())
                .like(!StringUtils.isBlank(dto.getName()), KnowledgeBase::getName, dto.getName())
                .page(PageUtils.getPageCondition(dto));

        return PageUtils.getPage(entityPage, KnowledgeBaseVO.class);
    }
}