package cc.xiaoxu.cloud.service;

import cc.xiaoxu.cloud.bean.ai.vo.SplitTxtVO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.dao.KnowledgeSectionMapper;
import cc.xiaoxu.cloud.entity.KnowledgeSection;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class KnowledgeSectionService extends ServiceImpl<KnowledgeSectionMapper, KnowledgeSection> {

    private final ALiYunService aLiYunService;

    public boolean rebuild(SplitTxtVO vo) {

        int knowledgeId = 2;

        boolean hasNext = true;
        int pageNum = 1;
        List<String> readSectionList = new ArrayList<>();
        while (hasNext) {
            // 每次处理 1000 条数据
            List<String> readSectionListTemp = aLiYunService.readSection(vo.getIndexId(), vo.getFiled(), vo.getWorkspaceId(), pageNum, 1000);
            readSectionList.addAll(readSectionListTemp);
            log.info("读取到 {} 条数据", readSectionListTemp.size());
            if (readSectionListTemp.isEmpty()) {
                hasNext = false;
            }
            pageNum++;
        }

        log.info("读取完成，一共 {} 条数据", readSectionList.size());
        // 移除旧数据
        lambdaUpdate()
                .eq(KnowledgeSection::getKnowledgeId, knowledgeId)
                .remove();
        // 数据入库
        List<KnowledgeSection> knowledgeSectionList = readSectionList.stream().map(k -> buildKnowledgeSection(knowledgeId, k)).toList();
        saveBatch(knowledgeSectionList, 1000);
        return true;
    }

    private KnowledgeSection buildKnowledgeSection(Integer knowledgeId, String content) {

        KnowledgeSection knowledgeSection = new KnowledgeSection();
        knowledgeSection.setKnowledgeId(knowledgeId);
        knowledgeSection.setCutContent(content);
        knowledgeSection.setState(StateEnum.ENABLE.getCode());
        knowledgeSection.setCreateTime(new Date());
        return knowledgeSection;
    }
}