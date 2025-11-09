package cc.xiaoxu.cloud.my.entity;

import cc.xiaoxu.cloud.bean.enums.OperatingStatusEnum;
import cc.xiaoxu.cloud.bean.enums.PointTypeEnum;
import cc.xiaoxu.cloud.core.bean.entity.BaseInfoEntity;
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
@TableName("t_point")
@NoArgsConstructor
@AllArgsConstructor
public class Point extends BaseInfoEntity {

    @Schema(description = "点位类型")
    private PointTypeEnum pointType;

    @Schema(description = "简称")
    private String pointShortName;

    @Schema(description = "全称")
    private String pointFullName;

    @Schema(description = "地点描述")
    private String describe;

    @Schema(description = "详细地址")
    private String address;

    @Schema(description = "经度")
    private String longitude;

    @Schema(description = "纬度")
    private String latitude;

    @Schema(description = "上级id，用于数据归总")
    private Integer parentId;

    @Schema(description = "收藏次数")
    private Integer collectTimes;

    @Schema(description = "照片")
    private String photo;

    @Schema(description = "我去过的次数")
    private Integer visitedTimes;

    @Schema(description = "地址code")
    private Integer addressCode;

    @Schema(description = "营业状态")
    private OperatingStatusEnum operatingStatus;

    @Schema(description = "推荐距离(米）")
    private Integer recommendedDistance;

    @Schema(description = "营业时间")
    private String openingHours;

    @Schema(description = "联系电话")
    private String telephone;

    @Schema(description = "人均（分）")
    private Integer cost;
}