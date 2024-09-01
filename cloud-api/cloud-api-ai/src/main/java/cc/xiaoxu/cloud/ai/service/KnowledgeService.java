package cc.xiaoxu.cloud.ai.service;

import cc.xiaoxu.cloud.ai.dao.KnowledgeMapper;
import cc.xiaoxu.cloud.ai.entity.Knowledge;
import cc.xiaoxu.cloud.bean.ai.enums.ALiFileStatusEnum;
import cc.xiaoxu.cloud.bean.ai.enums.KnowledgeTypeEnum;
import com.aliyun.bailian20231229.models.DescribeFileResponseBody;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        save(knowledge);
        updateFileStatus(knowledge);
    }

    public void updateFileStatus(Knowledge knowledge) {

        String fileId = knowledge.getAdditionalInfo();
        Integer id = knowledge.getId();
        DescribeFileResponseBody.DescribeFileResponseBodyData describeFile = aLiYunService.describeFile(fileId);
        String status = describeFile.getStatus();
        log.info("文件 {}({}) 当前状态为：{}", fileId, id, status);
        if (ALiFileStatusEnum.PARSE_FAILED.getCode().equals(status) || ALiFileStatusEnum.PARSE_SUCCESS.getCode().equals(status)) {
            lambdaUpdate()
                    .eq(Knowledge::getId, id)
                    .eq(Knowledge::getAdditionalInfo, fileId)
                    .set(Knowledge::getStatus, status)
                    .update();
        }
    }
}