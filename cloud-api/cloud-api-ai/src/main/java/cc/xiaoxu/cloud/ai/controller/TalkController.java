package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.manager.AiManager;
import cc.xiaoxu.cloud.ai.service.KnowledgeSectionService;
import cc.xiaoxu.cloud.ai.service.LocalApiService;
import cc.xiaoxu.cloud.ai.service.TenantService;
import cc.xiaoxu.cloud.bean.ai.dto.AskDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiModelEnum;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeSectionExpandVO;
import cc.xiaoxu.cloud.bean.ai.vo.SseVO;
import cc.xiaoxu.cloud.core.annotation.Wrap;
import cc.xiaoxu.cloud.core.cache.CacheService;
import cc.xiaoxu.cloud.core.utils.StopWatchUtil;
import cc.xiaoxu.cloud.core.utils.enums.EnumUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Tag(name = "AI 知识库聊天服务")
@RequestMapping("/talk")
public class TalkController {

    @Resource
    private KnowledgeSectionService knowledgeSectionService;

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    private CacheService cacheService;

    @Resource
    private TenantService tenantService;

    @Resource
    private LocalApiService localApiService;

    @Resource
    private AiManager aiManager;

    private static final String DEFAULT_ANSWER = "没有在知识库中查找到相关信息，请调整问题描述或更新知识库";

    @PostMapping(value = "/get_model")
    @Operation(summary = "获取模型")
    public List<AiModelEnum> getModel() {

        return List.of(AiModelEnum.LOCAL_QWEN2_5_14B_INSTRUCT_AWQ, AiModelEnum.LOCAL_QWEN2_5_32B_INSTRUCT_AWQ, AiModelEnum.LOCAL, AiModelEnum.MOONSHOT_V1_128K);
    }

    @Parameters({
            @Parameter(required = true, name = "tenant", description = "租户", in = ParameterIn.PATH),
            @Parameter(required = true, name = "knowledgeId", description = "选用知识分类，留空则不限制", in = ParameterIn.PATH),
            @Parameter(required = true, name = "question", description = "问题", in = ParameterIn.PATH),
            @Parameter(required = true, name = "similarity", description = "相似度，越小越好，越大越不相似", in = ParameterIn.PATH),
            @Parameter(required = true, name = "modelTypeEnum", description = "选择的模型", in = ParameterIn.PATH),
            @Parameter(required = true, name = "similarityContentNum", description = "引用分段数，取最相似的前 n 条", in = ParameterIn.PATH)
    })
    @Wrap(disabled = true)
    @GetMapping(value = "/ask/{tenant}/{knowledgeId}/{similarity}/{similarityContentNum}/{modelTypeEnum}/{question}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "提问 - 全参数")
    public SseEmitter ask(@PathVariable("tenant") String tenant, @PathVariable("knowledgeId") String knowledgeId,
                          @PathVariable("similarity") Double similarity, @PathVariable("similarityContentNum") Integer similarityContentNum,
                          @PathVariable("modelTypeEnum") String modelTypeEnum,
                          @PathVariable("question") String question, HttpServletResponse response) {

        AiKimiController.setResponseHeader(response);

        StopWatchUtil sw = new StopWatchUtil("知识库提问");
        sw.start("校验用户");
        tenantService.checkTenantThrow(tenant);
        sw.start("构建必备类");
        AskDTO vo = new AskDTO(question, similarity, similarityContentNum, knowledgeId);
        SseEmitter emitter = new SseEmitter();
        sw.start("校验用户");

        // 提问
        sendSseEmitter(vo, emitter, tenant, sw, EnumUtils.getByClass(modelTypeEnum, AiModelEnum.class));
        return emitter;
    }

    @Parameters({
            @Parameter(required = true, name = "tenant", description = "租户", in = ParameterIn.PATH),
            @Parameter(required = true, name = "question", description = "问题", in = ParameterIn.PATH),
    })
    @Wrap(disabled = true)
    @GetMapping(value = "/ask/{tenant}/{question}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "提问 - 简洁参数")
    public SseEmitter ask(@PathVariable("tenant") String tenant, @PathVariable("question") String question, HttpServletResponse response) {

        return ask(tenant, null, 0.7, 10, "LOCAL_QWEN2_5_32B_INSTRUCT_AWQ", question, response);
    }

    private Integer count = 100;
    private String countKey = "AI:COUNT:";

    private void sendSseEmitter(AskDTO vo, SseEmitter emitter, String tenant, StopWatchUtil sw, AiModelEnum modelTypeEnum) {

        sw.start("获取知识数据");
        List<KnowledgeSectionExpandVO> similarityDataList = getKnowledgeSectionDataList(vo, tenant, sw);

        if (CollectionUtils.isEmpty(similarityDataList)) {
            log.info("未匹配到相似度数据，使用默认回答：{}", DEFAULT_ANSWER);
            threadPoolTaskExecutor.execute(() -> {
                defaultAnswer(emitter);
                sw.print(log::info);
            });
        } else {
            // 回答问题后扣减次数
            sw.start("调用问题回答");
            Integer todayCount = cacheService.getCacheObject(countKey + tenant);
            if (null == todayCount) {
                todayCount = 0;
            }
            log.warn("当前使用次数：{}，总次数：{}", todayCount, count);
            if (todayCount >= count) {
                threadPoolTaskExecutor.execute(() -> defaultAnswer(emitter, "没有可使用次数了哦"));
                return;
            }
            todayCount++;
            cacheService.setCacheObject(countKey + tenant, todayCount);

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

    private List<KnowledgeSectionExpandVO> getKnowledgeSectionDataList(AskDTO vo, String tenant, StopWatchUtil sw) {

        sw.start("问题转向量");
        // 问题转为向量
        List<Double> vectorList = localApiService.vector(vo.getQuestion());
        String embedding = String.valueOf(vectorList);
        log.info("向量计算完成，维度：{}", vectorList.size());

        // 取出相似度数据
        sw.start("问题相似文本查询");
        return knowledgeSectionService.getBaseMapper()
                .getSimilarityData(embedding, vo, tenant);
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