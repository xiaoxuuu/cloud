package cc.xiaoxu.cloud.ai.service;

import cc.xiaoxu.cloud.ai.dao.KnowledgeMapper;
import cc.xiaoxu.cloud.ai.entity.Knowledge;
import cc.xiaoxu.cloud.bean.ai.dto.*;
import cc.xiaoxu.cloud.bean.ai.enums.FileStatusEnum;
import cc.xiaoxu.cloud.bean.ai.enums.KnowledgeTypeEnum;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeExpandVO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.utils.PageUtils;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.core.utils.enums.EnumUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class KnowledgeService extends ServiceImpl<KnowledgeMapper, Knowledge> {

    private final ApplicationEventPublisher applicationEventPublisher;

    public Knowledge addKnowledge(KnowledgeAddFileDTO dto, String fileName, String fileId, Integer userId, KnowledgeTypeEnum knowledgeTypeEnum) {

        Knowledge knowledge = new Knowledge();
        knowledge.setKnowledgeBaseId(dto.getKnowledgeBaseId());
        knowledge.setUserId(userId);
        knowledge.setType(knowledgeTypeEnum.getCode());
        knowledge.setName(fileName);
        knowledge.setFileId(fileId);
        knowledge.setStatus(FileStatusEnum.UPLOAD_INIT.getCode());
        knowledge.setState(StateEnum.PROGRESSING.getCode());
        knowledge.setCreateTime(new Date());
        save(knowledge);
        return knowledge;
    }

    public void addTable(KnowledgeAddTableDTO dto, Integer userId) {

        Knowledge knowledge = new Knowledge();
        knowledge.setKnowledgeBaseId(dto.getKnowledgeBaseId());
        knowledge.setUserId(userId);
        knowledge.setType(KnowledgeTypeEnum.TABLE.getCode());
        knowledge.setName(dto.getTableName());
        knowledge.setFileInfo(dto.getSql());
        knowledge.setStatus(FileStatusEnum.SECTION_READ.getCode());
        knowledge.setState(StateEnum.PROGRESSING.getCode());
        knowledge.setCreateTime(new Date());
        save(knowledge);

        // 使用事件异步处理
        applicationEventPublisher.publishEvent(new KnowledgeAddTableEventDTO(dto.getTableName(), dto.getSql(), knowledge.getKnowledgeBaseId(), knowledge.getId(), userId));
    }

    public void addCustom(KnowledgeAddCustomDTO dto, Integer userId) {

        Knowledge knowledge = lambdaQuery()
                .eq(Knowledge::getUserId, userId)
                .eq(Knowledge::getType, KnowledgeTypeEnum.CUSTOM.getCode())
                .eq(Knowledge::getName, dto.getKnowledgeName())
                .one();
        if (null == knowledge) {
            knowledge = createCustomData(dto, userId);
        }

        // 切片数据
        applicationEventPublisher.publishEvent(new KnowledgeAddCustomEventDTO(dto.getContent(), knowledge.getKnowledgeBaseId(), knowledge.getId(), userId));
    }

    private Knowledge createCustomData(KnowledgeAddCustomDTO dto, Integer userId) {

        Knowledge knowledge = new Knowledge();
        knowledge.setKnowledgeBaseId(dto.getKnowledgeBaseId());
        knowledge.setUserId(userId);
        knowledge.setType(KnowledgeTypeEnum.CUSTOM.getCode());
        knowledge.setName(dto.getKnowledgeName());
        knowledge.setStatus(FileStatusEnum.SECTION_READ.getCode());
        knowledge.setState(StateEnum.PROGRESSING.getCode());
        knowledge.setCreateTime(new Date());
        save(knowledge);
        return knowledge;
    }

    public void changeStatus(Integer knowledgeId, FileStatusEnum fileStatusEnum) {
        lambdaUpdate()
                .eq(Knowledge::getId, knowledgeId)
                .set(Knowledge::getModifyTime, new Date())
                .set(Knowledge::getStatus, fileStatusEnum.getCode())
                .update();
    }

    public Page<KnowledgeExpandVO> pages(KnowledgePageDTO dto, Integer userId) {

        Page<Knowledge> entityPage = lambdaQuery()
                .eq(null != dto.getKnowledgeBaseId(), Knowledge::getKnowledgeBaseId, dto.getKnowledgeBaseId())
                .eq(StringUtils.isNotBlank(dto.getName()), Knowledge::getName, dto.getName())
                .eq(Knowledge::getUserId, userId)
                .ne(Knowledge::getState, StateEnum.DELETE.getCode())
                .page(PageUtils.getPageCondition(dto));

        Page<KnowledgeExpandVO> page = PageUtils.getPage(entityPage, KnowledgeExpandVO.class);
        List<KnowledgeExpandVO> list = page.getRecords();
        addExpandInfo(list);
        return page;
    }

    private static void addExpandInfo(List<KnowledgeExpandVO> list) {
        for (KnowledgeExpandVO record : list) {
            record.setTypeName(EnumUtils.getByClass(record.getType(), KnowledgeTypeEnum.class).getIntroduction());
            if (EnumUtils.getByClass(record.getState(), StateEnum.class) == StateEnum.PROGRESSING) {
                record.setStatusName(EnumUtils.getByClass(record.getStatus(), FileStatusEnum.class).getIntroduction());
            } else {
                record.setStatusName(EnumUtils.getByClass(record.getState(), StateEnum.class).getIntroduction());
            }
        }
    }

    public List<KnowledgeExpandVO> lists(KnowledgeListDTO dto, Integer userId) {

        List<Knowledge> knowledgeList = lambdaQuery()
                .eq(null != dto.getKnowledgeBaseId(), Knowledge::getKnowledgeBaseId, dto.getKnowledgeBaseId())
                .eq(Knowledge::getUserId, userId)
                .eq(Knowledge::getState, StateEnum.ENABLE.getCode())
                .eq(Knowledge::getStatus, FileStatusEnum.ALL_COMPLETED.getCode())
                .orderByDesc(Knowledge::getId)
                .list();

        List<KnowledgeExpandVO> list = new ArrayList<>();
        BeanUtils.populateList(knowledgeList, list, KnowledgeExpandVO.class);
        addExpandInfo(list);
        return list;
    }
}