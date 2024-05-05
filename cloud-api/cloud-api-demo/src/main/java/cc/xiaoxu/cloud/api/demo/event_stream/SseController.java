package cc.xiaoxu.cloud.api.demo.event_stream;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Random;

@RestController
@RequestMapping("/sse")
public class SseController {

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @RequestMapping(value = "/events", method = {RequestMethod.GET, RequestMethod.POST}, produces = "text/event-stream")
    public SseEmitter events(HttpServletResponse response) {

        // 设置响应的字符编码为 UTF-8
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Type", "text/event-steam");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");

        SseEmitter emitter = new SseEmitter();

        Integer conversationId = 181;

        // 定时发送消息
        Runnable emitterSender = () -> {
            try {
                Random random = new Random();
                emitter.send(new JsonResult<>("START", null));
                emitter.send(new JsonResult<>("ID", conversationId));
                emitter.send(new JsonResult<>("NAME", "你好"));
                for (char c : markdown.toCharArray()) {
                    Thread.sleep(random.nextInt(200) + 50);
                    emitter.send(new JsonResult<>("MSG", c));
                }

                emitter.send(new JsonResult<>("END", null));
            } catch (Exception ignored) {
            } finally {
                emitter.complete();
            }
        };
        threadPoolTaskExecutor.execute(emitterSender);
        return emitter;
    }

    public static final String markdown = """
            # Markdown 样例
                            
            ## 一级标题
            ### 二级标题
            #### 三级标题
            ##### 四级标题
            ###### 五级标题
                            
            ## 段落和文本样式
            这是一段普通文本。可以使用 `*` 或者 `_` 来添加 *斜体* 或者 _斜体_。
            可以使用两个 `**` 或者 `__` 来添加 **粗体** 或者 __粗体__。
            可以使用三个 `\\*\\*\\*` 或者 `___` 来添加 ***粗斜体*** 或者 ___粗斜体___。
                            
            ## 列表
            ### 无序列表
            - 列表项一
            - 列表项二
              - 子列表项一
              - 子列表项二
                            
            ### 有序列表
            1. 第一项
            2. 第二项
               1. 第二项的第一个子项
               2. 第二项的第二个子项
                            
            ## 链接和图片
            [这是一个链接](http://www.example.com)
                            
            ![这是图片的alt文字](http://www.example.com/image.jpg)
                            
            ## 代码
            ### 行内代码
            这是一段 `行内代码`。
                            
            ### 缩进式代码块
                这是一段缩进的代码。
                你可以在需要的地方添加更多行代码。
                            
            ### 围栏式代码块
            ```java
            public class HelloWorld {
                public static void main(String[] args) {
                    System.out.println("Hello, World!");
                }
            }
            ```
                            
            ## 引用
                            
            > 这是一个引用文本。
            >
            > > 这是一个嵌套的引用。
                            
            ## 分隔线
                            
            ------
                            
            这是一条分隔线。
                            
            ## 表格
                            
            | 表头1   | 表头2   | 表头3   |
            | ------- | ------- | ------- |
            | 单元格1 | 单元格2 | 单元格3 |
            | 单元格4 | 单元格5 | 单元格6 |
                            
            ## 任务列表
                            
            -  已完成项
            -  未完成项
                            
            ## 表情符号
                            
            :smile:
            :heart:
            """;
}