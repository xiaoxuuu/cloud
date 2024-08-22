package cc.xiaoxu.cloud.controller;

import cc.xiaoxu.cloud.bean.ai.dto.AiChatMessageDTO;
import cc.xiaoxu.cloud.bean.ai.dto.AskDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiChatModelEnum;
import cc.xiaoxu.cloud.bean.ai.enums.AiTalkTypeEnum;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeSectionVO;
import cc.xiaoxu.cloud.core.annotation.Wrap;
import cc.xiaoxu.cloud.manager.AiProcessor;
import cc.xiaoxu.cloud.manager.ChatInfo;
import cc.xiaoxu.cloud.manager.ai.Prompt;
import cc.xiaoxu.cloud.service.ALiYunService;
import cc.xiaoxu.cloud.service.KnowledgeSectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@Tag(name = "聊天服务")
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

    @Wrap(disabled = true)
    @PostMapping(value = "/ask", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "提问")
    public String ask(@Valid @RequestBody AskDTO vo, HttpServletResponse response) {

        // 问题转为向量
        List<Double> vectorList = aLiYunService.vector(vo.getQuestion());
        String embedding = String.valueOf(vectorList);
        log.info("向量计算完成，维度：{}", vectorList.size());

        // 取出相似度数据
        List<KnowledgeSectionVO> similarityData = knowledgeSectionService.getBaseMapper().getSimilarityData(embedding, vo.getSimilarity(), vo.getSimilarityContentNum());
        String distanceList = similarityData.stream().map(KnowledgeSectionVO::getDistance).map(String::toString).map(k -> k.substring(0, 5)).collect(Collectors.joining(","));
        log.info("相似文本获取成功：{} 条，相似度依次为：[{}] (越大越好)", similarityData.size(), distanceList);
        String knowledgeList = similarityData.stream().map(KnowledgeSectionVO::getCutContent).collect(Collectors.joining(System.lineSeparator()));

        // 提问
        AiKimiController.setResponseHeader(response);
        SseEmitter emitter = new SseEmitter();
        List<AiChatMessageDTO> ask = Prompt.Knowledge.ask("超魔杀帝国", vo.getQuestion(), knowledgeList);

        ChatInfo chatInfo = ChatInfo.of(ask, AiTalkTypeEnum.KNOWLEDGE, AiChatModelEnum.Q_WEN_72B_CHAT)
                .apiKey(apiKey)
                .stream(emitter);
//        threadPoolTaskExecutor.execute(() -> aiProcessor.chat(chatInfo));
//        return emitter;
        return aiProcessor.chat(chatInfo).getResult();
    }
}