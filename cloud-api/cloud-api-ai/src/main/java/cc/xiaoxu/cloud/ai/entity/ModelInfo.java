package cc.xiaoxu.cloud.ai.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("t_model_info")
@NoArgsConstructor
@AllArgsConstructor
public class ModelInfo {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键id")
    private Integer id;

    @Schema(description = "模型公司")
    private String company;

    @Schema(description = "模型名称")
    private String name;

    @Schema(description = "模型类型")
    private String model;

    @Schema(description = "简介")
    private String introduction;

    @Schema(description = "api url")
    private String url;

    @Schema(description = "排序")
    private String sort;

    @Schema(description = "api key")
    private String apiKey;
}