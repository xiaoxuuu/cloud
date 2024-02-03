package cc.xiaoxu.cloud.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 接口文档配置
 * <p>
 * 2022/10/17 14:59
 *
 * @author XiaoXu
 */
@Slf4j
@Configuration
// @OpenAPIDefinition(servers = {@Server(url = "/api", description = "代理服务"), @Server(url = "/", description = "直连服务")})
public class DocumentConfig {

    @Value("${app.version}")
    private String appVersion;

    @Value("${server.port}")
    private String port;

    @Value("${spring.profiles.active}")
    private String activeProfiles;

    @Bean
    public OpenAPI customOpenApi() {

        Server server = new Server().description("默认服务");
        if ("prod".equals(activeProfiles)) {
            log.warn("swagger server url: /api");
            server.url("/api");
        } else {
            log.warn("swagger server url: /");
            server.url("/");
        }
        return new OpenAPI()
                .servers(List.of(server))
                .info(getInfo());
    }

    private Info getInfo() {

        Contact contact = new Contact()
                .name("小徐")
                .url("https://xiaoxu.cc/blog/")
                .email("i@xiaoxu.cc");
        return new Info()
                .title("Cloud 接口文档 - 小徐")
                .description("Cloud 接口文档")
                .version(appVersion)
                .termsOfService("https://xiaoxu.cc")
                .contact(contact);
    }
}