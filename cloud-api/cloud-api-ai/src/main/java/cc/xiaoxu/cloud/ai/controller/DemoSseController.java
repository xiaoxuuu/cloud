package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.bean.ai.vo.SseVO;
import cc.xiaoxu.cloud.core.annotation.Wrap;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@Tag(name = "demo 演示 sse 流")
@RequestMapping("/demo/sse")
public class DemoSseController {

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Wrap(disabled = true)
    @RequestMapping(value = "/flux", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<SseVO<String>> flux() {

        return Flux.create(sink -> {
            try {
                sink.next(SseVO.start());
                sink.next(SseVO.id("181"));
                sink.next(SseVO.name("会话181"));
                for (char c : markdown.toCharArray()) {
                    Thread.sleep(new Random().nextInt(10) + 5);
                    sink.next(SseVO.msg(String.valueOf(c)));
                }
                sink.next(SseVO.end());
            } catch (Exception ignored) {
            } finally {
                sink.complete();
            }
        });
    }

    private static final Map<String, SseEmitter> sseMap = new ConcurrentHashMap<>();

    @Wrap(disabled = true)
    @RequestMapping(value = "/events", method = {RequestMethod.GET, RequestMethod.POST}, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter events(HttpServletResponse response) {

        // 设置响应的字符编码为 UTF-8
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("X-Accel-Buffering", "no");

        SseEmitter sseEmitter = sseMap.get("vo.getTalkId()");
        if (null != sseEmitter) {
            return sseEmitter;
        }

        SseEmitter emitter = new SseEmitter();
        log.error("new SseEmitter()");
        sseMap.put("vo.getTalkId()", emitter);

        Integer conversationId = 181;

        // 发送消息
        Runnable emitterSender = () -> {
            try {
                emitter.send(SseVO.start());
                emitter.send(SseVO.id(conversationId));
                emitter.send(SseVO.name("你好"));
                for (char c : markdown.toCharArray()) {
                    Thread.sleep(new Random().nextInt(20) + 10);
                    emitter.send(SseVO.msg(c));
                }
                emitter.send(SseVO.end());
            } catch (Exception ignored) {
            } finally {
                emitter.complete();
                sseMap.remove("vo.getTalkId()");
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