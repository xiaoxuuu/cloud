package cc.xiaoxu.cloud.ai.service;

import cc.xiaoxu.cloud.ai.dao.KnowledgeMapper;
import cc.xiaoxu.cloud.ai.entity.Knowledge;
import cc.xiaoxu.cloud.bean.ai.enums.ALiFileIndexResultEnum;
import cc.xiaoxu.cloud.bean.ai.enums.ALiFileUploadResultEnum;
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

    public void addALiFile(String fileName, String fileId) {

        Knowledge knowledge = new Knowledge();
        knowledge.setType(KnowledgeTypeEnum.ALi_FILE.getCode());
        knowledge.setName(fileName);
        knowledge.setThreePartyFileId(fileId);
        knowledge.setStatus(ALiFileUploadResultEnum.INIT.getCode());
        knowledge.setState(StateEnum.ENABLE.getCode());
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
                    .set(Knowledge::getModifyTime, new Date())
                    .update();
            return resultEnum == ALiFileIndexResultEnum.COMPLETED;
        }
        return false;
    }
}