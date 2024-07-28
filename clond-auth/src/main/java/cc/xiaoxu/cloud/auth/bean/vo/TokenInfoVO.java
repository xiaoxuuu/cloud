package cc.xiaoxu.cloud.auth.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "登录信息")
public class TokenInfoVO implements Serializable {

    @Schema(description = "token")
    private String accessToken;

    @Schema(description = "过期时间")
    private Long timeOut;

    @Schema(description = "设备信息")
    private String device;
}