package cc.xiaoxu.cloud.ai.manager.ai;

import cc.xiaoxu.cloud.bean.ai.dto.AiChatMessageDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiChatRoleEnum;

import java.util.List;
import java.util.Map;

public class Prompt {

    public static List<AiChatMessageDTO> build(String systemPrompt, String userPrompt, Map<String, String> map) {

        for (Map.Entry<String, String> k : map.entrySet()) {
            systemPrompt = systemPrompt.replace(k.getKey(), k.getValue());
            userPrompt = userPrompt.replace(k.getKey(), k.getValue());
        }
        AiChatMessageDTO system = AiChatMessageDTO.builder()
                .role(AiChatRoleEnum.SYSTEM.getCode())
                .content(systemPrompt)
                .build();
        AiChatMessageDTO user = AiChatMessageDTO.builder()
                .role(AiChatRoleEnum.USER.getCode())
                .content(userPrompt)
                .build();
        return List.of(system, user);
    }

    /**
     * 知识库问答
     */
    public static class Knowledge {

        /**
         * 知识库问答
         */
        public static List<AiChatMessageDTO> ask(String knowledgeTitle, String question, String knowledgeData) {

            Map<String, String> map = Map.of("{knowledgeTitle}", knowledgeTitle, "{question}", question, "{knowledgeData}", knowledgeData);
            String systemPrompt = """
                    你是个经验丰富的知识总结专家，我将给你一些有关于 {knowledgeTitle} 的知识，然后我会对你提问。请你从知识库中总结答案，并回复
                    要求：
                    - 请使用简洁且专业的语言来回答问题。
                    - 如果你不知道答案，请回答“没有在知识库中查找到相关信息，建议咨询相关技术支持或参考官方文档进行操作”。
                    - 避免提及你是从已知信息中获得的知识。
                    - 请保证答案与已知信息中描述的一致。
                    - 请使用 Markdown 语法优化答案的格式。
                    - 已知信息中的图片、链接地址和脚本语言请直接返回。
                    - 请使用与问题相同的语言来回答。
                    """;
            String userPrompt = """
                    问题：{question}
                    已知信息：{knowledgeData}
                    """;
            return build(systemPrompt, userPrompt, map);
        }
    }
}