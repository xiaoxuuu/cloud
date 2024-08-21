package cc.xiaoxu.cloud.service;

import cc.xiaoxu.cloud.core.exception.CustomException;
import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;
import com.alibaba.dashscope.embeddings.TextEmbeddingResultItem;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.aliyun.bailian20231229.Client;
import com.aliyun.bailian20231229.models.*;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ALiYunService {

    @Value("${ali.bailian.api-key}")
    private String apiKey;

    @Value("${ali.access-key-id}")
    private String accessKeyId;

    @Value("${ali.access-key-secret}")
    private String accessKeySecret;

    /**
     * 文本向量化计算
     * @param textList 文本
     * @return 向量化结果
     */
    public List<TextEmbeddingResultItem> vector(List<String> textList) {
        TextEmbeddingParam param = TextEmbeddingParam
                .builder()
                .model(TextEmbedding.Models.TEXT_EMBEDDING_V2)
                .apiKey(apiKey)
                .texts(textList).build();
        TextEmbedding textEmbedding = new TextEmbedding();
        TextEmbeddingResult result;
        try {
            result = textEmbedding.call(param);
        } catch (NoApiKeyException e) {
            throw new CustomException(e.getMessage());
        }
        return result.getOutput().getEmbeddings();
    }

    public void createIndex(String indexName, String fileId, String workspaceId) {

        Client client = createClient();
        CreateIndexRequest createIndexRequest = new CreateIndexRequest()
                .setName(indexName)
                .setStructureType("unstructured")
                .setEmbeddingModelName("text-embedding-v2")
                .setRerankModelName("gte-rerank-hybrid")
                .setChunkSize(2048)
                .setOverlapSize(16)
                .setSourceType("DATA_CENTER_FILE")
                .setDocumentIds(List.of(fileId))
                .setSinkType("DEFAULT");
        RuntimeOptions runtime = new RuntimeOptions();
        Map<String, String> headers = new HashMap<>();
        try {
            CreateIndexResponse indexWithOptions = client.createIndexWithOptions(workspaceId, createIndexRequest, headers, runtime);
            // 处理结果
        } catch (TeaException error) {
            String errorMsg = "createIndex 调用失败：" + error.getMessage() + "，诊断地址：" + error.getData().get("Recommend");
            throw new CustomException(errorMsg);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            String errorMsg = "createIndex 调用失败：" + error.getMessage() + "，诊断地址：" + error.getData().get("Recommend");
            throw new CustomException(errorMsg);
        }
    }

    public void submitTask(String indexId, String workspaceId) {

        Client client = createClient();
        SubmitIndexJobRequest submitIndexJobRequest = new SubmitIndexJobRequest().setIndexId(indexId);
        RuntimeOptions runtime = new RuntimeOptions();
        Map<String, String> headers = new HashMap<>();
        try {
            SubmitIndexJobResponse submitIndexJobResponse = client.submitIndexJobWithOptions(workspaceId, submitIndexJobRequest, headers, runtime);
            // 处理结果
        } catch (TeaException error) {
            String errorMsg = "submitTask 调用失败：" + error.getMessage() + "，诊断地址：" + error.getData().get("Recommend");
            throw new CustomException(errorMsg);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            String errorMsg = "submitTask 调用失败：" + error.getMessage() + "，诊断地址：" + error.getData().get("Recommend");
            throw new CustomException(errorMsg);
        }
    }

    public List<String> readSection(String indexId, String fileId, String workspaceId, Integer pageNum, Integer pageSize) {

        Client client = createClient();
        ListChunksRequest listChunksRequest = new ListChunksRequest()
                .setIndexId(indexId)
                .setFiled(fileId)
                .setPageNum(pageNum)
                .setPageSize(pageSize)
                .setFields(Arrays.asList("content"));
        RuntimeOptions runtime = new RuntimeOptions();
        Map<String, String> headers = new HashMap<>();
        try {
            // 复制代码运行请自行打印 API 的返回值
            ListChunksResponse listChunksResponse = client.listChunksWithOptions(workspaceId, listChunksRequest, headers, runtime);
            return listChunksResponse.body.getData().nodes.stream().map(ListChunksResponseBody.ListChunksResponseBodyDataNodes::getText).toList();
        } catch (TeaException error) {
            String errorMsg = "readSection 调用失败：" + error.getMessage() + "，诊断地址：" + error.getData().get("Recommend");
            throw new CustomException(errorMsg);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            String errorMsg = "readSection 调用失败：" + error.getMessage() + "，诊断地址：" + error.getData().get("Recommend");
            throw new CustomException(errorMsg);
        }
    }

    /**
     * <b>description</b> :
     * <p>使用AK&amp;SK初始化账号Client</p>
     * @return Client
     */
    private Client createClient() {
        // 工程代码泄露可能会导致 AccessKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考。
        // 建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html。
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        // Endpoint 请参考 https://api.aliyun.com/product/bailian
        config.endpoint = "bailian.cn-beijing.aliyuncs.com";
        try {
            return new Client(config);
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
    }
}