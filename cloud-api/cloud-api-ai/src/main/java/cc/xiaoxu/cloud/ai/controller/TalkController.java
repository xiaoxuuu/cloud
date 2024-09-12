package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.manager.AiProcessor;
import cc.xiaoxu.cloud.ai.manager.ChatInfo;
import cc.xiaoxu.cloud.ai.manager.ai.Prompt;
import cc.xiaoxu.cloud.ai.service.ALiYunService;
import cc.xiaoxu.cloud.ai.service.KnowledgeSectionService;
import cc.xiaoxu.cloud.bean.ai.dto.AiChatMessageDTO;
import cc.xiaoxu.cloud.bean.ai.dto.AskDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiChatModelEnum;
import cc.xiaoxu.cloud.bean.ai.enums.AiTalkTypeEnum;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeSectionExpandVO;
import cc.xiaoxu.cloud.bean.ai.vo.SseVO;
import cc.xiaoxu.cloud.core.annotation.Wrap;
import cc.xiaoxu.cloud.core.cache.CacheService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${ali.bailian.api-key}")
    private String apiKey;

    @Resource
    private KnowledgeSectionService knowledgeSectionService;

    @Resource
    private ALiYunService aLiYunService;

    @Resource
    private AiProcessor aiProcessor;

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Resource
    private CacheService cacheService;

    private static final String DEFAULT_ANSWER = "没有在知识库中查找到相关信息，请调整问题描述或更新知识库";

    @PostMapping(value = "/ask/{tenant}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "提问")
    public String ask(@Valid @RequestBody AskDTO vo, @PathVariable("tenant") String tenant, HttpServletResponse response) {

        TenantController.checkTenantThrow(tenant);
        List<KnowledgeSectionExpandVO> similarityData = getKnowledgeSectionDataList(vo, tenant);

        if (CollectionUtils.isEmpty(similarityData)) {
            log.info("未匹配到相似度数据，使用默认回答：{}", DEFAULT_ANSWER);
            return DEFAULT_ANSWER;
        } else {
            ChatInfo chatInfo = getChatInfo(vo, response, null, similarityData);
            return aiProcessor.chat(chatInfo).getResult();
        }
    }

    @Parameters({
            @Parameter(required = true, name = "knowledgeId", description = "选用知识分类，留空则不限制", in = ParameterIn.PATH),
            @Parameter(required = true, name = "question", description = "问题", in = ParameterIn.PATH),
            @Parameter(required = true, name = "similarity", description = "相似度，越小越好，越大越不相似", in = ParameterIn.PATH),
            @Parameter(required = true, name = "similarityContentNum", description = "引用分段数，取最相似的前 n 条", in = ParameterIn.PATH)
    })
    @Wrap(disabled = true)
    @GetMapping(value = "/ask/{tenant}/{knowledgeId}/{similarity}/{similarityContentNum}/{question}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "提问 - 全参数")
    public SseEmitter ask(@PathVariable("knowledgeId") String knowledgeId, @PathVariable("similarity") Double similarity,
                          @PathVariable("similarityContentNum") Integer similarityContentNum,
                          @PathVariable("question") String question, @PathVariable("tenant") String tenant, HttpServletResponse response) {

        TenantController.checkTenantThrow(tenant);
        AskDTO vo = new AskDTO(question, similarity, similarityContentNum, knowledgeId);
        SseEmitter emitter = new SseEmitter();
        sendSseEmitter(response, vo, emitter, tenant);
        return emitter;
    }

    @Wrap(disabled = true)
    @GetMapping(value = "/ask/{tenant}/{question}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "提问 - 简洁参数")
    public SseEmitter ask(@PathVariable("question") String question, @PathVariable("tenant") String tenant, HttpServletResponse response) {

        TenantController.checkTenantThrow(tenant);
        AskDTO vo = new AskDTO(question, 0.7, 10, null);
        SseEmitter emitter = new SseEmitter();
        sendSseEmitter(response, vo, emitter, tenant);
        return emitter;
    }

    private Integer count = 100;
    private String countKey = "AI:COUNT:";

    private void sendSseEmitter(HttpServletResponse response, AskDTO vo, SseEmitter emitter, String tenant) {

        List<KnowledgeSectionExpandVO> similarityData = getKnowledgeSectionDataList(vo, tenant);

        if (CollectionUtils.isEmpty(similarityData)) {
            log.info("未匹配到相似度数据，使用默认回答：{}", DEFAULT_ANSWER);
            threadPoolTaskExecutor.execute(() -> defaultAnswer(emitter));
        } else {
            // 回答问题后扣减次数
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
            ChatInfo chatInfo = getChatInfo(vo, response, emitter, similarityData);
            threadPoolTaskExecutor.execute(() -> aiProcessor.chat(chatInfo));
        }
    }

    private ChatInfo getChatInfo(AskDTO vo, HttpServletResponse response, SseEmitter emitter, List<KnowledgeSectionExpandVO> similarityData) {

        String distanceList = similarityData.stream()
                .map(KnowledgeSectionExpandVO::getDistance)
                .map(String::toString)
                .map(k -> k.length() < 6 ? k : k.substring(0, 5))
                .collect(Collectors.joining(","));
        try {
            emitter.send(SseVO.start());
            for (KnowledgeSectionExpandVO similarityDatum : similarityData) {
                similarityDatum.setEmbedding(null);
                emitter.send(SseVO.paramMap(Map.of("USE_DATA", similarityDatum)));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("相似文本获取成功：{} 条，相似度依次为：[{}] (越小越好)", similarityData.size(), distanceList);
        String knowledgeList = similarityData.stream()
                .map(KnowledgeSectionExpandVO::getCutContent)
                .collect(Collectors.joining(System.lineSeparator()));

        // 提问
        AiKimiController.setResponseHeader(response);
        List<AiChatMessageDTO> ask = Prompt.Ask.v2("本地知识库", vo.getQuestion(), knowledgeList, DEFAULT_ANSWER);

        return ChatInfo.of(ask, AiTalkTypeEnum.KNOWLEDGE, AiChatModelEnum.Q_WEN_72B_CHAT)
                .apiKey(apiKey)
                .stream(emitter);
    }

    private List<KnowledgeSectionExpandVO> getKnowledgeSectionDataList(AskDTO vo, String tenant) {

        // 问题转为向量
        List<Double> vectorList = aLiYunService.vector(vo.getQuestion());
        String embedding = String.valueOf(vectorList);
        log.info("向量计算完成，维度：{}", vectorList.size());

        // 取出相似度数据
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