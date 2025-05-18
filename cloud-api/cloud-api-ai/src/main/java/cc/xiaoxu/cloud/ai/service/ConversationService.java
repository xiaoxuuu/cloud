package cc.xiaoxu.cloud.ai.service;

import cc.xiaoxu.cloud.ai.dao.ConversationKnowledgeMapper;
import cc.xiaoxu.cloud.ai.dao.ConversationMapper;
import cc.xiaoxu.cloud.ai.dao.ModelInfoMapper;
import cc.xiaoxu.cloud.ai.entity.Conversation;
import cc.xiaoxu.cloud.ai.entity.ConversationDetail;
import cc.xiaoxu.cloud.ai.entity.ConversationKnowledge;
import cc.xiaoxu.cloud.ai.entity.ModelInfo;
import cc.xiaoxu.cloud.ai.manager.AiManager;
import cc.xiaoxu.cloud.bean.ai.dto.ConversationAddDTO;
import cc.xiaoxu.cloud.bean.ai.dto.ConversationEditDTO;
import cc.xiaoxu.cloud.bean.ai.dto.ConversationListDTO;
import cc.xiaoxu.cloud.bean.ai.dto.ConversationPageDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiChatRoleEnum;
import cc.xiaoxu.cloud.bean.ai.vo.ConversationVO;
import cc.xiaoxu.cloud.bean.ai.vo.KnowledgeSectionExpandVO;
import cc.xiaoxu.cloud.bean.ai.vo.SseVO;
import cc.xiaoxu.cloud.bean.enums.StateEnum;
import cc.xiaoxu.cloud.core.bean.entity.BaseEntity;
import cc.xiaoxu.cloud.core.utils.PageUtils;
import cc.xiaoxu.cloud.core.utils.StopWatchUtil;
import cc.xiaoxu.cloud.core.utils.bean.BeanUtils;
import cc.xiaoxu.cloud.core.utils.date.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    private final ConversationKnowledgeMapper conversationKnowledgeMapper;
    private final ConversationDetailService conversationDetailService;
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

        String promot = """
                    你是个经验丰富的知识总结专家，我将给你一些「知识列表」，然后我会对你提问。请你从「知识列表」中总结答案，并回复
                要求：
                - 请使用简洁且专业的语言回答问题；
                - 请使用与问题相同的语言回答问题。
                - 请使用 Markdown 语法优化答案的格式。
                - 避免提及不属于「知识列表」中的知识，保证答案仅来源于「知识列表」。
                - 「知识列表」中的图片、链接地址和脚本语言请直接返回。
                - 如果「知识列表」不包含问题的答案，请回复「没有在知识库中查找到相关信息，请调整问题描述或更新知识库」。
                - 如果提问多个问题，仅回答与「知识列表」相关的问题，无关问题直接忽略。
                """;
        conversationDetailService.create(promot, entity.getId(), userId, modelId, AiChatRoleEnum.SYSTEM, promot.length());

        return entity;
    }

    @NotNull
    private static Conversation getConversation(String question, Integer userId, Integer modelId) {
        Conversation entity = new Conversation();
        entity.setName(question);
        entity.setUserId(userId);
        entity.setModelId(modelId);
        BaseEntity.buildCreate(entity, userId);
        return entity;
    }

    @SneakyThrows
    public void talk(SseEmitter emitter, StopWatchUtil sw, ConversationAddDTO dto, List<KnowledgeSectionExpandVO> similarityDataList, Integer userId) {

        sw.start("获取对话数据");
        Conversation conversation = getOrCreateConversation(dto.getConversationId(), dto.getQuestion(), userId, dto.getModelId());

        sw.start("获取模型数据");
        ModelInfo modelInfo = modelInfoMapper.selectById(conversation.getModelId());

        emitter.send(SseVO.start());
        emitter.send(SseVO.id(conversation.getId()));

        sw.start("知识整理");
        String similarityData = getSimilarityData(emitter, similarityDataList);

        // 保存提问信息
        String q = """
                问题：{{question}}
                知识列表：{{knowledgeData}}
                """;
        ConversationDetail question = conversationDetailService.create(q.replace("{{question}}", dto.getQuestion()).replace("{{knowledgeData}}", similarityData), conversation.getId(), userId, modelInfo.getId(), AiChatRoleEnum.USER, dto.getQuestion().length());

        sw.start("提问");
        aiManager.knowledge(dto.getQuestion(), similarityData, conversation.getId(), modelInfo, emitter, userId, question);
    }

    private String getSimilarityData(SseEmitter emitter, List<KnowledgeSectionExpandVO> similarityData) {

        String distanceList = similarityData.stream()
                .map(KnowledgeSectionExpandVO::getDistance)
                .map(String::toString)
                .map(k -> k.length() < 6 ? k : k.substring(0, 5))
                .collect(Collectors.joining(","));
        if (null != emitter) {
            try {
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

    public Page<ConversationVO> pages(ConversationPageDTO dto, Integer userId) {

        Page<Conversation> entityPage = lambdaQuery()
                .eq(Conversation::getUserId, userId)
                .ne(Conversation::getState, StateEnum.DELETE.getCode())
                .like(StringUtils.isBlank(dto.getName()), Conversation::getName, dto.getName())
                .page(PageUtils.getPageCondition(dto));

        Page<ConversationVO> page = PageUtils.getPage(entityPage, ConversationVO.class);

        addExpandInfo(page.getRecords());

        return page;
    }

    private void addExpandInfo(List<ConversationVO> list) {

        // 查询对话引用知识库
        List<Integer> conversationIdList = list.stream().map(ConversationVO::getId).toList();
        LambdaQueryWrapper<ConversationKnowledge> wrapper = new LambdaQueryWrapper<ConversationKnowledge>().in(ConversationKnowledge::getConversationId, conversationIdList);
        List<ConversationKnowledge> conversationKnowledgeList = conversationKnowledgeMapper.selectList(wrapper);
        Map<Integer, List<Integer>> conversationKnowledgeLMap = conversationKnowledgeList.stream()
                .collect(Collectors.groupingBy(ConversationKnowledge::getConversationId, Collectors.mapping(ConversationKnowledge::getKnowledgeBaseId, Collectors.toList())));

        for (ConversationVO record : list) {
            record.setKnowledgeBaseIdList(conversationKnowledgeLMap.get(record.getId()));
        }
    }

    public List<ConversationVO> lists(ConversationListDTO dto, Integer userId) {

        List<Conversation> entityList = lambdaQuery()
                .eq(Conversation::getUserId, userId)
                .ne(Conversation::getState, StateEnum.DELETE.getCode())
                .like(StringUtils.isBlank(dto.getName()), Conversation::getName, dto.getName())
                .list();

        List<ConversationVO> list = BeanUtils.populateList(entityList, ConversationVO.class);
        addExpandInfo(list);
        return list;
    }

    public void edit(ConversationEditDTO dto, Integer userId) {

        if (StringUtils.isNotBlank(dto.getName()) || null != dto.getModelId()) {
            lambdaUpdate()
                    .eq(Conversation::getUserId, userId)
                    .in(Conversation::getId, dto.getId())
                    .set(StringUtils.isNotBlank(dto.getName()), Conversation::getName, dto.getName())
                    .set(null != dto.getModelId(), Conversation::getModelId, dto.getModelId())
                    .set(Conversation::getModifyId, userId)
                    .set(Conversation::getModifyTime, DateUtils.getNowDate())
                    .update();
        }
    }
}