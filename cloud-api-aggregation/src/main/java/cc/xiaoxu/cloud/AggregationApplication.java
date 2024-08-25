package cc.xiaoxu.cloud;

import cc.xiaoxu.cloud.core.utils.GetStartInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@Slf4j
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value = {SpringBootApplication.class}))
@SpringBootApplication(scanBasePackages = "cc.xiaoxu.cloud")
//@EnableScheduling
//@EnableCaching
//@EnableAsync(proxyTargetClass = true)
public class AggregationApplication {

    public static void main(String[] args) {

        // 获取 Spring Boot 上下文
        ConfigurableApplicationContext ctx = SpringApplication.run(AggregationApplication.class, args);
        log.error(GetStartInfoUtils.getLog(ctx));
    }
}