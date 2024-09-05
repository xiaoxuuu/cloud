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
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeSectionVO;
import cc.xiaoxu.cloud.core.annotation.Wrap;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping(value = "/ask", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "提问")
    public String ask(@Valid @RequestBody AskDTO vo, HttpServletResponse response) {

        ChatInfo chatInfo = getChatInfo(vo, response, null);
        return aiProcessor.chat(chatInfo).getResult();
    }

    @Parameters({
            @Parameter(required = true, name = "question", description = "问题", in = ParameterIn.PATH),
            @Parameter(required = true, name = "similarity", description = "相似度，越小越好，越大越不相似", in = ParameterIn.PATH),
            @Parameter(required = true, name = "similarityContentNum", description = "引用分段数，取最相似的前 n 条", in = ParameterIn.PATH)
    })
    @Wrap(disabled = true)
    @GetMapping(value = "/ask/{similarity}/{similarityContentNum}/{question}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "提问")
    public SseEmitter ask(@PathVariable("similarity") Double similarity, @PathVariable("similarityContentNum") Integer similarityContentNum,
                          @PathVariable("question") String question, HttpServletResponse response) {

        SseEmitter emitter = new SseEmitter();
        ChatInfo chatInfo = getChatInfo(new AskDTO(question, similarity, similarityContentNum), response, emitter);
        threadPoolTaskExecutor.execute(() -> aiProcessor.chat(chatInfo));
        return emitter;
    }

    private ChatInfo getChatInfo(AskDTO vo, HttpServletResponse response, SseEmitter emitter) {
        // 问题转为向量
        List<Double> vectorList = aLiYunService.vector(vo.getQuestion());
        String embedding = String.valueOf(vectorList);
        log.info("向量计算完成，维度：{}", vectorList.size());

        // 取出相似度数据
        List<KnowledgeSectionVO> similarityData = knowledgeSectionService.getBaseMapper().getSimilarityData(embedding, vo.getSimilarity(), vo.getSimilarityContentNum());
        String distanceList = similarityData.stream().map(KnowledgeSectionVO::getDistance).map(String::toString).map(k -> k.substring(0, 5)).collect(Collectors.joining(","));
        log.info("相似文本获取成功：{} 条，相似度依次为：[{}] (越小越好)", similarityData.size(), distanceList);
        String knowledgeList = similarityData.stream().map(KnowledgeSectionVO::getCutContent).collect(Collectors.joining(System.lineSeparator()));

        // 提问
        AiKimiController.setResponseHeader(response);
        List<AiChatMessageDTO> ask = Prompt.Knowledge.ask("本地知识库", vo.getQuestion(), knowledgeList);

        return ChatInfo.of(ask, AiTalkTypeEnum.KNOWLEDGE, AiChatModelEnum.Q_WEN_72B_CHAT)
                .apiKey(apiKey)
                .stream(emitter);
    }
}