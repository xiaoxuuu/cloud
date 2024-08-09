package cc.xiaoxi.cloud.bean.file.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "文件请求数据")
public class DeleteFileVO {

    @Schema(description = "文件id集合")
    private List<String> fileIdList;
}