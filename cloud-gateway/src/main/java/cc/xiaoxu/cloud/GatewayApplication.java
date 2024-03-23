package cc.xiaoxu.cloud;

import cc.xiaoxu.cloud.core.utils.GetStartInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(GatewayApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        ConfigurableApplicationContext ctx = application.run(args);
        log.error(GetStartInfoUtils.getLog(ctx));
    }
}