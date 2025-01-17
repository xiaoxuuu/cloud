package cc.xiaoxu.cloud.ai.service;

import cc.xiaoxu.cloud.ai.dao.ConversationDetailContentMapper;
import cc.xiaoxu.cloud.ai.dao.ConversationDetailMapper;
import cc.xiaoxu.cloud.ai.entity.ConversationDetail;
import cc.xiaoxu.cloud.ai.entity.ConversationDetailContent;
import cc.xiaoxu.cloud.bean.ai.vo.ConversationDetailVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
        List<ConversationDetailContent> contentList = conversationDetailContentMapper.selectBatchIds(contentIdList);
        Map<Integer, String> contentMap = contentList.stream().collect(Collectors.toMap(ConversationDetailContent::getId, ConversationDetailContent::getContent));

        // 封装数据
        return conversationList.stream()
                .map(this::toConversationDetailVO)
                .peek(k -> k.setContent(contentMap.get(k.getContentId())))
                .toList();
    }

    private ConversationDetailVO toConversationDetailVO(ConversationDetail detail) {

        ConversationDetailVO vo = new ConversationDetailVO();
        vo.setConversationId(detail.getConversationId());
        vo.setContentId(detail.getContentId());
        vo.setCreateTime(detail.getCreateTime());
        vo.setModel(detail.getModel());
        vo.setRole(detail.getRole());
        vo.setToken(detail.getToken());
        return vo;
    }
}