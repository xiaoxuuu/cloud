package cc.xiaoxu.cloud.controller;

import com.alibaba.dashscope.embeddings.*;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;

import java.util.Arrays;

public class AiALiController {

    public static void textCall() throws ApiException, NoApiKeyException {
        TextEmbeddingParam param = TextEmbeddingParam
                .builder()
                .model(TextEmbedding.Models.TEXT_EMBEDDING_V2)
                .texts(Arrays.asList("风急天高猿啸哀", "渚清沙白鸟飞回", "无边落木萧萧下", "不尽长江滚滚来")).build();
        TextEmbedding textEmbedding = new TextEmbedding();
        TextEmbeddingResult result = textEmbedding.call(param);
        System.out.println(result);
    }

    /**
     * 向量转换
     */
    public void fileCall() throws ApiException, NoApiKeyException {
        BatchTextEmbeddingParam param = BatchTextEmbeddingParam.builder()
                .model(BatchTextEmbedding.Models.TEXT_EMBEDDING_ASYNC_V2)
                .apiKey("apiKey")
                .url("https://modelscope.oss-cn-beijing.aliyuncs.com/resource/text_embedding_file.txt")
                .build();
        BatchTextEmbedding textEmbedding = new BatchTextEmbedding();
        BatchTextEmbeddingResult result = textEmbedding.call(param);
        System.out.println(result);
    }
}