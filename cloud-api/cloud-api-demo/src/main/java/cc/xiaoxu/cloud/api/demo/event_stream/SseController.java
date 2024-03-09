package cc.xiaoxu.cloud.api.demo.event_stream;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController("/sse")
public class SseController {

    private final CopyOnWriteArrayList<SseEmitter> emitterList = new CopyOnWriteArrayList<>();

    @GetMapping(value = "/events", produces = "text/event-stream")
    public SseEmitter events(HttpServletResponse response) throws IOException {

        // 设置响应的字符编码为 UTF-8
        response.setCharacterEncoding("UTF-8");

        SseEmitter emitter = new SseEmitter();
        // 保持对 SseEmitter 的引用，以便后续发送消息
        emitterList.add(emitter);

        // 发送初始消息
        emitter.send("初始化消息");

        // 定时发送消息
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    // 每秒发送一次消息
                    Thread.sleep(1000);
                    emitter.send("消息 " + (i + 1));
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
            // 发送完成消息并关闭连接
            try {
                emitter.send("完成");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            emitter.complete();
        }).start();

        return emitter;
    }

    // 用于关闭所有SseEmitter的辅助方法
    public void closeAllEmitters() {
        for (SseEmitter emitter : emitterList) {
            if (emitter != null) {
                emitter.complete();
            }
        }
    }
}