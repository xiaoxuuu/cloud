package cc.xiaoxu.cloud.ai.event;

import cc.xiaoxu.cloud.ai.service.KnowledgeSectionService;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeAddCustomEventDTO;
import cc.xiaoxu.cloud.bean.dto.IdDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AddCustomEvent {

    private final KnowledgeSectionService knowledgeSectionService;

    @EventListener(classes = {KnowledgeAddCustomEventDTO.class})
    public void onApplicationEvent(KnowledgeAddCustomEventDTO dto) {

        // 数据查询
        knowledgeSectionService.readCustomSection(dto.getKnowledgeId(), dto.getContent());

        // 切片
        knowledgeSectionService.calcVector(new IdDTO(String.valueOf(dto.getKnowledgeId())));
    }
}
