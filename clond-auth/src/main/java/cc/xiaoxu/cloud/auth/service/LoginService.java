package cc.xiaoxu.cloud.auth.service;

import cc.xiaoxu.cloud.auth.bean.dto.PwdLoginDTO;
import cc.xiaoxu.cloud.auth.bean.dto.UserDTO;
import cc.xiaoxu.cloud.auth.bean.vo.TokenInfoVO;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public class LoginService {

//    @DubboReference
//    private RemoteUserService remoteUserService;

    /**
     * 检查用户是否已存在
     *
     * @param dto 用户名
     * @return 存在true; 不存在false;
     */
    public boolean checkUser(UserDTO dto) {
        // 读取用户信息
        return true;
    }

    /**
     * 账号密码登录
     *
     * @param login 登录信息 {@link  PwdLoginDTO}
     * @return {@link  TokenInfoVO}
     */
    public TokenInfoVO pwdLogin(PwdLoginDTO login) {

        // 校验验证码
        // 校验账号
        // 校验密码
        // 登录
        SaLoginModel loginModel = new SaLoginModel().build().setExtra("client", login.getClient()).setDevice(login.getDevice());
        StpUtil.login("1", loginModel);
        // 获取登录信息
        SaTokenInfo saTokenInfo = StpUtil.getTokenInfo();
        return new TokenInfoVO(saTokenInfo.getTokenValue(), saTokenInfo.getTokenTimeout(), saTokenInfo.getLoginDevice());
    }
}