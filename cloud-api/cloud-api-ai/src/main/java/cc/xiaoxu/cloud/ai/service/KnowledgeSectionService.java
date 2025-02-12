package cc.xiaoxu.cloud.ai.service;

import cc.xiaoxu.cloud.ai.dao.KnowledgeSectionMapper;
import cc.xiaoxu.cloud.ai.entity.Knowledge;
import cc.xiaoxu.cloud.ai.entity.KnowledgeSection;
import cc.xiaoxu.cloud.ai.manager.CommonManager;
import cc.xiaoxu.cloud.ai.utils.FileUtils;
import cc.xiaoxu.cloud.bean.ai.dto.AskDTO;
import cc.xiaoxu.cloud.bean.ai.dto.ConversationAddDTO;
import cc.xiaoxu.cloud.bean.ai.dto.KnowledgeEditStateDTO;
import cc.xiaoxu.cloud.bean.ai.dto.LocalVectorDTO;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeSectionExpandVO;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeSectionVO;
import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.bean.dto.PageDTO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.utils.PageUtils;
import cc.xiaoxu.cloud.core.utils.StopWatchUtil;
import cc.xiaoxu.cloud.core.utils.set.ListUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class KnowledgeSectionService extends ServiceImpl<KnowledgeSectionMapper, KnowledgeSection> {

    private final CommonManager commonManager;
    private final LocalApiService localApiService;

    public boolean rebuildSection(Knowledge knowledge) {

        log.debug("读取文件：{}", knowledge.getName());
        // 读取指定位置文件
        String content = FileUtils.read(knowledge.getFileId());
        log.debug("读取文件长度：{}", content.length());

        // 发起请求
        List<String> textList;
        textList = localApiService.split(content);
        // 数据入库
        insertNewData(knowledge.getKnowledgeBaseId(), knowledge.getId(), textList, knowledge.getUserId());
        return true;
    }

    public boolean readTableSection(Integer knowledgeBaseId, Integer knowledgeId, String sql, Integer userId) {

        List<String> dataList = commonManager.getList(sql);

        insertNewData(knowledgeBaseId, knowledgeId, dataList, userId);
        return true;
    }

    public boolean readCustomSection(Integer knowledgeBaseId, Integer knowledgeId, String content, Integer userId) {

        KnowledgeSection knowledgeSection = buildKnowledgeSection(knowledgeBaseId, knowledgeId, content, userId);
        save(knowledgeSection);
        return true;
    }

    public void insertNewData(Integer knowledgeBaseId, Integer knowledgeId, List<String> dataList, Integer userId) {

        log.debug("读取完成，一共 {} 条数据", dataList.size());
        // 移除旧数据
        lambdaUpdate()
                .eq(KnowledgeSection::getKnowledgeId, knowledgeId)
                .remove();
        // 数据入库
        List<KnowledgeSection> knowledgeSectionList = dataList.stream().map(k -> buildKnowledgeSection(knowledgeBaseId, knowledgeId, k, userId)).toList();
        saveBatch(knowledgeSectionList, 1000);
    }

    private KnowledgeSection buildKnowledgeSection(Integer knowledgeBaseId, Integer knowledgeId, String content, Integer userId) {

        KnowledgeSection knowledgeSection = new KnowledgeSection();
        knowledgeSection.setUserId(userId);
        knowledgeSection.setKnowledgeBaseId(knowledgeBaseId);
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
        int success = calcVector(list, 10);
        // 补偿
        if (success != list.size()) {
            List<KnowledgeSection> relist = lambdaQuery()
                    .eq(KnowledgeSection::getKnowledgeId, dto.getId())
                    .isNull(KnowledgeSection::getEmbedding)
                    .list();
            log.debug("文本切片补偿：{} 片", relist.size());
            int i = calcVector(relist, 1);
            log.debug("文本切片补偿完成：{} 片，剩余 {} 片", i, relist.size() - i);
        }
        return true;
    }

    public int calcVector(List<KnowledgeSection> list, int batchSize) {

        AtomicInteger size = new AtomicInteger(list.size());
        log.debug("文本切片：{} 片", size);
        List<List<KnowledgeSection>> lists = ListUtils.splitList(list, batchSize);
        lists.parallelStream().forEach(knowledgeSections -> {
            List<String> cutList = knowledgeSections.stream().map(KnowledgeSection::getCutContent).toList();
            List<LocalVectorDTO> vectorList = localApiService.localVector(cutList);
            Map<Integer, List<Double>> map = vectorList.stream().collect(Collectors.toMap(LocalVectorDTO::getIndex, LocalVectorDTO::getEmbedding));
            for (int i = 0; i < knowledgeSections.size(); i++) {
                if (map.containsKey(i)) {
                    getBaseMapper().updateEmbedding(String.valueOf(map.get(i)), knowledgeSections.get(i).getId());
                    size.getAndDecrement();
                }
            }
        });
        log.debug("文本切片完成：{} 片，剩余：{} 片", list.size() - size.get(), size);
        return list.size() - size.get();
    }

    public Page<KnowledgeSectionVO> pages(PageDTO dto, Integer userId) {

        Page<KnowledgeSection> entityPage = lambdaQuery()
                .eq(KnowledgeSection::getUserId, userId)
                .page(PageUtils.getPageCondition(dto));

        Page<KnowledgeSectionVO> page = PageUtils.getPage(entityPage, KnowledgeSectionVO.class);
        for (KnowledgeSectionVO record : page.getRecords()) {
            record.setEmbedding(null);
        }
        return page;
    }

    public void editState(KnowledgeEditStateDTO dto, Integer userId) {

        lambdaUpdate()
                .eq(KnowledgeSection::getUserId, userId)
                .in(KnowledgeSection::getKnowledgeId, dto.getIdList())
                .set(KnowledgeSection::getState, dto.getState())
                .update();
    }

    public List<KnowledgeSectionExpandVO> getKnowledgeSectionDataList(ConversationAddDTO dto, Integer userId, StopWatchUtil sw) {

        AskDTO vo = new AskDTO(dto.getQuestion(), 0.7, 5, dto.getKnowledgeBaseId());

        sw.start("问题转向量");
        // 问题转为向量
        List<Double> vectorList = localApiService.vector(vo.getQuestion());
        String embedding = String.valueOf(vectorList);
        log.info("向量计算完成，维度：{}", vectorList.size());

        // 取出相似度数据
        sw.start("问题相似文本查询");
        return getBaseMapper().getSimilarityData(embedding, vo, userId);
    }
}