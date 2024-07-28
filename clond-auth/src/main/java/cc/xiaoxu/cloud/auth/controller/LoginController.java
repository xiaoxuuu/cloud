package cc.xiaoxu.cloud.auth.controller;

import cc.xiaoxu.clond.core.satoken.utils.LoginUtil;
import cc.xiaoxu.cloud.auth.bean.dto.PwdLoginDTO;
import cc.xiaoxu.cloud.auth.bean.dto.UserDTO;
import cc.xiaoxu.cloud.auth.bean.vo.TokenInfoVO;
import cc.xiaoxu.cloud.auth.service.LoginService;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
@Tag(name = "授权接口")
public class LoginController {

    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/check_user")
    @Operation(summary = "检查用户")
    @SaIgnore
    public boolean checkUser(@Valid @RequestBody UserDTO dto) {
        return loginService.checkUser(dto);
    }

    @PostMapping("/pwd_login")
    @Operation(summary = "密码登录")
    @SaIgnore
    public TokenInfoVO pwdLogin(@RequestBody @Valid PwdLoginDTO login) {
        return loginService.pwdLogin(login);
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录")
    public void logout() {
        StpUtil.logout(LoginUtil.getLoginId());
    }
}