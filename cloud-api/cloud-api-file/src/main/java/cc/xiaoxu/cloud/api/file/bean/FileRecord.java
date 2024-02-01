package cc.xiaoxu.cloud.api.file.bean;

import cc.xiaoxu.cloud.core.bean.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Schema(description = "文件上传记录")
@Data
@TableName(value = "t_file_record")
public class FileRecord extends BaseEntity {

    @Schema(description = "文件原名")
    @TableField(value = "original_name")
    private String originalName;

    @Schema(description = "新文件名")
    @TableField(value = "name")
    private String name;

    @Schema(description = "文件路径")
    @TableField(value = "path")
    private String path;

    @Schema(description = "文件类型")
    @TableField(value = "suffix")
    private String suffix;

    @Schema(description = "文件md5")
    @TableField(value = "md5")
    private String md5;

    @Schema(description = "文件大小(字节)")
    @TableField(value = "file_size")
    private String fileSize;
}