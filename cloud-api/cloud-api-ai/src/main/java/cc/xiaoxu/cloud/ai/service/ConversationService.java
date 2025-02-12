package cc.xiaoxu.cloud.ai.service;

import cc.xiaoxu.cloud.ai.dao.ConversationMapper;
import cc.xiaoxu.cloud.ai.dao.ModelInfoMapper;
import cc.xiaoxu.cloud.ai.entity.Conversation;
import cc.xiaoxu.cloud.ai.manager.AiManager;
import cc.xiaoxu.cloud.bean.ai.dto.ConversationAddDTO;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeSectionExpandVO;
import cc.xiaoxu.cloud.bean.ai.vo.SseVO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.utils.DateUtils;
import cc.xiaoxu.cloud.core.utils.StopWatchUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ConversationService extends ServiceImpl<ConversationMapper, Conversation> {

    private final AiManager aiManager;
    private final ModelInfoMapper modelInfoMapper;

    private static final String DEFAULT_ANSWER = "没有在知识库中查找到相关信息，请调整问题描述或更新知识库";

    public Conversation getOrCreateConversation(Integer conversationId, String question, Integer userId, Integer modelId) {

        Conversation one = lambdaQuery().eq(Conversation::getId, conversationId).one();
        if (null != one) {
            return one;
        }

        // 创建会话
        Conversation entity = getConversation(question, userId, modelId);
        save(entity);
        return entity;
    }

    @NotNull
    private static Conversation getConversation(String question, Integer userId, Integer modelId) {
        Conversation entity = new Conversation();
        entity.setName(question);
        entity.setUserId(userId);
        entity.setModelId(modelId);
        entity.setState(StateEnum.ENABLE.getCode());
        entity.setCreateTime(DateUtils.getNowDate());
        entity.setCreateId(userId);
        return entity;
    }

    public void talk(SseEmitter emitter, StopWatchUtil sw, ConversationAddDTO dto, List<KnowledgeSectionExpandVO> similarityDataList, Integer userId) {

        sw.start("获取对话数据");
        Conversation conversation = getOrCreateConversation(dto.getConversationId(), dto.getQuestion(), userId, dto.getModelId());

        sw.start("提问");
        String similarityData = getSimilarityData(emitter, similarityDataList);
        aiManager.knowledge(dto.getQuestion(), similarityData, conversation.getId(), dto.getModelId(), emitter);
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

    public void defaultAnswer(SseEmitter emitter) {
        defaultAnswer(emitter, DEFAULT_ANSWER);
    }

    private void defaultAnswer(SseEmitter emitter, String answer) {

        log.info("未匹配到相似度数据，使用默认回答：{}", answer);
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