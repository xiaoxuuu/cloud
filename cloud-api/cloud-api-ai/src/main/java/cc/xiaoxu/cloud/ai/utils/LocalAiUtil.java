package cc.xiaoxu.cloud.ai.utils;

import cc.xiaoxu.cloud.bean.ai.dto.LocalVectorDTO;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.core.utils.OkHttpUtils;
import cc.xiaoxu.cloud.core.utils.bean.JsonUtils;
import okhttp3.Response;

import java.util.List;

public class LocalAiUtil {

    public static final String URL = "http://192.168.5.54:55555";
    private static final String SPLIT_BODY = """
            {
                "texts": %s,
                "truncate_dim": 1024
            }
            """;

    public static List<LocalVectorDTO> localVector(List<String> contentList) {

        List<LocalVectorDTO> vectorList;
        String formatted = SPLIT_BODY.formatted(JsonUtils.toString(contentList));
        try (Response response = OkHttpUtils.builder()
                .url(URL + "/embeddings")
                .body(formatted)
                .post(true)
                .syncResponse()) {
            String resultData = response.body().string();
            vectorList = JsonUtils.parseArray(resultData, LocalVectorDTO.class);
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
        return vectorList;
    }

    public static List<Double> vector(String text) {

        return localVector(List.of(text)).getFirst().getEmbedding();
    }
}
