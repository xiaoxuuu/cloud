package cc.xiaoxu.cloud.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.Arrays;

/**
 * <p>获取启动信息</p>
 *
 * @author 小徐
 * @since 2023/7/10 14:38
 */
public class GetStartInfoUtils {

    /**
     * 返回配置信息，cloud-gateway - GatewayApplication 存在同名方法，需同步修改
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
        String message = "%s(%s)%s Start Success, Package Time: %s, Start Time: %s";
        return message.formatted(name, port, Arrays.toString(environment.getActiveProfiles()), packageTime, startTime);
    }
}