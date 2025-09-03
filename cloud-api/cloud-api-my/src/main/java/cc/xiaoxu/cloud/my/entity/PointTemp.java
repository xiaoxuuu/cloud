package cc.xiaoxu.cloud.my.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
public class PointTemp extends Point {

    @Schema(description = "错误经度")
    private String longitudeFake;

    @Schema(description = "错误纬度")
    private String latitudeFake;
}