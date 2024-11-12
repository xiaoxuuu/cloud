package cc.xiaoxu.cloud.ai.manager.ai;

import cc.xiaoxu.cloud.bean.ai.dto.AiChatMessageDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiChatRoleEnum;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Prompt {

    public static List<AiChatMessageDTO> build(String systemPrompt, String userPrompt, Map<String, String> map) {

        systemPrompt = null == systemPrompt ? "" : systemPrompt;
        userPrompt = null == userPrompt ? "" : userPrompt;
        for (Map.Entry<String, String> k : map.entrySet()) {
            systemPrompt = systemPrompt.replace(k.getKey(), k.getValue());
            userPrompt = userPrompt.replace(k.getKey(), k.getValue());
        }
        List<AiChatMessageDTO> list = new ArrayList<>();
        if (StringUtils.isNotBlank(systemPrompt)) {
            AiChatMessageDTO system = AiChatMessageDTO.builder()
                    .role(AiChatRoleEnum.SYSTEM.getCode())
                    .content(systemPrompt)
                    .build();
            list.add(system);
        }
        if (StringUtils.isNotBlank(userPrompt)) {
            AiChatMessageDTO user = AiChatMessageDTO.builder()
                    .role(AiChatRoleEnum.USER.getCode())
                    .content(userPrompt)
                    .build();
            list.add(user);
        }
        return list;
    }

    /**
     * 知识库问答
     */
    public static class Ask {

        /**
         * 知识库问答
         */
        public static List<AiChatMessageDTO> v1(String knowledgeTitle, String question, String knowledgeData, String defaultAnswer) {

            Map<String, String> map = Map.of("{knowledgeTitle}", knowledgeTitle, "{question}", question, "{knowledgeData}", knowledgeData, "{defaultAnswer}", defaultAnswer);
            String systemPrompt = """
                    你是个经验丰富的知识总结专家，我将给你一些有关于 {knowledgeTitle} 的知识，然后我会对你提问。请你从知识库中总结答案，并回复
                    要求：
                    - 请使用简洁且专业的语言来回答问题。
                    - 如果你不知道答案，请回答“{defaultAnswer}”。
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

        /**
         * 知识库问答
         */
        public static List<AiChatMessageDTO> v2(String knowledgeTitle, String question, String knowledgeData, String defaultAnswer) {

            Map<String, String> map = Map.of("{knowledgeTitle}", knowledgeTitle, "{question}", question,
                    "{knowledgeData}", knowledgeData, "{defaultAnswer}", defaultAnswer);
            String systemPrompt = """
                    你是个经验丰富的知识总结专家，我将给你一些有关于 {knowledgeTitle} 的「知识列表」，然后我会对你提问。请你从「知识列表」中总结答案，并回复
                    要求：
                    - 请使用简洁且专业的语言来回答问题。
                    - 如果「知识列表」不包含问题的答案，请回答“{defaultAnswer}”。
                    - 如果一次提问多个问题，仅回答与「知识列表」相关的问题，无关问题直接忽略。
                    - 避免提及不属于「知识列表」中的知识，保证答案仅来源于「知识列表」。
                    - 请使用 Markdown 语法优化答案的格式。
                    - 「知识列表」中的图片、链接地址和脚本语言请直接返回。
                    - 请使用与问题相同的语言来回答。
                    """;
            String userPrompt = """
                    问题：{question}
                    知识列表：{knowledgeData}
                    """;
            return build(systemPrompt, userPrompt, map);
        }
    }


    /**
     * 测试
     */
    public static class Test {

        /**
         * 测试
         */
        public static List<AiChatMessageDTO> simple(String question) {

            Map<String, String> map = Map.of("{question}", question);
            String systemPrompt = "";
            String userPrompt = """
                    {question}
                    """;
            return build(systemPrompt, userPrompt, map);
        }

        /**
         * 测试
         */
        public static List<AiChatMessageDTO> v1(String question) {

            Map<String, String> map = Map.of("{question}", question);
            String systemPrompt = """
                    你是个经验丰富的专家，我将对你提问。请你回复
                    要求：
                    - 请使用简洁且专业的语言来回答问题。
                    - 请使用 Markdown 语法优化答案的格式。
                    - 请使用与问题相同的语言来回答。
                    """;
            String userPrompt = """
                    {question}
                    """;
            return build(systemPrompt, userPrompt, map);
        }
    }
}