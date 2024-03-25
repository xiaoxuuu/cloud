package cc.xiaoxu.cloud;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class GatewayApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplication(GatewayApplication.class).run(args);
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
        String packageTime = environment.getProperty("time.package");
        packageTime = StringUtils.isBlank(packageTime) ? notConfigured : packageTime.replace("_", " ");
        // 启动时间
        String startTime = environment.getProperty("time.start");
        startTime = StringUtils.isBlank(startTime) ? notConfigured : startTime.replace("_", " ");

        // 文本处理
        String message = "%s(%s) Start Success, Package Time: %s, Start Time: %s";
        return message.formatted(name, port, packageTime, startTime);
    }
}