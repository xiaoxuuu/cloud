package cc.xiaoxu.cloud.ai.manager;

import cc.xiaoxu.cloud.ai.service.ConversationService;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersistentChatMemoryStore implements ChatMemoryStore {

    @Resource
    private ConversationService conversationService;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        return null;
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {

    }

    @Override
    public void deleteMessages(Object memoryId) {

    }
}