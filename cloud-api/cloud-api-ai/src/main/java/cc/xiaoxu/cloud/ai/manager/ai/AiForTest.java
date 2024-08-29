package cc.xiaoxu.cloud.ai.manager.ai;

import cc.xiaoxu.cloud.bean.ai.dto.AiChatMessageDTO;
import cc.xiaoxu.cloud.bean.ai.dto.AiChatResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 测试调用
 */
@Slf4j
public class AiForTest {

    private static final String RESULT = """
            你好。
            """;

    protected static AiChatResultDTO test(List<AiChatMessageDTO> aiChatMessageDTOList) {

        return new AiChatResultDTO()
                .setResult(CollectionUtils.isEmpty(aiChatMessageDTOList) ? RESULT : aiChatMessageDTOList.getFirst().getContent())
                .setToken(0)
                .setStatusCode(200);
    }
}