package cc.xiaoxu.cloud.ai.manager;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;

public interface Assistant {

    String chat(String userMessage);

    String chat(@MemoryId int memoryId, @UserMessage String userMessage);

    TokenStream chatStream(String userMessage);
}