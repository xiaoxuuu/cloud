package cc.xiaoxu.cloud;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;

@Slf4j
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(GatewayApplication.class);
        // ApplicationStartup 用于自定义 Spring Boot 启动时的日志输出行为。
        // BufferingApplicationStartup 会缓冲日志输出直到 Spring Boot 的启动完成。可以避免日志输出的乱序问题，尤其是在异步环境中。
        // 2048 是传递给 BufferingApplicationStartup 构造函数的参数，表示缓冲区的大小。这里意味着日志缓冲区可以存储 2048 字节的数据。
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        ConfigurableApplicationContext ctx = application.run(args);
        log.error(getLog(ctx));
    }

    /**
     * 返回配置信息，cloud-core-spring - utils.GetStartInfoUtils 存在同名方法，需同步修改
     */
    public static String getLog(ConfigurableApplicationContext ctx) {

        String notConfigured = "Not Configured";
        ConfigurableEnvironment environment = ctx.getEnvironment();
        // 应用名
        String name = environment.getProperty("spring.application.name");
        name = StringUtils.isBlank(name) ? notConfigured : name.toUpperCase();
        // 端口
        String port = environment.getProperty("server.port");
        port = StringUtils.isBlank(port) ? notConfigured : port;
        // 打包时间
        String packageTime = environment.getProperty("build.time");
        packageTime = StringUtils.isBlank(packageTime) ? notConfigured : packageTime.replace("_", " ");
        // 启动时间
        String startTime = environment.getProperty("time.start");
        startTime = StringUtils.isBlank(startTime) ? notConfigured : startTime.replace("_", " ");

        // 文本处理
        String message = "%s(%s)%s Start Success, Package Time: %s, Start Time: %s";
        return message.formatted(name, port, Arrays.toString(environment.getActiveProfiles()), packageTime, startTime);
    }
}