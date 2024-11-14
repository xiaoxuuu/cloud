package cc.xiaoxu.cloud.ai.service;

import cc.xiaoxu.cloud.ai.dao.KnowledgeSectionMapper;
import cc.xiaoxu.cloud.ai.entity.Knowledge;
import cc.xiaoxu.cloud.ai.entity.KnowledgeSection;
import cc.xiaoxu.cloud.ai.manager.CommonManager;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeEditStateDTO;
import cc.xiaoxu.cloud.bean.ai.dto.LocalVectorDTO;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeSectionVO;
import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.bean.dto.PageDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.utils.PageUtils;
import cc.xiaoxu.cloud.core.utils.set.ListUtils;
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

    private final ALiYunApiService aLiYunApiService;
    private final CommonManager commonManager;
    private final LocalApiService localApiService;

    public boolean readALiSection(Knowledge knowledge) {

        boolean hasNext = true;
        int pageNum = 1;
        List<String> readSectionList = new ArrayList<>();
        while (hasNext) {
            // 每次处理 1000 条数据
            List<String> readSectionListTemp = aLiYunApiService.readSection(knowledge.getThreePartyFileId(), pageNum, 1000);
            readSectionList.addAll(readSectionListTemp);
            log.info("读取到 {} 条数据", readSectionListTemp.size());
            if (readSectionListTemp.isEmpty()) {
                hasNext = false;
            }
            pageNum++;
        }

        insertNewData(knowledge.getId(), readSectionList, knowledge.getTenant());
        return true;
    }

    public boolean readTableSection(Integer knowledgeId, String sql, String tenant) {

        List<String> dataList = commonManager.getList(sql);

        insertNewData(knowledgeId, dataList, tenant);
        return true;
    }

    public boolean readCustomSection(Integer knowledgeId, String content, String tenant) {

        KnowledgeSection knowledgeSection = buildKnowledgeSection(knowledgeId, content, tenant);
        save(knowledgeSection);
        return true;
    }

    public void insertNewData(Integer knowledgeId, List<String> dataList, String tenant) {

        log.info("读取完成，一共 {} 条数据", dataList.size());
        // 移除旧数据
        lambdaUpdate()
                .eq(KnowledgeSection::getKnowledgeId, knowledgeId)
                .remove();
        // 数据入库
        List<KnowledgeSection> knowledgeSectionList = dataList.stream().map(k -> buildKnowledgeSection(knowledgeId, k, tenant)).toList();
        saveBatch(knowledgeSectionList, 1000);
    }

    private KnowledgeSection buildKnowledgeSection(Integer knowledgeId, String content, String tenant) {

        KnowledgeSection knowledgeSection = new KnowledgeSection();
        knowledgeSection.setTenant(tenant);
        knowledgeSection.setKnowledgeId(knowledgeId);
        knowledgeSection.setCutContent(content);
        knowledgeSection.setState(StateEnum.ENABLE.getCode());
        knowledgeSection.setCreateTime(new Date());
        return knowledgeSection;
    }

    public boolean calcVector(IdDTO dto) {

        List<KnowledgeSection> list = lambdaQuery()
                .eq(KnowledgeSection::getKnowledgeId, dto.getId())
                .isNull(KnowledgeSection::getEmbedding)
                .list();
        List<List<KnowledgeSection>> lists = ListUtils.splitList(list, 10);
        lists.parallelStream().forEach(knowledgeSections -> {
            String collect = knowledgeSections.stream().map(KnowledgeSection::getId).map(String::valueOf).collect(Collectors.joining(","));
            log.info("处理文本：{}", collect);
            List<String> cutList = knowledgeSections.stream().map(KnowledgeSection::getCutContent).toList();
            // 阿里向量化
//            List<TextEmbeddingResultItem> embeddingResultItemList = aLiYunApiService.vector(cutList);
//            Map<Integer, List<Double>> map = embeddingResultItemList.stream().collect(Collectors.toMap(TextEmbeddingResultItem::getTextIndex, TextEmbeddingResultItem::getEmbedding));
            // 本地
            List<LocalVectorDTO> vectorList = localApiService.localVector(cutList);
            Map<Integer, List<Double>> map = vectorList.stream().collect(Collectors.toMap(LocalVectorDTO::getIndex, LocalVectorDTO::getEmbedding));
            for (int i = 0; i < knowledgeSections.size(); i++) {
                if (map.containsKey(i)) {
                    getBaseMapper().updateEmbedding(String.valueOf(map.get(i)), knowledgeSections.get(i).getId());
                }
            }
        });
        return true;
    }

    public Page<KnowledgeSectionVO> pages(PageDTO dto, String tenant) {

        Page<KnowledgeSection> entityPage = lambdaQuery()
                .eq(KnowledgeSection::getTenant, tenant)
                .page(PageUtils.getPageCondition(dto));

        Page<KnowledgeSectionVO> page = PageUtils.getPage(entityPage, KnowledgeSectionVO.class);
        for (KnowledgeSectionVO record : page.getRecords()) {
            record.setEmbedding(null);
        }
        return page;
    }

    public void editState(KnowledgeEditStateDTO dto, String tenant) {

        lambdaUpdate()
                .eq(KnowledgeSection::getTenant, tenant)
                .in(KnowledgeSection::getKnowledgeId, dto.getIdList())
                .set(KnowledgeSection::getState, dto.getState())
                .update();
    }
}