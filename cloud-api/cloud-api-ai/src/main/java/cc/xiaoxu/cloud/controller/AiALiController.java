package cc.xiaoxu.cloud.controller;

import com.alibaba.dashscope.embeddings.BatchTextEmbedding;
import com.alibaba.dashscope.embeddings.BatchTextEmbeddingParam;
import com.alibaba.dashscope.embeddings.BatchTextEmbeddingResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;

public class AiALiController {

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