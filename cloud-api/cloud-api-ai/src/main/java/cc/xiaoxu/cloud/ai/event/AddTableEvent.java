package cc.xiaoxu.cloud.ai.event;

import cc.xiaoxu.cloud.ai.entity.Knowledge;
import cc.xiaoxu.cloud.ai.service.KnowledgeSectionService;
import cc.xiaoxu.cloud.ai.service.KnowledgeService;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeAddTableEventDTO;
import cc.xiaoxu.cloud.bean.ai.enums.FileStatusEnum;
import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AddTableEvent {

    private final KnowledgeService knowledgeService;
    private final KnowledgeSectionService knowledgeSectionService;

    @Async
    @EventListener(classes = {KnowledgeAddTableEventDTO.class})
    public void onApplicationEvent(KnowledgeAddTableEventDTO dto) {

        // 数据查询
        knowledgeService.changeStatus(dto.getKnowledgeId(), FileStatusEnum.SECTION_READ);
        knowledgeSectionService.readTableSection(dto.getKnowledgeBaseId(), dto.getKnowledgeId(), dto.getSql(), dto.getUserId());

        // 切片
        knowledgeService.changeStatus(dto.getKnowledgeId(), FileStatusEnum.VECTOR_CALC);
        knowledgeSectionService.calcVector(new IdDTO(dto.getKnowledgeId()));
        knowledgeService.changeStatus(dto.getKnowledgeId(), FileStatusEnum.ALL_COMPLETED);

        knowledgeService.lambdaUpdate()
                .eq(Knowledge::getId, dto.getKnowledgeId())
                .eq(Knowledge::getState, StateEnum.ENABLE.getCode())
                .update();
    }
}