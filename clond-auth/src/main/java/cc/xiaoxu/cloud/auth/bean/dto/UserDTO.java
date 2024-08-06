package cc.xiaoxu.cloud.auth.bean.dto;

import cc.xiaoxu.cloud.bean.enums.AccountClient;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "用户入参")
public class UserDTO {

    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "客户端")
    @NotNull(message = "客户端不能为空")
    private AccountClient client;
}