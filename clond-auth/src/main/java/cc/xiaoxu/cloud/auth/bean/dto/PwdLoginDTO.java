package cc.xiaoxu.cloud.auth.bean.dto;

import cc.xiaoxu.cloud.bean.enums.AccountClient;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "密码登录-请求参数")
public class PwdLoginDTO {

    @Schema(description = "客户端")
    @NotNull(message = "客户端不能为空")
    private AccountClient client;

    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "验证码")
    private String validateCode;

    @Schema(description = "验证码随机数")
    private String randomStr;

    @Schema(description = "设备信息")
    @NotBlank(message = "设备信息不能为空")
    private String device;
}