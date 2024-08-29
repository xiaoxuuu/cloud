package cc.xiaoxu.cloud.ai.manager;

import cc.xiaoxu.cloud.bean.ai.dto.AiChatMessageDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiChatModelEnum;
import cc.xiaoxu.cloud.bean.ai.enums.AiTalkTypeEnum;
import lombok.Data;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Data
public class ChatInfo {

    private List<AiChatMessageDTO> chatMessageList;
    private AiChatModelEnum modelTypeEnum;
    private AiTalkTypeEnum talkTypeEnum;
    private String apiKey;
    private Integer retryTime;
    private SseEmitter sseEmitter;
    private String remark;

    private ChatInfo() {
    }

    public static ChatInfo of(List<AiChatMessageDTO> chatMessageList, AiTalkTypeEnum talkTypeEnum, AiChatModelEnum modelTypeEnum) {
        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setChatMessageList(chatMessageList);
        chatInfo.setModelTypeEnum(modelTypeEnum);
        chatInfo.setTalkTypeEnum(talkTypeEnum);
        // 重试 5 次
        chatInfo.setRetryTime(5);
        return chatInfo;
    }

    public ChatInfo apiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    public ChatInfo retryTime(Integer retryTime) {
        this.retryTime = retryTime;
        return this;
    }

    public ChatInfo stream(SseEmitter sseEmitter) {
        this.sseEmitter = sseEmitter;
        return this;
    }

    public ChatInfo remark(String remark) {
        this.remark = remark;
        return this;
    }
}