package cc.xiaoxu.cloud.core.controller;

import cc.xiaoxu.cloud.core.utils.constants.DateConstants;
import cc.xiaoxu.cloud.core.utils.date.DateUtils;
import cc.xiaoxu.cloud.core.utils.text.MD5Utils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 2022/5/11 10:33
 *
 * @author Xiao Xu
 */
@RestController
@Tag(name = "系统信息", description = "core-base-spring 内置接口，用于查询模块信息，依赖 core 的服务均自带此接口")
@RequestMapping("/cloud")
public class CloudController {

    @Value("${time.package:未读取到配置}")
    private String packageTime;

    @Value("${time.start:未读取到配置}")
    private String startTime;

    @GetMapping("/info")
    @Operation(summary = "读取项目基本信息", description = "可展示模块以下信息：打包时间、启动时间、主机名、服务ip、域名")
    public String get() {

        Map<String, String> map = new HashMap<>();
        map.put("打包时间", packageTime.replace("_", " "));
        map.put("启动时间", startTime.replace("_", " "));
        map.put("主机时间", DateUtils.getNowString());
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            map.put("主机名", inetAddress.getHostName());
            map.put("服务IP", inetAddress.getHostAddress());
            map.put("域名", inetAddress.getCanonicalHostName());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        return map.entrySet().stream().map(k -> k.getKey() + "：" + k.getValue()).collect(Collectors.joining("，"));
    }

    @GetMapping("/code")
    @Operation(summary = "获取授权码", description = "执行高风险操作的授权码，每天 00:00:00 自动更新")
    public String checkCode() {

        return getCheckCode();
    }

    public static String getCheckCode() {
        return MD5Utils.toMd5(DateUtils.getNowCustomString(DateConstants.DATE_MARK)).substring(0, 6);
    }
}