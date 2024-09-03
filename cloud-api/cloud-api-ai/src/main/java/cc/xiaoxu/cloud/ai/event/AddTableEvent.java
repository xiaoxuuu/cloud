package cc.xiaoxu.cloud.ai.event;

import cc.xiaoxu.cloud.ai.manager.CommonManager;
import cc.xiaoxu.cloud.ai.service.KnowledgeSectionService;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeAddTableEventDTO;
import cc.xiaoxu.cloud.bean.dto.IdDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AddTableEvent {

    private final CommonManager commonManager;
    private final KnowledgeSectionService knowledgeSectionService;

    @EventListener(classes = {KnowledgeAddTableEventDTO.class})
    public void onApplicationEvent(KnowledgeAddTableEventDTO dto) {

        // 数据查询
        knowledgeSectionService.readTableSection(dto.getKnowledgeId(), dto.getSql());

        // 切片
        knowledgeSectionService.calcVector(new IdDTO(String.valueOf(dto.getKnowledgeId())));
    }
}
