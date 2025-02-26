package cc.xiaoxu.cloud.config;

import cc.xiaoxu.cloud.assistant.ChatAssistant;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI 配置
 * <p>
 * 2025/03/11 17:08
 *
 * @author XiaoXu
 */
@Slf4j
@Configuration
public class AiConfig {

    @Value("${app.ai.base-url}")
    private String baseUrl;

    @Value("${app.ai.apikey}")
    private String apikey;

    @Value("${app.ai.model-name}")
    private String modelName;

    @Bean
    public ChatAssistant getChatAssistant() {

        ChatLanguageModel model = OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .apiKey(apikey)
                .modelName(modelName)
//                .logRequests(true)
//                .logResponses(true)
                .build();

        return AiServices.builder(ChatAssistant.class)
                .chatLanguageModel(model)
                .build();
    }
}