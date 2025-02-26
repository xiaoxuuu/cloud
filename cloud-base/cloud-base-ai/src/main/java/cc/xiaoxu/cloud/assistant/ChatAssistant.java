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
             - 总结与分析使用中文返回，不论之前提供的数据是什么语言；
             - 你需要以 json 格式返回数据，不论分析出多条还是单条数据，都使用数组返回数据：{"res": [{日程1}, {日程2}]}；
             - 如果无法分析出网站信息，请返回 {"res": null}
            
            # 数据返回
            以下是 json 需要包含的字段：
             - website_name：网站标题，从我给出的信息中检索是否存在网站标题，如果不存在，此字段返回 null
             - short_name：网站短标题，从我给出的信息总结网站短标题，不超过 20 个字
             - description：对网站内容，功能进行总结，不超过 200 个字
            """)
    @UserMessage("""
            网站标题：{{websiteName}}
            网站数据：{{websiteData}}
            """)
    String analysis(@V("websiteName") String websiteName, @V("websiteData") String websiteData);
}