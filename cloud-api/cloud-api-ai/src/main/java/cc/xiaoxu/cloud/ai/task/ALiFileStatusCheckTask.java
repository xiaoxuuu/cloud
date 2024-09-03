package cc.xiaoxu.cloud.ai.task;

import cc.xiaoxu.cloud.ai.entity.Knowledge;
import cc.xiaoxu.cloud.ai.service.ALiYunService;
import cc.xiaoxu.cloud.ai.service.KnowledgeSectionService;
import cc.xiaoxu.cloud.ai.service.KnowledgeService;
import cc.xiaoxu.cloud.bean.ai.enums.ALiFileUploadResultEnum;
import cc.xiaoxu.cloud.bean.ai.enums.KnowledgeTypeEnum;
import cc.xiaoxu.cloud.bean.dto.IdDTO;
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

    @Scheduled(cron = "0/30 * * * * ?")
    public void aLiFileUploadResultCheck() {

        List<Knowledge> knowledgeList = knowledgeService.lambdaQuery()
                .in(Knowledge::getStatus, List.of(ALiFileUploadResultEnum.INIT.getCode(), ALiFileUploadResultEnum.PARSING.getCode()))
                .eq(Knowledge::getType, KnowledgeTypeEnum.ALi_FILE.getCode())
                .list();
        for (Knowledge knowledge : knowledgeList) {
            boolean succeeded = knowledgeService.updateFileUploadResult(knowledge);
            if (!succeeded) {
                continue;
            }
            // 添加知识库索引
            aLiYunService.submitIndexAddDocumentsJob(knowledge.getAdditionalInfo());

            knowledgeSectionService.rebuildSection(knowledge);
            knowledgeSectionService.calcVector(new IdDTO(String.valueOf(knowledge.getId())));
        }
    }
}