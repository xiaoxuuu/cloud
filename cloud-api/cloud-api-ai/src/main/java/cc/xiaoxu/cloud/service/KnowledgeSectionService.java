package cc.xiaoxu.cloud.service;

import cc.xiaoxu.cloud.bean.dto.IdDTO;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.dao.KnowledgeSectionMapper;
import cc.xiaoxu.cloud.entity.KnowledgeSection;
import com.aliyun.bailian20231229.Client;
import com.aliyun.bailian20231229.models.CreateIndexRequest;
import com.aliyun.bailian20231229.models.CreateIndexResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class KnowledgeSectionService extends ServiceImpl<KnowledgeSectionMapper, KnowledgeSection> {

    @Value("${ali.bailian.api-key}")
    private String apiKey;

    @Value("${ali.access-key-id}")
    private String accessKeyId;

    @Value("${ali.access-key-secret}")
    private String accessKeySecret;

    public boolean rebuild(IdDTO dto) {

        return false;
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
        java.util.Map<String, String> headers = new java.util.HashMap<>();
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

    /**
     * <b>description</b> :
     * <p>使用AK&amp;SK初始化账号Client</p>
     * @return Client
     *
     * @throws Exception
     */
    public Client createClient() {
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