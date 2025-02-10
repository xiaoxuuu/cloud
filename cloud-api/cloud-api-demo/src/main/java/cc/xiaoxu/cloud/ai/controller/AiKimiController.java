package cc.xiaoxu.cloud.ai.controller;

import cc.xiaoxu.cloud.ai.bean.KimiVO;
import cc.xiaoxu.cloud.ai.bean.SseVO;
import cc.xiaoxu.cloud.core.annotation.Wrap;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.annotation.JSONField;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@Tag(name = "AI kimi 对话")
@RequestMapping("/ai/kimi")
public class AiKimiController {

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private static final Map<String, SseEmitter> sseMap = new ConcurrentHashMap<>();

    @Wrap(disabled = true)
    @PostMapping(value = "/talk", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter events(@RequestBody KimiVO vo, HttpServletResponse response) {

        setResponseHeader(response);

        SseEmitter sseEmitter = sseMap.get(vo.getTalkId());
        if (sseEmitter != null) {
            return sseEmitter;
        }

        SseEmitter emitter = new SseEmitter();
        sseMap.put(vo.getTalkId(), emitter);

        // 发送消息
        Runnable emitterSender = () -> {
            try {
                emitter.send(SseVO.start());
                emitter.send(SseVO.id(vo.getTalkId()));
                emitter.send(SseVO.name(vo.getQuestion()));
                talkToKimi(vo, emitter);
                emitter.send(SseVO.end());
            } catch (Exception ignored) {
            } finally {
                emitter.complete();
                sseMap.remove(vo.getTalkId());
            }
        };
        threadPoolTaskExecutor.execute(emitterSender);
        return emitter;
    }

    public static void setResponseHeader(HttpServletResponse response) {
        // 设置响应的字符编码为 UTF-8
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Connection", "keep-alive");
        response.setHeader("X-Accel-Buffering", "no");
    }

    private static final String REQUEST_BODY = """
            {
                 "model": "moonshot-v1-8k",
                 "messages": [
                     {"role": "system", "content": "你是 Kimi，由 Moonshot AI 提供的人工智能助手，你更擅长中文和英文的对话。你会为用户提供安全，有帮助，准确的回答。同时，你会拒绝一切涉及恐怖主义，种族歧视，黄色暴力等问题的回答。Moonshot AI 为专有名词，不可翻译成其他语言。"},
                     {"role": "user", "content": "%s"}
                 ],
                 "stream": true
            }
            """;

    public static void talkToKimi(KimiVO vo, SseEmitter emitter) {

        try {
            URL url = URI.create("https://api.moonshot.cn/v1/chat/completions").toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            connection.setRequestProperty("Accept", MediaType.TEXT_EVENT_STREAM_VALUE);
            connection.setRequestProperty("Authorization", "Bearer " + vo.getApiKey());
            connection.setDoOutput(true);
            connection.setDoInput(true);

            //构造发送内容
            String requestBody = String.format(REQUEST_BODY, vo.getQuestion());

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.write(requestBody.getBytes());
            wr.close();

            int statusCode = connection.getResponseCode();
            log.error("调用状态：{}", statusCode);
            InputStream inputStream = statusCode == 200 ? connection.getInputStream() : connection.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                if ("data: [DONE]".equals(line)) {
                    break;
                }
                String data = null;
                try {
                    data = getData(line);
                } catch (KimiFinishException e) {
                    log.error("KIMI token: {}", e.getMessage());
                    break;
                }
                if (StringUtils.isNotBlank(data)) {
                    emitter.send(SseVO.msg(data));
                }
            }
            reader.close();
            inputStream.close();
            connection.disconnect();
            log.error("结束");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getData(String line) {
        if (StringUtils.isBlank(line)) {
            return null;
        }
        String data = line.replaceFirst("data: ", "");
        KimiResult kimiResult = JSON.parseObject(data, KimiResult.class);
        if (null == kimiResult) {
            return null;
        }
        if (CollectionUtils.isEmpty(kimiResult.choices)) {
            return null;
        }
        Choice choice = kimiResult.choices.getFirst();
        if ("stop".equals(choice.finishReason)) {
            if (null == choice.usage) {
                return null;
            }
            if (null == choice.usage.totalTokens) {
                return null;
            }
            throw new KimiFinishException("" + choice.usage.totalTokens);
        }

        if (null == choice.delta) {
            return null;
        }
        return choice.delta.content;
    }

    private record KimiResult(String id, String object, int created, String model, List<Choice> choices) {
    }

    private record Choice(Integer index, Delta delta, @JSONField(name = "finish_reason") String finishReason,
                          Usage usage) {
    }

    private record Delta(String role, String content) {
    }

    private record Usage(@JSONField(name = "prompt_tokens") Integer promptTokens,
                         @JSONField(name = "completion_tokens") Integer completionTokens,
                         @JSONField(name = "total_tokens") Integer totalTokens) {
    }

    private static class KimiFinishException extends RuntimeException {

        public KimiFinishException(String message) {
            super(message);
        }
    }
}