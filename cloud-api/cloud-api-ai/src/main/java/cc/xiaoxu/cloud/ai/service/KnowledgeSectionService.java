package cc.xiaoxu.cloud.ai.service;

import cc.xiaoxu.cloud.ai.dao.KnowledgeSectionMapper;
import cc.xiaoxu.cloud.ai.entity.Knowledge;
import cc.xiaoxu.cloud.ai.entity.KnowledgeSection;
import cc.xiaoxu.cloud.ai.manager.CommonManager;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeSectionVO;
import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.bean.dto.PageDTO;
import cc.xiaoxu.cloud.core.utils.PageUtils;
import cc.xiaoxu.cloud.core.utils.set.ListUtils;
import com.alibaba.dashscope.embeddings.TextEmbeddingResultItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class KnowledgeSectionService extends ServiceImpl<KnowledgeSectionMapper, KnowledgeSection> {

    private final ALiYunService aLiYunService;
    private final CommonManager commonManager;

    public boolean readALiSection(Knowledge knowledge) {

        boolean hasNext = true;
        int pageNum = 1;
        List<String> readSectionList = new ArrayList<>();
        while (hasNext) {
            // 每次处理 1000 条数据
            List<String> readSectionListTemp = aLiYunService.readSection(knowledge.getThreePartyFileId(), pageNum, 1000);
            readSectionList.addAll(readSectionListTemp);
            log.info("读取到 {} 条数据", readSectionListTemp.size());
            if (readSectionListTemp.isEmpty()) {
                hasNext = false;
            }
            pageNum++;
        }

        insertNewData(knowledge.getId(), readSectionList);
        return true;
    }

    public boolean readTableSection(Integer knowledgeId, String sql) {

        List<String> dataList = commonManager.getList(sql);

        insertNewData(knowledgeId, dataList);
        return true;
    }

    public boolean readCustomSection(Integer knowledgeId, String content) {

        KnowledgeSection knowledgeSection = buildKnowledgeSection(knowledgeId, content);
        save(knowledgeSection);
        return true;
    }

    private void insertNewData(Integer knowledgeId, List<String> dataList) {

        log.info("读取完成，一共 {} 条数据", dataList.size());
        // 移除旧数据
        lambdaUpdate()
                .eq(KnowledgeSection::getKnowledgeId, knowledgeId)
                .remove();
        // 数据入库
        List<KnowledgeSection> knowledgeSectionList = dataList.stream().map(k -> buildKnowledgeSection(knowledgeId, k)).toList();
        saveBatch(knowledgeSectionList, 1000);
    }

    private KnowledgeSection buildKnowledgeSection(Integer knowledgeId, String content) {

        KnowledgeSection knowledgeSection = new KnowledgeSection();
        knowledgeSection.setKnowledgeId(knowledgeId);
        knowledgeSection.setCutContent(content);
        knowledgeSection.setState(StateEnum.ENABLE.getCode());
        knowledgeSection.setCreateTime(new Date());
        return knowledgeSection;
    }

    public boolean calcVector(IdDTO dto) {

        List<KnowledgeSection> list = lambdaQuery()
                .eq(KnowledgeSection::getKnowledgeId, Integer.parseInt(dto.getId()))
                .isNull(KnowledgeSection::getEmbedding)
                .list();
        List<List<KnowledgeSection>> lists = ListUtils.splitList(list, 25);
        for (List<KnowledgeSection> knowledgeSections : lists) {
            List<String> cutList = knowledgeSections.stream().map(KnowledgeSection::getCutContent).toList();
            List<TextEmbeddingResultItem> embeddingResultItemList = aLiYunService.vector(cutList);
            Map<Integer, List<Double>> map = embeddingResultItemList.stream().collect(Collectors.toMap(TextEmbeddingResultItem::getTextIndex, TextEmbeddingResultItem::getEmbedding));
            for (int i = 0; i < knowledgeSections.size(); i++) {
                if (map.containsKey(i)) {
                    getBaseMapper().updateEmbedding(String.valueOf(map.get(i)), knowledgeSections.get(i).getId());
                }
            }
        }
        return true;
    }

    public Page<KnowledgeSectionVO> pages(PageDTO dto) {

        Page<KnowledgeSection> entityPage = lambdaQuery()
                .page(PageUtils.getPageCondition(dto));

        return PageUtils.getPage(entityPage, KnowledgeSectionVO.class);
    }
}