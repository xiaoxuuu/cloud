package cc.xiaoxu.cloud.bean.dto.amap;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "高德地图输入提示 - 请求参数")
public class AmapInputTipsRequestDTO extends AmapBaseRequestDTO {

    @Schema(description = "坐标点，格式：经度,纬度")
    private String location;
}