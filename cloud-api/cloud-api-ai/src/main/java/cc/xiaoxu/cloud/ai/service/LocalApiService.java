package cc.xiaoxu.cloud.ai.service;

import cc.xiaoxu.cloud.ai.utils.OkHttpUtils;
import cc.xiaoxu.cloud.bean.ai.dto.LocalVectorDTO;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.core.utils.bean.JsonUtils;
import com.alibaba.fastjson2.JSONException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LocalApiService {

    @Value("${api.file.location}")
    private String fileLocation;

    @Value("${api.model.url}")
    public String URL;

    private static final String vector_BODY = """
            {
                "texts": %s,
                "truncate_dim": 1024
            }
            """;

    private static final String splitBody = """
            {
                "text": "%s",
                "chunk_size": %s,
                "chunk_overlap": 0
            }
            """;
    private static final String chunk_size = "768";

    public List<LocalVectorDTO> localVector(List<String> contentList) {

        List<LocalVectorDTO> vectorList;
        String formatted = vector_BODY.formatted(JsonUtils.toString(contentList));
        try (Response response = OkHttpUtils.builder()
                .url(URL + "/embeddings")
                .body(formatted)
                .post(true)
                .syncResponse()) {
            String resultData = response.body().string();
            vectorList = JsonUtils.parseArray(resultData, LocalVectorDTO.class);
        } catch (JSONException e) {
            log.error("JSON解析异常：{}", e.getMessage());
            return new ArrayList<>();
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
        return vectorList;
    }

    public List<Float> vector(String text) {

        return localVector(List.of(text)).getFirst().getEmbedding();
    }

    public List<String> split(String content) {
        log.debug("文件切片");
        List<String> textList;
        String replaceContent = content.replace(System.lineSeparator(), "");
        log.debug("单个切片大小：{}，实际切片长度：{}", chunk_size, replaceContent.length());
        String formatted = splitBody.formatted(replaceContent, chunk_size);
        try (Response response = OkHttpUtils.builder()
                .url(URL + "/split")
                .body(formatted)
                .post(true)
                .syncResponse()) {
            String resultData = response.body().string();
            textList = JsonUtils.parseArray(resultData, String.class);
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }
        log.debug("文件切片数量：{}", textList.size());
        return textList;
    }

    /**
     * 上传文件至本地
     */
    public String uploadFile(MultipartFile file) {

        if (file.isEmpty()) {
            throw new CustomException("Please select a file to upload");
        }

        try {
            // 获取文件名
            String originalFilename = file.getOriginalFilename();
            // 创建文件的存储路径
            Path path = Paths.get(fileLocation);
            // 确保目录存在
            Files.createDirectories(path);

            // 获取当前时间并格式化
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String formattedDateTime = now.format(formatter);

            // 在文件名前添加当前时间
            String newFilename = formattedDateTime + "_" + originalFilename;

            // 构建完整的文件路径
            Path filePath = path.resolve(newFilename);

            // 将文件写入到指定路径
            Files.copy(file.getInputStream(), filePath);

            return filePath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException("Failed to upload file " + file.getOriginalFilename());
        }
    }
}