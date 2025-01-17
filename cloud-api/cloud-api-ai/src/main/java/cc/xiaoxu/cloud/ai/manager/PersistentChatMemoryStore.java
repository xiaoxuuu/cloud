package cc.xiaoxu.cloud.ai.manager;

import cc.xiaoxu.cloud.ai.service.ConversationDetailService;
import cc.xiaoxu.cloud.bean.ai.enums.AiChatRoleEnum;
import cc.xiaoxu.cloud.bean.ai.vo.ConversationDetailVO;
import cc.xiaoxu.cloud.core.utils.enums.EnumUtils;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersistentChatMemoryStore implements ChatMemoryStore {

    @Resource
    private ConversationDetailService conversationDetailService;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {

        List<ConversationDetailVO> conversationDetailList = conversationDetailService.getConversationDetailList((int) memoryId);
        return conversationDetailList.stream().map(k -> {
            AiChatRoleEnum roleEnum = EnumUtils.getByClass(k.getModel(), AiChatRoleEnum.class);
            return switch (roleEnum) {
                case AI -> new AiMessage(k.getContent());
                case SYSTEM -> new SystemMessage(k.getContent());
                case USER -> new UserMessage(k.getContent());
            };
        }).toList();
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {

    }

    @Override
    public void deleteMessages(Object memoryId) {

    }
}