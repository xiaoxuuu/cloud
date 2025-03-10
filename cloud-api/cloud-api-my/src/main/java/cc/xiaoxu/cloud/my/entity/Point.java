package cc.xiaoxu.cloud.my.entity;

import cc.xiaoxu.cloud.bean.enums.PointTypeEnum;
import cc.xiaoxu.cloud.core.bean.entity.BaseInfoEntity;
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
@TableName("t_point")
@NoArgsConstructor
@AllArgsConstructor
public class Point extends BaseInfoEntity {

    @Schema(description = "点位类型")
    private PointTypeEnum pointType;

    @Schema(description = "名称")
    private String pointName;

    @Schema(description = "地点描述")
    private String describe;

    @Schema(description = "详细地址")
    private String address;

    @Schema(description = "经度")
    private String longitude;

    @Schema(description = "纬度")
    private String latitude;

    @Schema(description = "上级id，用于数据归总")
    private String parentId;

    @Schema(description = "收藏次数")
    private Integer collectTimes;

    @Schema(description = "照片")
    private String photo;

    @Schema(description = "我去过的次数")
    private Integer visitedTimes;

    @Schema(description = "来源")
    private String source;

    @Schema(description = "地址code")
    private String addressCode;

    @Schema(description = "高德 - WIA 坐标（来源高德小程序）")
    private String amapWia;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "高德 - 更新时间")
    private Date amapUpdateTime;

    @Schema(description = "高德 - 标签")
    private String amapTag;

    @Schema(description = "高德 - 评分")
    private String amapRating;

    @Schema(description = "高德 - 人均消费")
    private String amapCost;

    @Schema(description = "高德 - POI ID")
    private String amapPoiId;
}