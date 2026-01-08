package cc.xiaoxu.cloud.my.controller;

import ai.djl.util.JsonUtils;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.my.service.ConstantService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.util.WxMaConfigHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@Slf4j
@Tag(name = "微信小程序认证接口")
@RequestMapping("/wx/ma/user/{appid}")
public class WechatMiniAppController {

    private final WxMaService wxMaService;
    private final ConstantService constantService;

    @Operation(summary = "登陆", description = "静默登陆")
    @GetMapping("/login/{code}")
    public String login(@PathVariable("appid") String appid, @PathVariable("code") String code) {
        if (StringUtils.isBlank(code)) {
            throw new CustomException("错误的参数");
        }

        if (!wxMaService.switchover(appid)) {
            throw new CustomException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }

        try {
            WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
            log.info(session.getSessionKey());
            log.info(session.getOpenid());
            String value = constantService.getValue("admin");
            if (StringUtils.isBlank(value)) {
                return null;
            }
            Set<String> adminOpenIdSet = Arrays.stream(value.split(",")).collect(Collectors.toSet());
            if (adminOpenIdSet.contains(session.getOpenid())) {
                return "ADMIN";
            }
            return null;
        } catch (WxErrorException e) {
            log.error(e.getMessage(), e);
            return e.toString();
        } finally {
            WxMaConfigHolder.remove();//清理ThreadLocal
        }
    }

    @Operation(summary = "获取用户信息", description = "获取用户信息接口")
    @GetMapping("/info")
    public String info(@PathVariable String appid, String sessionKey,
                       String signature, String rawData, String encryptedData, String iv) {
        if (!wxMaService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }

        // 用户信息校验
        if (!wxMaService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            WxMaConfigHolder.remove();//清理ThreadLocal
            return "user check failed";
        }

        // 解密用户信息
        WxMaUserInfo userInfo = wxMaService.getUserService().getUserInfo(sessionKey, encryptedData, iv);
        WxMaConfigHolder.remove();//清理ThreadLocal
        return JsonUtils.toJson(userInfo);
    }

    @Operation(summary = "获取用户手机号", description = "获取用户手机号")
    @GetMapping("/phone")
    public String phone(@PathVariable String appid, String sessionKey, String signature,
                        String rawData, String encryptedData, String iv) {
        if (!wxMaService.switchover(appid)) {
            throw new IllegalArgumentException(String.format("未找到对应appid=[%s]的配置，请核实！", appid));
        }

        // 用户信息校验
        if (!wxMaService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
            WxMaConfigHolder.remove();//清理ThreadLocal
            return "user check failed";
        }

        // 解密
        WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);
        WxMaConfigHolder.remove();//清理ThreadLocal
        return JsonUtils.toJson(phoneNoInfo);
    }
}