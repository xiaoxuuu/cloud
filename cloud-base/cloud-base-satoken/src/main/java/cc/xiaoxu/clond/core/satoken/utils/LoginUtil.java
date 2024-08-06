package cc.xiaoxu.clond.core.satoken.utils;

import cc.xiaoxu.cloud.bean.enums.AccountClient;
import cc.xiaoxu.cloud.core.exception.CustomException;
import cc.xiaoxu.cloud.core.utils.constants.SystemConstants;
import cc.xiaoxu.cloud.core.utils.enums.EnumUtils;
import cn.dev33.satoken.stp.StpUtil;

import java.util.List;

/**
 * <p>登录工具类</p>
 *
 * @author 小徐
 * @since 2024/7/24 上午11:46
 */
public class LoginUtil {

    /**
     * 获取账号ID
     *
     * @return String
     */
    public static String getLoginId() {
        return (String) StpUtil.getLoginId();
    }

    /**
     * 获取账号所属客户端
     *
     * @return AccountClient
     */
    public static AccountClient getClient() {
        return EnumUtils.getByClass(StpUtil.getExtra("client"), AccountClient.class);
    }

    /**
     * 获取角色列表
     */
    public static List<String> getRoleList() {
        return StpUtil.getRoleList();
    }

    /**
     * 从sa-token中获取loginID 拆分成客户端ID和登录ID
     * 例如：ADMIN_123456789012345678901234
     *
     * @return String['ADMIN',123123123]
     */
    private static String[] getLoginIdArray() {
        String loginId;
        try {
            loginId = LoginUtil.getLoginId();
        } catch (Exception ignored) {
            throw new CustomException("未获取到登录信息");
        }
        return loginId.split(SystemConstants.UNDERLINE);

    }
}