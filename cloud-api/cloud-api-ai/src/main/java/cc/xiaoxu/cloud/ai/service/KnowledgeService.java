package cc.xiaoxu.cloud.ai.service;

import cc.xiaoxu.cloud.ai.dao.KnowledgeMapper;
import cc.xiaoxu.cloud.ai.entity.Knowledge;
import cc.xiaoxu.cloud.bean.ai.dto.*;
import cc.xiaoxu.cloud.bean.ai.enums.ALiFileIndexResultEnum;
import cc.xiaoxu.cloud.bean.ai.enums.ALiFileUploadResultEnum;
import cc.xiaoxu.cloud.bean.ai.enums.FileStatusEnum;
import cc.xiaoxu.cloud.bean.ai.enums.KnowledgeTypeEnum;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeExpandVO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.bean.dto.PageDTO;
import cc.xiaoxu.cloud.core.utils.PageUtils;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.core.utils.enums.EnumUtils;
import com.aliyun.bailian20231229.models.DescribeFileResponseBody;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class KnowledgeService extends ServiceImpl<KnowledgeMapper, Knowledge> {

    private final ALiYunService aLiYunService;
    private final ApplicationEventPublisher applicationEventPublisher;

    public void addALiFile(String fileName, String fileId, String tenant) {

        Knowledge knowledge = new Knowledge();
        knowledge.setTenant(tenant);
        knowledge.setType(KnowledgeTypeEnum.ALi_FILE.getCode());
        knowledge.setName(fileName);
        knowledge.setThreePartyFileId(fileId);
        knowledge.setStatus(ALiFileUploadResultEnum.INIT.getCode());
        knowledge.setState(StateEnum.PROGRESSING.getCode());
        knowledge.setCreateTime(new Date());
        save(knowledge);
        updateFileUploadResult(knowledge);
    }

    public boolean updateFileUploadResult(Knowledge knowledge) {

        String fileId = knowledge.getThreePartyFileId();
        Integer id = knowledge.getId();
        DescribeFileResponseBody.DescribeFileResponseBodyData describeFile = aLiYunService.describeFile(fileId);
        String status = describeFile.getStatus();
        log.info("文件上传 {}({}) 当前状态为：{}", fileId, id, status);
        Set<ALiFileUploadResultEnum> statusSet = Set.of(ALiFileUploadResultEnum.PARSING, ALiFileUploadResultEnum.PARSE_FAILED, ALiFileUploadResultEnum.PARSE_SUCCESS);
        ALiFileUploadResultEnum statusEnum = EnumUtils.getByClass(status, ALiFileUploadResultEnum.class);
        if (statusSet.contains(statusEnum)) {
            if (ALiFileUploadResultEnum.PARSING == statusEnum) {
                return false;
            }
            lambdaUpdate()
                    .eq(Knowledge::getId, id)
                    .eq(Knowledge::getThreePartyFileId, fileId)
                    .set(Knowledge::getStatus, statusEnum.getCode())
                    .set(Knowledge::getModifyTime, new Date())
                    .update();
            return statusEnum == ALiFileUploadResultEnum.PARSE_SUCCESS;
        }
        return false;
    }

    public boolean updateFileIndexResult(Knowledge knowledge) {

        String fileId = knowledge.getThreePartyFileId();
        Integer id = knowledge.getId();
        String status = aLiYunService.getIndexJobStatus(knowledge.getThreePartyInfo());
        log.info("文件切片 {}({}) 当前状态为：{}", fileId, id, status);
        Set<ALiFileIndexResultEnum> resultSet = Set.of(ALiFileIndexResultEnum.RUNNING, ALiFileIndexResultEnum.COMPLETED, ALiFileIndexResultEnum.FAILED);
        ALiFileIndexResultEnum resultEnum = EnumUtils.getByClass(status, ALiFileIndexResultEnum.class);
        if (resultSet.contains(resultEnum)) {
            if (ALiFileIndexResultEnum.RUNNING == resultEnum) {
                return false;
            }
            lambdaUpdate()
                    .eq(Knowledge::getId, id)
                    .eq(Knowledge::getThreePartyFileId, fileId)
                    .set(Knowledge::getStatus, resultEnum.getCode())
                    .set(Knowledge::getState, StateEnum.ENABLE.getCode())
                    .set(Knowledge::getModifyTime, new Date())
                    .update();
            return resultEnum == ALiFileIndexResultEnum.COMPLETED;
        }
        return false;
    }


    public void addTable(KnowledgeAddTableDTO dto, String tenant) {

        Knowledge knowledge = new Knowledge();
        knowledge.setTenant(tenant);
        knowledge.setType(KnowledgeTypeEnum.TABLE.getCode());
        knowledge.setName(dto.getTableName());
        knowledge.setThreePartyInfo(dto.getSql());
        knowledge.setStatus(FileStatusEnum.SECTION_READ.getCode());
        knowledge.setState(StateEnum.PROGRESSING.getCode());
        knowledge.setCreateTime(new Date());
        save(knowledge);

        // 使用事件异步处理
        applicationEventPublisher.publishEvent(new KnowledgeAddTableEventDTO(dto.getTableName(), dto.getSql(), knowledge.getId()));
    }

    public void addCustom(KnowledgeAddCustomDTO dto, String tenant) {

        Knowledge knowledge = lambdaQuery()
                .eq(Knowledge::getTenant, tenant)
                .eq(Knowledge::getType, KnowledgeTypeEnum.CUSTOM.getCode())
                .eq(Knowledge::getName, dto.getKnowledgeName())
                .one();
        if (null == knowledge) {
            knowledge = createCustomData(dto, tenant);
        }

        // 切片数据
        applicationEventPublisher.publishEvent(new KnowledgeAddCustomEventDTO(dto.getContent(), knowledge.getId()));
    }

    private Knowledge createCustomData(KnowledgeAddCustomDTO dto, String tenant) {

        Knowledge knowledge = new Knowledge();
        knowledge.setTenant(tenant);
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

    public void editState(KnowledgeEditStateDTO dto, String tenant) {

        lambdaUpdate()
                .eq(Knowledge::getTenant, tenant)
                .in(Knowledge::getId, dto.getIdList())
                .set(Knowledge::getState, dto.getState())
                .update();
    }

    public Page<KnowledgeExpandVO> pages(PageDTO dto, String tenant) {

        Page<Knowledge> entityPage = lambdaQuery()
                .eq(Knowledge::getTenant, tenant)
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

    public List<KnowledgeExpandVO> lists(String tenant) {

        List<Knowledge> knowledgeList = lambdaQuery()
                .eq(Knowledge::getTenant, tenant)
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