package cc.xiaoxu.cloud.my.entity;

import cc.xiaoxu.cloud.core.bean.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_nav_website")
@NoArgsConstructor
@AllArgsConstructor
public class NavWebsite extends BaseEntity {

    @Schema(description = "展示名称")
    private String shortName;

    @Schema(description = "网站名称（抓取）")
    private String websiteName;

    @Schema(description = "链接")
    private String url;

    @Schema(description = "网站描述")
    private String description;

    @Schema(description = "图标id")
    private Integer iconId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "上次正常访问时间（抓取）")
    private Date lastAvailableTime;

    @Schema(description = "访问次数")
    private Integer visitNum;

    @Schema(description = "排序")
    private String sort;
}