package cc.xiaoxu.cloud.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * <p>获取启动信息</p>
 *
 * @author 小徐
 * @since 2023/7/10 14:38
 */
public class GetStartInfoUtils {

    public static String getLog(ConfigurableApplicationContext ctx) {

        ConfigurableEnvironment environment = ctx.getEnvironment();
        // 应用名
        String name = environment.getProperty("spring.application.name");
        name = StringUtils.isBlank(name) ? "未读取到配置" : name.toUpperCase();
        // 端口
        String port = environment.getProperty("server.port");
        port = StringUtils.isBlank(port) ? "未读取到配置" : port;
        // 版本
        String appVersion = environment.getProperty("app.version");
        appVersion = StringUtils.isBlank(appVersion) ? "" : appVersion;
        // 打包时间
        String packageTime = environment.getProperty("time.package");
        packageTime = StringUtils.isBlank(packageTime) ? "未读取到配置" : packageTime.replace("_", " ");
        // 启动时间
        String startTime = environment.getProperty("time.start");
        startTime = StringUtils.isBlank(startTime) ? "未读取到配置" : startTime.replace("_", " ");

        // 文本处理
        String message = "%s start success. Port: %s, Version: %s, PackageTime: %s, StartTime: %s";
        return message.formatted(name, port, appVersion, packageTime, startTime);
    }
}