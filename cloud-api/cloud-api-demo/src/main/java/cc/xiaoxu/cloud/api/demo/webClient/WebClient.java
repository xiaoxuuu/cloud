package cc.xiaoxu.cloud.api.demo.webClient;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.SneakyThrows;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

@Component
public class WebClient {

    public void execute() {

        String id = "53510000MJY8498812";
        String contentType = "application/json;charset=UTF-8";
        String referer = "https://xxgs.chinanpo.mca.gov.cn/gsxt/newDetails?b=eyJpZCI6IjUzNTEwMDAwTUpZODQ5ODgxMiJ9";
        String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36";

        org.springframework.web.reactive.function.client.WebClient client = buildWebClient();
        PublicApi publicApi = HttpServiceProxyFactory.builder().clientAdapter(WebClientAdapter.forClient(client)).build().createClient(PublicApi.class);

        System.out.println(publicApi.detail(new IdDTO(id), contentType, referer, userAgent));
    }

    public void post() {

        org.springframework.web.reactive.function.client.WebClient webClient = org.springframework.web.reactive.function.client.WebClient.builder().clientConnector(new ReactorClientHttpConnector(buildHttpClient())).build();

        String url = "https://xxgs.chinanpo.mca.gov.cn/gsxt/PlatformSHZZFRKGSXT/biz/ma/shzzgsxt/a/getAae01CertificateInfo.html";
        String requestBody = "{\"id\":\"53510000MJY8498812\"}";

        String response = webClient.post()
                .uri(url)
                .header("Content-Type", "application/json;charset=UTF-8")
                .header("Referer", "https://xxgs.chinanpo.mca.gov.cn/gsxt/newDetails?b=eyJpZCI6IjUzNTEwMDAwTUpZODQ5ODgxMiJ9")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println(response);
    }

    @SneakyThrows
    private HttpClient buildHttpClient() {

        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }

                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[]{};
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        SslContext context = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        return HttpClient.create().secure(t -> t.sslContext(context));
    }

    private org.springframework.web.reactive.function.client.WebClient buildWebClient() {

        org.springframework.web.reactive.function.client.WebClient.Builder builder = org.springframework.web.reactive.function.client.WebClient.builder().clientConnector(new ReactorClientHttpConnector(buildHttpClient()));

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                // 设置最大内存缓冲区为 16MB
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                .build();
        builder.exchangeStrategies(strategies);

        return builder.baseUrl("https://xxgs.chinanpo.mca.gov.cn").build();
    }
}
