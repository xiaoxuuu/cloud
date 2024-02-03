package cc.xiaoxu.cloud.my.navigation.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NavWebsiteAddVisitNumVO {

    @Schema(description = "id")
    private String id;
}