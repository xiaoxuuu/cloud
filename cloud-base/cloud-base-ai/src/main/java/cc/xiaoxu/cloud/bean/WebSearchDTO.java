package cc.xiaoxu.cloud.bean;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Schema(description = "Web 搜索")
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class WebSearchDTO {

    @Schema(description = "查询关键字")
    private String query;

    @JsonAlias("follow_up_questions")
    @Schema(description = "查询关键字")
    private String followUpQuestions;

    @Schema(description = "查询的简短总结")
    private String answer;

    @Schema(description = "图片列表")
    private String images;

    @Schema(description = "查询结果")
    private List<WebSearchInfoDTO> results;

    @JsonAlias("response_time")
    @Schema(description = "请求完成所需时间（秒）")
    private Float responseTime;

    public WebSearchDTO(Float responseTime) {
        this.responseTime = responseTime;
        this.results = new ArrayList<>();
    }
}