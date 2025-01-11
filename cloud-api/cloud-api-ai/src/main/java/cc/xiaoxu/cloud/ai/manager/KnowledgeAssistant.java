package cc.xiaoxu.cloud.ai.manager;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface KnowledgeAssistant {

    String chat(String userMessage);

    @SystemMessage("""
            你是个经验丰富的知识总结专家，我将给你一些「知识列表」，然后我会对你提问。请你从「知识列表」中总结答案，并回复
            要求：
            - 请使用简洁且专业的语言来回答问题。
            - 如果「知识列表」不包含问题的答案，请回复「没有在知识库中查找到相关信息，请调整问题描述或更新知识库」。
            - 如果一次提问多个问题，仅回答与「知识列表」相关的问题，无关问题直接忽略。
            - 避免提及不属于「知识列表」中的知识，保证答案仅来源于「知识列表」。
            - 请使用 Markdown 语法优化答案的格式。
            - 「知识列表」中的图片、链接地址和脚本语言请直接返回。
            - 请使用与问题相同的语言来回答。
            """)
    @UserMessage("""
            问题：{{question}}
            知识列表：{{knowledgeData}}
            """)
    TokenStream knowledge(@V("question") String question, @V("knowledgeData") String knowledgeData);
}