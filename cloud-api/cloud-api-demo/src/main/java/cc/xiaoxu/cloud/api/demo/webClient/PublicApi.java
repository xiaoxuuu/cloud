package cc.xiaoxu.cloud.api.demo.webClient;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.PostExchange;

//@HttpExchange("")
public interface PublicApi {

    @PostExchange("/gsxt/PlatformSHZZFRKGSXT/biz/ma/shzzgsxt/a/getAae01CertificateInfo.html")
    String detail(@RequestBody IdDTO id,
                  @RequestHeader("Content-Type") String contentType,
                  @RequestHeader("Referer") String referer,
                  @RequestHeader("User-Agent") String userAgent
    );
}