package cc.xiaoxu.cloud.my.manager;

import cc.xiaoxu.cloud.bean.dto.WebExtractDTO;
import cc.xiaoxu.cloud.bean.dto.WebSearchDTO;
import cc.xiaoxu.cloud.core.exception.CustomException;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchManager {

    @Value("${api.tavily}")
    private String tavilyApiKey;

    private static final String SEARCH_BODY = """
            {
                "query": "%s",
                "max_results": 10
            }
            """;

    private static final String EXTRACT_BODY = """
            {
                "urls": [%s]
            }
            """;

    public WebSearchDTO search(String keyword) {
        log.debug("联网搜索");
        WebSearchDTO result;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tavily.com/search"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + tavilyApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(SEARCH_BODY.formatted(keyword)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String resultData = response.body();
            result = JSON.parseObject(resultData, WebSearchDTO.class);
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }

        return result;
    }

    public WebExtractDTO extract(List<String> urlList) {

        String url = urlList.stream().map(k -> "\"" + k + "\"").collect(Collectors.joining(","));

        WebExtractDTO result;
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.tavily.com/extract"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + tavilyApiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(EXTRACT_BODY.formatted(url)))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String resultData = response.body();
            result = JSON.parseObject(resultData, WebExtractDTO.class);
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }

        return result;
    }
}