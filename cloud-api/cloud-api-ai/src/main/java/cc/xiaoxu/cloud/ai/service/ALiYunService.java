package cc.xiaoxu.cloud.ai.service;

import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.core.utils.text.MD5Utils;
import com.alibaba.dashscope.embeddings.*;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.aliyun.bailian20231229.Client;
import com.aliyun.bailian20231229.models.*;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
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

    @Value("${env.api.ali.bailian.workspace.id}")
    private String workspaceId;

    @Value("${env.api.ali.bailian.category.id}")
    private String categoryId;

    @Value("${env.api.ali.bailian.index.id}")
    private String indexId;

    /**
     * 文本向量化计算
     * @param text 文本
     * @return 向量化结果
     */
    public List<Double> vector(String text) {

        return vector(List.of(text)).getFirst().getEmbedding();
    }

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

    /**
     * 向量转换-批量
     * @param url https://modelscope.oss-cn-beijing.aliyuncs.com/resource/text_embedding_file.txt
     * @return
     */
    public String vectorUrl(String url) {
        BatchTextEmbeddingParam param = BatchTextEmbeddingParam.builder()
                .model(BatchTextEmbedding.Models.TEXT_EMBEDDING_ASYNC_V2)
                .apiKey(apiKey)
                .url(url)
                .build();
        BatchTextEmbedding textEmbedding = new BatchTextEmbedding();
        BatchTextEmbeddingResult result = null;
        try {
            result = textEmbedding.call(param);
        } catch (NoApiKeyException e) {
            throw new CustomException(e.getMessage());
        }
        return result.getOutput().getUrl();
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
            String errorMsg = "接口 createIndex 调用失败：" + error.getMessage() + "，诊断地址：" + error.getData().get("Recommend");
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
            String errorMsg = "接口 submitTask 调用失败：" + error.getMessage() + "，诊断地址：" + error.getData().get("Recommend");
            throw new CustomException(errorMsg);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            String errorMsg = "submitTask 调用失败：" + error.getMessage() + "，诊断地址：" + error.getData().get("Recommend");
            throw new CustomException(errorMsg);
        }
    }

    public List<String> readSection(String fileId, Integer pageNum, Integer pageSize) {

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
            String errorMsg = "接口 readSection 调用失败：" + error.getMessage() + "，诊断地址：" + error.getData().get("Recommend");
            throw new CustomException(errorMsg);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            String errorMsg = "readSection 调用失败：" + error.getMessage() + "，诊断地址：" + error.getData().get("Recommend");
            throw new CustomException(errorMsg);
        }
    }

    public String uploadFile(MultipartFile file) {

        ApplyFileUploadLeaseResponseBody.ApplyFileUploadLeaseResponseBodyData appliedFileUploadLease = applyFileUploadLease(categoryId, workspaceId, file);
        uploadFile(file, appliedFileUploadLease);
        AddFileResponseBody.AddFileResponseBodyData addFileResponseBodyData = addFile(appliedFileUploadLease.getFileUploadLeaseId(), categoryId, workspaceId);
        return addFileResponseBodyData.getFileId();
    }

    /**
     * <p>申请文档上传组约</p>
     * <p>该接口用于支持百炼数据中心-数据管理模块文档上传操作，用于申请一个文件上传租约（返回一个HTTP链接），并将文件上传到百炼指定的存储空间内</p>
     * @param categoryId 类目id
     * @param workspaceId 工作空间id
     */
    private ApplyFileUploadLeaseResponseBody.ApplyFileUploadLeaseResponseBodyData applyFileUploadLease(String categoryId, String workspaceId, MultipartFile file) {

        Client client = createClient();
        ApplyFileUploadLeaseRequest applyFileUploadLeaseRequest = new ApplyFileUploadLeaseRequest();
        applyFileUploadLeaseRequest.setFileName(file.getOriginalFilename());
        applyFileUploadLeaseRequest.setSizeInBytes(String.valueOf(file.getSize()));
        try {
            applyFileUploadLeaseRequest.setMd5(MD5Utils.toMd5(file.getInputStream()));
        } catch (IOException e) {
            throw new CustomException("文件 MD5 计算异常：" + e.getMessage());
        }
        RuntimeOptions runtime = new RuntimeOptions();
        Map<String, String> headers = new HashMap<>();
        try {
            // 复制代码运行请自行打印 API 的返回值
            ApplyFileUploadLeaseResponse applyFileUploadLeaseResponse = client.applyFileUploadLeaseWithOptions(categoryId, workspaceId, applyFileUploadLeaseRequest, headers, runtime);
            return applyFileUploadLeaseResponse.getBody().getData();
        } catch (TeaException error) {
            String errorMsg = "接口 applyFileUploadLease 调用失败：" + error.getMessage() + "，诊断地址：" + error.getData().get("Recommend");
            throw new CustomException(errorMsg);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            String errorMsg = "applyFileUploadLease 调用失败：" + error.getMessage() + "，诊断地址：" + error.getData().get("Recommend");
            throw new CustomException(errorMsg);
        }
    }

    private static void uploadFile(MultipartFile file, ApplyFileUploadLeaseResponseBody.ApplyFileUploadLeaseResponseBodyData applyFileUploadLeaseResponseBodyData) {

        HttpURLConnection connection = null;
        try {
            // 创建URL对象
            URI uri = URI.create(applyFileUploadLeaseResponseBodyData.getParam().getUrl());
            connection = (HttpURLConnection) uri.toURL().openConnection();

            // 设置请求方法为PUT，预签名URL默认用于PUT操作进行文件上传
            connection.setRequestMethod("PUT");

            // 允许向connection输出，因为这个连接是用于上传文件的
            connection.setDoOutput(true);

            // 设置请求头，这里设置ApplyFileUploadLease接口返回的Data.Param.Headers中的参数
            @SuppressWarnings("unchecked")
            Map<String, String> headers = (Map<String, String>) applyFileUploadLeaseResponseBodyData.getParam().getHeaders();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            // 读取文件并通过连接上传
            try (DataOutputStream outStream = new DataOutputStream(connection.getOutputStream());
                 InputStream fileInputStream = file.getInputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
                outStream.flush();
            }

            // 检查响应代码
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 文件上传成功处理
                System.out.println("File uploaded successfully.");
            } else {
                throw new CustomException("上传文件失败" + responseCode + "，reason：" + connection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private AddFileResponseBody.AddFileResponseBodyData addFile(String leaseId, String categoryId, String workspaceId) {
        Client client = createClient();
        AddFileRequest addFileRequest = new AddFileRequest()
                .setLeaseId(leaseId)
                .setParser("DASHSCOPE_DOCMIND")
                .setCategoryId(categoryId);
        RuntimeOptions runtime = new RuntimeOptions();
        Map<String, String> headers = new HashMap<>();
        try {
            // 复制代码运行请自行打印 API 的返回值
            AddFileResponse addFileResponse = client.addFileWithOptions(workspaceId, addFileRequest, headers, runtime);
            return addFileResponse.getBody().getData();
        } catch (TeaException error) {
            String errorMsg = "接口 addFile 调用失败：" + error.getMessage() + "，诊断地址：" + error.getData().get("Recommend");
            throw new CustomException(errorMsg);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            String errorMsg = "addFile 调用失败：" + error.getMessage() + "，诊断地址：" + error.getData().get("Recommend");
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