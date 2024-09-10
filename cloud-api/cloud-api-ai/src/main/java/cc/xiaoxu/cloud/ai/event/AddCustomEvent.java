package cc.xiaoxu.cloud.ai.event;

import cc.xiaoxu.cloud.ai.entity.Knowledge;
import cc.xiaoxu.cloud.ai.service.KnowledgeSectionService;
import cc.xiaoxu.cloud.ai.service.KnowledgeService;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeAddCustomEventDTO;
import cc.xiaoxu.cloud.bean.ai.enums.FileStatusEnum;
import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AddCustomEvent {

    private final KnowledgeService knowledgeService;
    private final KnowledgeSectionService knowledgeSectionService;

    @EventListener(classes = {KnowledgeAddCustomEventDTO.class})
    public void onApplicationEvent(KnowledgeAddCustomEventDTO dto) {

        // 数据查询
        knowledgeService.changeStatus(dto.getKnowledgeId(), FileStatusEnum.SECTION_READ);
        knowledgeSectionService.readCustomSection(dto.getKnowledgeId(), dto.getContent());

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
