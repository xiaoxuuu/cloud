package cc.xiaoxu.cloud.ai.task;

import cc.xiaoxu.cloud.ai.entity.Knowledge;
import cc.xiaoxu.cloud.ai.service.ALiYunService;
import cc.xiaoxu.cloud.ai.service.KnowledgeSectionService;
import cc.xiaoxu.cloud.ai.service.KnowledgeService;
import cc.xiaoxu.cloud.bean.ai.enums.ALiFileIndexResultEnum;
import cc.xiaoxu.cloud.bean.ai.enums.ALiFileUploadResultEnum;
import cc.xiaoxu.cloud.bean.ai.enums.FileStatusEnum;
import cc.xiaoxu.cloud.bean.ai.enums.KnowledgeTypeEnum;
import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.utils.set.ListUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ALiFileStatusCheckTask {

    private final ALiYunService aLiYunService;
    private final KnowledgeService knowledgeService;
    private final KnowledgeSectionService knowledgeSectionService;

    /**
     * 阿里文件上传状态检查
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void aLiFileUploadResultCheck() throws InterruptedException {

        List<Knowledge> knowledgeList = knowledgeService.lambdaQuery()
                .in(Knowledge::getStatus, List.of(ALiFileUploadResultEnum.INIT.getCode(), ALiFileUploadResultEnum.PARSING.getCode()))
                .eq(Knowledge::getType, KnowledgeTypeEnum.ALi_FILE.getCode())
                .list();
        List<List<Knowledge>> lists = ListUtils.splitList(knowledgeList, 5);
        for (List<Knowledge> list : lists) {
            for (Knowledge knowledge : list) {
                boolean succeeded = knowledgeService.updateFileUploadResult(knowledge);
                if (!succeeded) {
                    continue;
                }
                // 执行知识库索引
                String jobId = aLiYunService.submitIndexAddDocumentsJob(knowledge.getThreePartyFileId());
                knowledge.setStatus(ALiFileIndexResultEnum.RUNNING.getCode());
                knowledge.setThreePartyInfo(jobId);
                knowledgeService.updateById(knowledge);
            }
            Thread.sleep(1500);
        }
    }

    /**
     * 阿里文件切片状态检查
     */
    @Scheduled(cron = "15/45 * * * * ?")
    public void aLiFileIndexResultCheck() throws InterruptedException {

        List<Knowledge> knowledgeList = knowledgeService.lambdaQuery()
                .in(Knowledge::getStatus, List.of(ALiFileIndexResultEnum.PENDING.getCode(), ALiFileIndexResultEnum.RUNNING.getCode()))
                .eq(Knowledge::getType, KnowledgeTypeEnum.ALi_FILE.getCode())
                .isNotNull(Knowledge::getThreePartyInfo)
                .list();
        List<List<Knowledge>> lists = ListUtils.splitList(knowledgeList, 5);
        for (List<Knowledge> list : lists) {
            for (Knowledge knowledge : list) {
                boolean succeeded = knowledgeService.updateFileIndexResult(knowledge);
                if (!succeeded) {
                    continue;
                }

                knowledge.setStatus(FileStatusEnum.SECTION_READ.getCode());
                knowledgeService.updateById(knowledge);
                knowledgeSectionService.readALiSection(knowledge);

                knowledge.setStatus(FileStatusEnum.VECTOR_CALC.getCode());
                knowledgeService.updateById(knowledge);
                knowledgeSectionService.calcVector(new IdDTO(knowledge.getId()));

                knowledge.setState(StateEnum.ENABLE.getCode());
                knowledge.setStatus(FileStatusEnum.ALL_COMPLETED.getCode());
                knowledgeService.updateById(knowledge);
            }
            Thread.sleep(1500);
        }
    }
}