package cc.xiaoxu.cloud.my.bean.mysql;

import cc.xiaoxu.cloud.core.bean.entity.BaseEntity;
import cc.xiaoxu.cloud.my.bean.enums.IconTypeEnum;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_nav_website_icon")
@AllArgsConstructor
public class NavWebsiteIcon extends BaseEntity {

    @Schema(description = "图标名称")
    private String name;

    @Schema(description = "图标")
    private String icon;

    @Schema(description = "类型：BASE64")
    private String type;

    public NavWebsiteIcon() {
        this.icon = "";
        this.type = IconTypeEnum.NULL.getCode();
    }
}