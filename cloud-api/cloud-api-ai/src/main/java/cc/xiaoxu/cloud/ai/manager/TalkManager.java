package cc.xiaoxu.cloud.ai.manager;

import cc.xiaoxu.cloud.ai.service.KnowledgeSectionService;
import cc.xiaoxu.cloud.ai.service.LocalApiService;
import cc.xiaoxu.cloud.bean.ai.dto.AskDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiModelEnum;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeSectionExpandVO;
import cc.xiaoxu.cloud.bean.ai.vo.SseVO;
import cc.xiaoxu.cloud.core.utils.StopWatchUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TalkManager {

    @Resource
    private KnowledgeSectionService knowledgeSectionService;

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    private LocalApiService localApiService;

    @Resource
    private AiManager aiManager;

    private static final String DEFAULT_ANSWER = "没有在知识库中查找到相关信息，请调整问题描述或更新知识库";

    public void talk(AskDTO vo, SseEmitter emitter, String userId, StopWatchUtil sw, AiModelEnum modelTypeEnum) {

        sw.start("获取知识数据");
        List<KnowledgeSectionExpandVO> similarityDataList = getKnowledgeSectionDataList(vo, userId, sw);

        if (CollectionUtils.isEmpty(similarityDataList)) {
            log.info("未匹配到相似度数据，使用默认回答：{}", DEFAULT_ANSWER);
            threadPoolTaskExecutor.execute(() -> {
                defaultAnswer(emitter);
                sw.print(log::info);
            });
        } else {
            // 回答问题后扣减次数
            sw.start("调用问题回答");

            String similarityData = getSimilarityData(emitter, similarityDataList);
            aiManager.knowledge(vo.getQuestion(), similarityData, 1, "sk-", modelTypeEnum, emitter);
        }
    }

    private String getSimilarityData(SseEmitter emitter, List<KnowledgeSectionExpandVO> similarityData) {

        String distanceList = similarityData.stream()
                .map(KnowledgeSectionExpandVO::getDistance)
                .map(String::toString)
                .map(k -> k.length() < 6 ? k : k.substring(0, 5))
                .collect(Collectors.joining(","));
        if (null != emitter) {
            try {
                emitter.send(SseVO.start());
                for (KnowledgeSectionExpandVO similarityDatum : similarityData) {
                    similarityDatum.setEmbedding(null);
                    emitter.send(SseVO.paramMap(Map.of("USE_DATA", similarityDatum)));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        log.info("相似文本获取成功：{} 条，相似度依次为：[{}] (越小越好)", similarityData.size(), distanceList);
        return similarityData.stream()
                .map(KnowledgeSectionExpandVO::getCutContent)
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private List<KnowledgeSectionExpandVO> getKnowledgeSectionDataList(AskDTO vo, String userId, StopWatchUtil sw) {

        sw.start("问题转向量");
        // 问题转为向量
        List<Double> vectorList = localApiService.vector(vo.getQuestion());
        String embedding = String.valueOf(vectorList);
        log.info("向量计算完成，维度：{}", vectorList.size());

        // 取出相似度数据
        sw.start("问题相似文本查询");
        return knowledgeSectionService.getBaseMapper()
                .getSimilarityData(embedding, vo, userId);
    }

    private void defaultAnswer(SseEmitter emitter) {
        defaultAnswer(emitter, DEFAULT_ANSWER);
    }

    private void defaultAnswer(SseEmitter emitter, String answer) {

        Random random = new Random();
        try {
            emitter.send(SseVO.start());
            for (char c : answer.toCharArray()) {
                emitter.send(SseVO.msg(c));
                Thread.sleep(random.nextInt(20) + 10);
            }
            emitter.send(SseVO.end());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            emitter.complete();
        }
    }
}
