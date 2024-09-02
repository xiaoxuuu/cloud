package cc.xiaoxu.cloud.ai.service;

import cc.xiaoxu.cloud.ai.dao.KnowledgeMapper;
import cc.xiaoxu.cloud.ai.entity.Knowledge;
import cc.xiaoxu.cloud.bean.ai.enums.ALiFileStatusEnum;
import cc.xiaoxu.cloud.bean.ai.enums.KnowledgeTypeEnum;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.utils.enums.EnumUtils;
import com.aliyun.bailian20231229.models.DescribeFileResponseBody;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class KnowledgeService extends ServiceImpl<KnowledgeMapper, Knowledge> {

    private final ALiYunService aLiYunService;

    public void addFile(String fileName, String fileId) {

        Knowledge knowledge = new Knowledge();
        knowledge.setType(KnowledgeTypeEnum.ALi_FILE.getCode());
        knowledge.setName(fileName);
        knowledge.setAdditionalInfo(fileId);
        knowledge.setStatus(ALiFileStatusEnum.INIT.getCode());
        knowledge.setState(StateEnum.ENABLE.getCode());
        knowledge.setCreateTime(new Date());
        save(knowledge);
        updateFileStatus(knowledge);
    }

    public void updateFileStatus(Knowledge knowledge) {

        String fileId = knowledge.getAdditionalInfo();
        Integer id = knowledge.getId();
        DescribeFileResponseBody.DescribeFileResponseBodyData describeFile = aLiYunService.describeFile(fileId);
        String status = describeFile.getStatus();
        log.info("文件 {}({}) 当前状态为：{}", fileId, id, status);
        Set<ALiFileStatusEnum> statusSet = Set.of(ALiFileStatusEnum.PARSE_FAILED, ALiFileStatusEnum.PARSE_SUCCESS);
        ALiFileStatusEnum statusEnum = EnumUtils.getByClass(status, ALiFileStatusEnum.class);
        if (statusSet.contains(statusEnum)) {
            lambdaUpdate()
                    .eq(Knowledge::getId, id)
                    .eq(Knowledge::getAdditionalInfo, fileId)
                    .set(Knowledge::getStatus, statusEnum.getCode())
                    .set(Knowledge::getModifyTime, new Date())
                    .update();
        }
    }
}