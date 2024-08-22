package cc.xiaoxu.cloud.service;

import cc.xiaoxu.cloud.bean.ai.dto.AiChatMessageDTO;
import cc.xiaoxu.cloud.bean.ai.dto.AiChatResultDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiChatModelEnum;
import cc.xiaoxu.cloud.core.utils.PrettifyDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class AiResultLogService {

    public void saveLog(List<AiChatMessageDTO> chatMessageList, String talkType, AiChatModelEnum model, AiChatResultDTO chat, long l, String remark) {

        long i = (System.currentTimeMillis() - l);
        String result = "ai 调用完成，结果：" + chat.getStatusCode() + "，消耗 token：" + chat.getToken() + "，耗时：" + PrettifyDateTime.initWithMillisecond(i).prettify();
        log.info(result);
    }
}