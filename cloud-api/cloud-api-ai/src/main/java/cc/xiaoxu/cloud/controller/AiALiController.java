package cc.xiaoxu.cloud.controller;

import cc.xiaoxu.cloud.bean.ai.vo.AiALiSplitTxtPageVO;
import com.alibaba.dashscope.embeddings.*;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.aliyun.bailian20231229.Client;
import com.aliyun.bailian20231229.models.ListChunksRequest;
import com.aliyun.bailian20231229.models.ListChunksResponse;
import com.aliyun.bailian20231229.models.ListChunksResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AiALiController {

    public static void main(String[] args) throws Exception {

        AiALiSplitTxtPageVO aiALiSplitTxtPageVO = new AiALiSplitTxtPageVO();

        aiALiSplitTxtPageVO.setApiKey("sk-9fa9419e92924aa5bae89905d4f86779");
        aiALiSplitTxtPageVO.setWorkspaceId("llm-1xz44kjs8q87eosk");
        aiALiSplitTxtPageVO.setIndexId("tcrsa5xbag");
        aiALiSplitTxtPageVO.setFiled("file_86789d2490cf4931b38bb2bce15c3104_10134373");
        aiALiSplitTxtPageVO.setPageNum(1);
        aiALiSplitTxtPageVO.setPageSize(10);
        AiALiController aiALiController = new AiALiController();
        String s = aiALiController.readSplitTxt(aiALiSplitTxtPageVO);
        System.out.println(s);
    }

    @Operation(summary = "读取分片数据", description = "读取阿里云分片好的文件数据")
    @PostMapping("/read_split_txt")
    public @ResponseBody String readSplitTxt(@RequestBody AiALiSplitTxtPageVO vo) throws Exception {

        Client client = AiALiController.createClient();
        ListChunksRequest listChunksRequest = new ListChunksRequest()
                .setFiled(vo.getFiled())
                .setIndexId(vo.getIndexId())
                .setPageNum(vo.getPageNum())
                .setPageSize(vo.getPageSize());
        RuntimeOptions runtime = new RuntimeOptions();
        Map<String, String> headers = new HashMap<>();
        try {
            // 复制代码运行请自行打印 API 的返回值
            ListChunksResponse listChunksResponse = client.listChunksWithOptions(vo.getWorkspaceId(), listChunksRequest, headers, runtime);
            List<ListChunksResponseBody.ListChunksResponseBodyDataNodes> nodes = listChunksResponse.body.getData().nodes;
            return nodes.stream().map(ListChunksResponseBody.ListChunksResponseBodyDataNodes::getText).collect(Collectors.joining(System.lineSeparator()));
        } catch (TeaException error) {
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            System.out.println(error.getData().get("Recommend"));
            Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            System.out.println(error.getData().get("Recommend"));
            Common.assertAsString(error.message);
        }
        return null;
    }

    /**
     * <b>description</b> :
     * <p>使用AK&amp;SK初始化账号Client</p>
     * @return Client
     *
     * @throws Exception
     */
    public static Client createClient() throws Exception {
        // 工程代码泄露可能会导致 AccessKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考。
        // 建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html。
        Config config = new Config()
                // 必填，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID。
                .setAccessKeyId("LTAI5tPnQuSgaViEAfG1zaZY")
                // 必填，请确保代码运行环境设置了环境变量 ALIBABA_CLOUD_ACCESS_KEY_SECRET。
                .setAccessKeySecret("pyK9of22HPjKbroc2LIhpM5u9679EX");
        // Endpoint 请参考 https://api.aliyun.com/product/bailian
        config.endpoint = "bailian.cn-beijing.aliyuncs.com";
        return new Client(config);
    }

    public static void textCall() throws ApiException, NoApiKeyException {
        TextEmbeddingParam param = TextEmbeddingParam
                .builder()
                .model(TextEmbedding.Models.TEXT_EMBEDDING_V1)
                .texts(Arrays.asList("风急天高猿啸哀", "渚清沙白鸟飞回", "无边落木萧萧下", "不尽长江滚滚来")).build();
        TextEmbedding textEmbedding = new TextEmbedding();
        TextEmbeddingResult result = textEmbedding.call(param);
        System.out.println(result);
    }

    /**
     * 向量转换
     */
    public static void fileCall() throws ApiException, NoApiKeyException {
        BatchTextEmbeddingParam param = BatchTextEmbeddingParam.builder()
                .model(BatchTextEmbedding.Models.TEXT_EMBEDDING_ASYNC_V1)
                .apiKey("sk-9fa9419e92924aa5bae89905d4f86779")
                .url("https://modelscope.oss-cn-beijing.aliyuncs.com/resource/text_embedding_file.txt")
                .build();
        BatchTextEmbedding textEmbedding = new BatchTextEmbedding();
        BatchTextEmbeddingResult result = textEmbedding.call(param);
        System.out.println(result);
    }

//    public static void main(String[] args) {
//        try {
//            textCall();
//        } catch (ApiException | NoApiKeyException e) {
//            System.out.println(e.getMessage());
//        }
//        System.exit(0);
//    }
}