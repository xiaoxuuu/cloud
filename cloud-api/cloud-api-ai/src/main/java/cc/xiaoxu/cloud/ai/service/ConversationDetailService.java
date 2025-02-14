package cc.xiaoxu.cloud.ai.service;

import cc.xiaoxu.cloud.ai.dao.ConversationDetailContentMapper;
import cc.xiaoxu.cloud.ai.dao.ConversationDetailMapper;
import cc.xiaoxu.cloud.ai.entity.ConversationDetail;
import cc.xiaoxu.cloud.ai.entity.ConversationDetailContent;
import cc.xiaoxu.cloud.bean.ai.enums.AiChatRoleEnum;
import cc.xiaoxu.cloud.bean.ai.vo.ConversationDetailVO;
import cc.xiaoxu.cloud.core.utils.DateUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ConversationDetailService extends ServiceImpl<ConversationDetailMapper, ConversationDetail> {

    private final ConversationDetailContentMapper conversationDetailContentMapper;

    public List<ConversationDetailVO> getConversationDetailList(Integer conversationId) {

        // 查询对话
        List<ConversationDetail> conversationList = lambdaQuery().eq(ConversationDetail::getConversationId, conversationId).list();
        List<Integer> contentIdList = conversationList.stream().map(ConversationDetail::getContentId).toList();

        // 查询每句话
        Map<Integer, String> contentMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(contentIdList)) {
            List<ConversationDetailContent> contentList = conversationDetailContentMapper.selectBatchIds(contentIdList);
            contentMap = contentList.stream().collect(Collectors.toMap(ConversationDetailContent::getId, ConversationDetailContent::getContent));
        }

        // 封装数据
        Map<Integer, String> finalContentMap = contentMap;
        return conversationList.stream()
                .sorted(Comparator.comparing(ConversationDetail::getId))
                .map(this::toConversationDetailVO)
                .peek(k -> k.setContent(finalContentMap.get(k.getContentId())))
                .toList();
    }

    private ConversationDetailVO toConversationDetailVO(ConversationDetail detail) {

        ConversationDetailVO vo = new ConversationDetailVO();
        vo.setConversationId(detail.getConversationId());
        vo.setContentId(detail.getContentId());
        vo.setCreateTime(detail.getCreateTime());
        vo.setModelId(detail.getModelId());
        vo.setRole(detail.getRole());
        vo.setToken(detail.getToken());
        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    public ConversationDetail create(String question, Integer conversationId, Integer userId, Integer modelId, AiChatRoleEnum role, Integer token) {

        // 保存对话内容
        ConversationDetailContent content = new ConversationDetailContent();
        content.setContent(question);
        conversationDetailContentMapper.insert(content);

        ConversationDetail conversationDetail = new ConversationDetail();
        conversationDetail.setConversationId(conversationId);
        conversationDetail.setUserId(userId);
        conversationDetail.setCreateTime(DateUtils.getNowDate());
        conversationDetail.setModelId(modelId);
        conversationDetail.setRole(role.getCode());
        conversationDetail.setToken(token);
        conversationDetail.setContentId(content.getId());
        save(conversationDetail);

        return conversationDetail;
    }
}