package cc.xiaoxu.cloud.assistant;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface ChatAssistant {

    @SystemMessage("""
            你是个经验丰富的总结专家，我将给你一个网站的标题以及参考数据。请你数据总结并回复。请注意，你需要满足以下要求：
            
            # 工作流程
             - 根据数据总结网站功能，尽可能精确，不要使用模糊词汇；
             - 总结的数据将用于搜索，尽可能全面；
             - 始终使用中文返回，不论之前提供的数据是什么语言；
             - 网站标题、网站数据是使用爬虫获取的，可能出现获取失败的情况，你需要判断并排除这种失败数据；
             - 网站短标题是我总结的，请尽可能保持我的原意，只进行排版，如果我未给出短标题，那么从所有数据中进行总结；
             - 你需要以 json 格式返回数据，{"website_name": "", "short_name": "", "description": ""}
             - 如果无法分析出网站信息，请返回 {}
            
            # 数据返回
            以下是 json 需要包含的字段：
             - website_name：网站标题，从我给出的信息中检索是否存在网站标题，如果不存在，此字段返回 null
             - short_name：网站短标题，要能一眼看出这个网站的功能，不超过 20 个字
             - description：围绕网站链接、网站标题、网站短标题，对网站内容、功能、描述进行总结，提炼关键词。尽可能多输出，但不超过 200 个字。
            """)
    @UserMessage("""
            网站链接：{{url}}
            网站短标题：{{shortName}}
            网站标题：{{websiteName}}
            网站数据：{{websiteData}}
            """)
    String analysis(@V("url") String url, @V("shortName") String shortName, @V("websiteName") String websiteName, @V("websiteData") String websiteData);
}