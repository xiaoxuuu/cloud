package cc.xiaoxu.cloud;

import cc.xiaoxu.cloud.core.utils.GetStartInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.TimeZone;

@Slf4j
@SpringBootApplication(scanBasePackages = {"cc.xiaoxu.cloud"})
@EnableDiscoveryClient
@EnableDubbo
public class AuthApplication {

    public static void main(String[] args) {

        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        // 获取 Spring Boot 上下文
        ConfigurableApplicationContext ctx = SpringApplication.run(AuthApplication.class, args);
        log.error(GetStartInfoUtils.getLog(ctx));
    }
}