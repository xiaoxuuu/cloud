package cc.xiaoxu.cloud.core.utils.http;

import cc.xiaoxu.cloud.core.utils.bean.JsonUtils;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * HTTP 工具类，基于 JDK 21 原生 HttpClient 实现
 * <p>
 * 支持 GET、POST 请求，可配置请求头、超时时间、跳过 HTTPS 证书验证、异步回调等功能
 * <p>
 * 2024/12/20
 *
 * @author XiaoXu
 */
public class HttpUtils {

    private final HttpClient httpClient;
    private final Map<String, String> headers;
    private Duration timeout;
    private String url;
    private String body;
    private final Map<String, String> params;

    /**
     * 私有构造函数
     */
    private HttpUtils(HttpClient httpClient) {
        this.httpClient = httpClient;
        this.headers = new HashMap<>();
        this.params = new HashMap<>();
        this.timeout = Duration.ofSeconds(30); // 默认30秒超时
    }

    /**
     * 创建 HttpUtils 实例
     *
     * @return HttpUtils 实例
     */
    public static HttpUtils builder() {
        return new HttpUtils(createDefaultHttpClient());
    }

    /**
     * 创建跳过 HTTPS 证书验证的 HttpUtils 实例
     *
     * @return HttpUtils 实例
     */
    public static HttpUtils builderWithoutSsl() {
        return new HttpUtils(createHttpClientWithoutSsl());
    }

    /**
     * 设置请求 URL
     *
     * @param url 请求 URL
     * @return HttpUtils 实例
     */
    public HttpUtils url(String url) {
        this.url = url;
        return this;
    }

    /**
     * 添加请求头
     *
     * @param key   请求头名称
     * @param value 请求头值
     * @return HttpUtils 实例
     */
    public HttpUtils header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    /**
     * 批量添加请求头
     *
     * @param headers 请求头 Map
     * @return HttpUtils 实例
     */
    public HttpUtils headers(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    /**
     * 设置请求超时时间
     *
     * @param timeout 超时时间
     * @return HttpUtils 实例
     */
    public HttpUtils timeout(Duration timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * 设置请求超时时间（秒）
     *
     * @param seconds 超时时间（秒）
     * @return HttpUtils 实例
     */
    public HttpUtils timeout(long seconds) {
        this.timeout = Duration.ofSeconds(seconds);
        return this;
    }

    /**
     * 添加 URL 参数
     *
     * @param key   参数名
     * @param value 参数值
     * @return HttpUtils 实例
     */
    public HttpUtils param(String key, String value) {
        this.params.put(key, value);
        return this;
    }

    /**
     * 批量添加 URL 参数
     *
     * @param params 参数 Map
     * @return HttpUtils 实例
     */
    public HttpUtils params(Map<String, String> params) {
        this.params.putAll(params);
        return this;
    }

    /**
     * 设置请求体（字符串）
     *
     * @param body 请求体
     * @return HttpUtils 实例
     */
    public HttpUtils body(String body) {
        this.body = body;
        return this;
    }

    /**
     * 设置请求体（对象，自动转换为 JSON）
     *
     * @param object 请求体对象
     * @return HttpUtils 实例
     */
    public HttpUtils body(Object object) {
        this.body = JsonUtils.toString(object);
        this.header("Content-Type", "application/json");
        return this;
    }

    /**
     * 执行 GET 请求（同步）
     *
     * @return 响应字符串
     * @throws Exception 请求异常
     */
    public String get() throws Exception {
        HttpRequest request = buildGetRequest();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    /**
     * 执行 GET 请求（异步）
     *
     * @param callback 回调函数
     */
    public void getAsync(Consumer<String> callback) {
        getAsync(callback, Throwable::printStackTrace);
    }

    /**
     * 执行 GET 请求（异步）
     *
     * @param callback      成功回调函数
     * @param errorCallback 错误回调函数
     */
    public void getAsync(Consumer<String> callback, Consumer<Throwable> errorCallback) {
        try {
            HttpRequest request = buildGetRequest();
            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(callback)
                    .exceptionally(throwable -> {
                        errorCallback.accept(throwable);
                        return null;
                    });
        } catch (Exception e) {
            errorCallback.accept(e);
        }
    }

    /**
     * 执行 GET 请求（异步，返回 CompletableFuture）
     *
     * @return CompletableFuture<String>
     */
    public CompletableFuture<String> getAsyncFuture() {
        try {
            HttpRequest request = buildGetRequest();
            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 执行 POST 请求（同步）
     *
     * @return 响应字符串
     * @throws Exception 请求异常
     */
    public String post() throws Exception {
        HttpRequest request = buildPostRequest();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    /**
     * 执行 POST 请求（异步）
     *
     * @param callback 回调函数
     */
    public void postAsync(Consumer<String> callback) {
        postAsync(callback, Throwable::printStackTrace);
    }

    /**
     * 执行 POST 请求（异步）
     *
     * @param callback      成功回调函数
     * @param errorCallback 错误回调函数
     */
    public void postAsync(Consumer<String> callback, Consumer<Throwable> errorCallback) {
        try {
            HttpRequest request = buildPostRequest();
            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(callback)
                    .exceptionally(throwable -> {
                        errorCallback.accept(throwable);
                        return null;
                    });
        } catch (Exception e) {
            errorCallback.accept(e);
        }
    }

    /**
     * 执行 POST 请求（异步，返回 CompletableFuture）
     *
     * @return CompletableFuture<String>
     */
    public CompletableFuture<String> postAsyncFuture() {
        try {
            HttpRequest request = buildPostRequest();
            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 构建 GET 请求
     *
     * @return HttpRequest
     * @throws Exception 构建异常
     */
    private HttpRequest buildGetRequest() throws Exception {
        String finalUrl = buildUrlWithParams();
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(finalUrl))
                .timeout(timeout)
                .GET();

        // 添加请求头
        headers.forEach(builder::header);

        return builder.build();
    }

    /**
     * 构建 POST 请求
     *
     * @return HttpRequest
     * @throws Exception 构建异常
     */
    private HttpRequest buildPostRequest() throws Exception {
        String finalUrl = buildUrlWithParams();
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(finalUrl))
                .timeout(timeout);

        // 设置请求体
        if (body != null && !body.isEmpty()) {
            builder.POST(HttpRequest.BodyPublishers.ofString(body));
        } else {
            builder.POST(HttpRequest.BodyPublishers.noBody());
        }

        // 添加请求头
        headers.forEach(builder::header);

        return builder.build();
    }

    /**
     * 构建带参数的 URL
     *
     * @return 完整的 URL
     */
    private String buildUrlWithParams() {
        if (params.isEmpty()) {
            return url;
        }

        String queryString = params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        return url + (url.contains("?") ? "&" : "?") + queryString;
    }

    /**
     * 创建默认的 HttpClient
     *
     * @return HttpClient
     */
    private static HttpClient createDefaultHttpClient() {
        return HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    /**
     * 创建跳过 SSL 证书验证的 HttpClient
     *
     * @return HttpClient
     */
    private static HttpClient createHttpClientWithoutSsl() {
        try {
            // 创建信任所有证书的 TrustManager
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                            // 信任所有客户端证书
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                            // 信任所有服务器证书
                        }
                    }
            };

            // 创建 SSLContext
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            return HttpClient.newBuilder()
                    .version(HttpClient.Version.HTTP_2)
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .sslContext(sslContext)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("创建 HttpClient 失败", e);
        }
    }
}