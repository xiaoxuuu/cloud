package cc.xiaoxu.cloud.ai.manager.ai;

import cc.xiaoxu.cloud.bean.ai.dto.AiChatMessageDTO;
import cc.xiaoxu.cloud.bean.ai.dto.AiChatResultDTO;
import cc.xiaoxu.cloud.bean.ai.enums.AiChatModelEnum;
import cc.xiaoxu.cloud.bean.ai.vo.SseVO;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.annotation.JSONField;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 通用模型调用
 */
@Slf4j
public class AiForLocalQwen {

    private static final String HEADER_PREFIX = "Bearer ";
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String CHAT_COMPLETION_URL = "http://172.168.1.78:50004/v1/chat/completions";
    private static final String REQUEST_BODY = """
            {
                 "model": "%s",
                 "messages": %s,
                 "stream": %s
            }
            """;

    protected static AiChatResultDTO chat(List<AiChatMessageDTO> aiChatMessageDto, String apiKey, AiChatModelEnum model, SseEmitter sseEmitter) {
        if (null == sseEmitter) {
            return chatNotStream(aiChatMessageDto, apiKey, model);
        }
        return chatStream(aiChatMessageDto, apiKey, model, sseEmitter);
    }

    /**
     * 聊天
     * <p>字数限制 : {"error":{"message":"Invalid request: Your request exceeded model token limit: 131072","type":"invalid_request_error"}}</p>
     * @param model 模型
     * @param aiChatMessageDTOList 消息列表
     * @return json
     */
    @SneakyThrows
    private static AiChatResultDTO chatNotStream(@NonNull List<AiChatMessageDTO> aiChatMessageDTOList, String apiKey, @NonNull AiChatModelEnum model) {

        String requestBody = String.format(REQUEST_BODY, model.getCode(), JSON.toJSONString(aiChatMessageDTOList), false);
        Request okhttpRequest = new Request.Builder()
                .url(CHAT_COMPLETION_URL)
                .post(RequestBody.create(requestBody, MediaType.get("application/json")))
                .addHeader(HEADER_AUTHORIZATION, HEADER_PREFIX + apiKey)
                .build();
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(2 * 60, TimeUnit.SECONDS)
                .readTimeout(2 * 60, TimeUnit.SECONDS)
                .build();
        Response response = client.newCall(okhttpRequest).execute();
        String responseMsg = null == response.body() ? "response.body() is null" : response.body().string();
        if (response.isSuccessful()) {
            client.dispatcher().executorService().shutdown();
            return getContentResponse(responseMsg);
        } else {
            client.dispatcher().executorService().shutdown();
            return new AiChatResultDTO()
                    .setStatusCode(response.code())
                    .setErrorMsg(getErrorResponse(responseMsg));
        }
    }

    /**
     * 获取JSON中请求的数据
     * @param response 响应体
     * @return 响应体中的文字
     */
    private static AiChatResultDTO getContentResponse(String response) {

        if (StringUtils.isBlank(response)) {
            return new AiChatResultDTO()
                    .setStatusCode(200)
                    .setErrorMsg("response is empty");
        }
        AiChatResultDTO aiChatResultDTO = new AiChatResultDTO();
        com.alibaba.fastjson2.JSONObject jsonObject = JSON.parseObject(response);
        JSONArray choices = jsonObject.getJSONArray("choices");
        if (!choices.isEmpty()) {
            com.alibaba.fastjson2.JSONObject firstChoice = choices.getJSONObject(0);
            com.alibaba.fastjson2.JSONObject message = firstChoice.getJSONObject("message");
            aiChatResultDTO.setResult(message.getString("content"));
            int totalTokens = jsonObject.getJSONObject("usage").getIntValue("total_tokens");
            aiChatResultDTO.setToken(totalTokens);
            aiChatResultDTO.setStatusCode(200);
            return aiChatResultDTO;
        }
        return new AiChatResultDTO()
                .setStatusCode(200)
                .setErrorMsg("response.choices is empty");
    }

    /**
     * 获取JSON中请求的数据
     * @param response 响应体
     * @return 响应体中的文字
     */
    private static String getErrorResponse(String response) {

        if (StringUtils.isBlank(response)) {
            return "";
        }
        com.alibaba.fastjson2.JSONObject jsonObject = JSON.parseObject(response);
        com.alibaba.fastjson2.JSONObject error = jsonObject.getJSONObject("error");
        return error.getString("message");
    }

    private static AiChatResultDTO chatStream(List<AiChatMessageDTO> aiChatMessageDto, String apiKey, AiChatModelEnum model, SseEmitter emitter) {

        AiChatResultDTO resultDTO = new AiChatResultDTO();
        try {
            HttpURLConnection connection = getHttpURLConnection(aiChatMessageDto, apiKey, model);
            int statusCode = connection.getResponseCode();
            resultDTO.setStatusCode(statusCode);

            InputStream inputStream = statusCode == 200 ? connection.getInputStream() : connection.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            StringBuilder stringBuilder = new StringBuilder();
            if (200 == statusCode) {
                while ((line = reader.readLine()) != null) {
                    if ("data: [DONE]".equals(line)) {
                        break;
                    }
                    String data;
                    try {
                        data = getData(line);
                    } catch (KimiFinishException e) {
                        resultDTO.setToken(Integer.parseInt(e.getMessage()));
                        break;
                    }
                    if (StringUtils.isNotBlank(data)) {
                        emitter.send(SseVO.msg(data));
                        stringBuilder.append(data);
                    }
                }
                resultDTO.setResult(stringBuilder.toString());
            } else {
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                String result = stringBuilder.toString();
                if (StringUtils.isBlank(result)) {
                    resultDTO.setErrorMsg("empty error msg");
                    resultDTO.setStatusCode(400);
                } else {
                    ErrorResponse errorInfo = JSON.parseObject(result, ErrorResponse.class);
                    resultDTO.setErrorMsg(errorInfo.error.message);
                    resultDTO.setStatusCode(errorInfo.error.code);
                }
            }
            if (null != emitter) {
                emitter.send(SseVO.end());
                emitter.complete();
            }
            reader.close();
            inputStream.close();
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            resultDTO.setToken(0);
            resultDTO.setStatusCode(400);
            resultDTO.setErrorMsg(e.getMessage());
        }
        return resultDTO;
    }

    private static @NotNull HttpURLConnection getHttpURLConnection(List<AiChatMessageDTO> aiChatMessageDto, String apiKey, AiChatModelEnum model) throws IOException {
        URL url = URI.create(CHAT_COMPLETION_URL).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", org.springframework.http.MediaType.APPLICATION_JSON_VALUE);
        connection.setRequestProperty("Accept", org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE);
//        connection.setRequestProperty(HEADER_AUTHORIZATION, HEADER_PREFIX + apiKey);
        connection.setDoOutput(true);
        connection.setDoInput(true);

        String requestBody = String.format(REQUEST_BODY, model.getCode(), JSON.toJSONString(aiChatMessageDto), true);
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.write(requestBody.getBytes());
        wr.close();
        return connection;
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
        Choice choice = kimiResult.choices.get(0);
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

    private record ErrorResponse(ErrorInfo error) {
    }

    private record ErrorInfo(@JSONField(name = "code") int code, @JSONField(name = "message") String message,
                             @JSONField(name = "param") String param, @JSONField(name = "type") String type,
                             @JSONField(name = "innererror") String innerError) {
    }
}