package cc.xiaoxu.cloud.api.demo.event_stream;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
                emitter.send(new JsonResult<>("START", null));
                emitter.send(new JsonResult<>("ID", conversationId));
                emitter.send(new JsonResult<>("NAME", "你好"));
                for (int i = 0; i < 20; i++) {

                    // 每秒发送一次消息
                    Thread.sleep(100);
                    emitter.send(new JsonResult<>("MSG", "" + (i + 1)));

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
}