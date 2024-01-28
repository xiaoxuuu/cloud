package cc.xiaoxu.cloud.file.bean;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "文件请求数据")
public class File {

    @Schema(description = "文件名称")
    private String fileName;

    @Schema(description = "文件名字集合")
    private List<String> fileNameList;
}