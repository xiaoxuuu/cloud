package cc.xiaoxu.cloud.my.dao;

import cc.xiaoxu.cloud.my.entity.PointMap;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PointMapMapper extends BaseMapper<PointMap> {

    @Select("""
            SELECT
                amap_result ->> 'id' AS id,
                CASE
                    -- 先判断是否包含(总店)，包含则直接返回原名称
                    WHEN (amap_result ->> 'name') ~ '\\(总店\\)' THEN amap_result ->> 'name'
                    -- 不包含(总店)则移除所有括号及内容
                    ELSE regexp_replace(amap_result ->> 'name', '\\([^)]*\\)', '', 'g')
                END AS name,
                amap_result ->> 'adcode' AS adcode,
                amap_result ->> 'address' AS address,
                amap_result ->> 'location' AS location,
                -- 直接用 #>> 按路径提取嵌套字段
                amap_result #>> '{business, tel}' AS tel,
                amap_result #>> '{business, cost}' AS cost,
                amap_result #>> '{business, keytag}' AS keytag,
                amap_result #>> '{business, rectag}' AS rectag,
                amap_result #>> '{business, opentime_week}' AS openTime
            FROM t_point_map
            WHERE amap_result IS NOT NULL
            """)
    List<MapData> loadData();

    record MapData(String id, String name, String adcode, String address, String location, String tel, String cost,
                   String keytag, String rectag, String openTime) {
    }
}