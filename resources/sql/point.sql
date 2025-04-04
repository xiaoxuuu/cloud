CREATE TABLE "public"."t_point"
(
    "id"               int4 NOT NULL,
    "point_type"       varchar(64) COLLATE "pg_catalog"."default",
    "point_name"       varchar(64) COLLATE "pg_catalog"."default",
    "describe"         varchar(255) COLLATE "pg_catalog"."default",
    "address"          varchar(255) COLLATE "pg_catalog"."default",
    "longitude"        varchar(16) COLLATE "pg_catalog"."default",
    "latitude"         varchar(16) COLLATE "pg_catalog"."default",
    "parent_id"        int4,
    "collect_times"    int2,
    "photo"            varchar(255) COLLATE "pg_catalog"."default",
    "visited_times"    int2,
    "source"           varchar(255) COLLATE "pg_catalog"."default",
    "address_code"     varchar(255) COLLATE "pg_catalog"."default",
    "amap_wia"         varchar(32) COLLATE "pg_catalog"."default",
    "amap_update_time" timestamp(6),
    "amap_tag"         text COLLATE "pg_catalog"."default",
    "amap_rating"      varchar(255) COLLATE "pg_catalog"."default",
    "amap_cost"        varchar(255) COLLATE "pg_catalog"."default",
    "amap_poi_id"      varchar(255) COLLATE "pg_catalog"."default",
    "state"            varchar(1) COLLATE "pg_catalog"."default",
    "remark"           varchar(255) COLLATE "pg_catalog"."default",
    "create_time"      timestamp(6),
    "modify_time"      timestamp(6)
)
;

ALTER TABLE "public"."t_point"
    OWNER TO "postgres";

COMMENT
ON COLUMN "public"."t_point"."id" IS 'id';

COMMENT
ON COLUMN "public"."t_point"."point_type" IS '类型';

COMMENT
ON COLUMN "public"."t_point"."point_name" IS '名称';

COMMENT
ON COLUMN "public"."t_point"."describe" IS '地点描述';

COMMENT
ON COLUMN "public"."t_point"."address" IS '详细地址';

COMMENT
ON COLUMN "public"."t_point"."longitude" IS '经度';

COMMENT
ON COLUMN "public"."t_point"."latitude" IS '纬度';

COMMENT
ON COLUMN "public"."t_point"."parent_id" IS '上级id，用于数据归总';

COMMENT
ON COLUMN "public"."t_point"."collect_times" IS '收藏次数';

COMMENT
ON COLUMN "public"."t_point"."photo" IS '照片';

COMMENT
ON COLUMN "public"."t_point"."visited_times" IS '我去过的次数';

COMMENT
ON COLUMN "public"."t_point"."source" IS '来源';

COMMENT
ON COLUMN "public"."t_point"."address_code" IS '地址code';

COMMENT
ON COLUMN "public"."t_point"."amap_wia" IS '高德地图 - WIA 坐标（来源高德小程序）';


COMMENT
ON COLUMN "public"."t_point"."amap_update_time" IS '高德-更新时间';

COMMENT
ON COLUMN "public"."t_point"."amap_tag" IS '高德-特色';

COMMENT
ON COLUMN "public"."t_point"."amap_rating" IS '高德-评分';

COMMENT
ON COLUMN "public"."t_point"."amap_cost" IS '高德-人均消费';

COMMENT
ON COLUMN "public"."t_point"."amap_poi_id" IS '高德-POI ID';

COMMENT
ON COLUMN "public"."t_point"."state" IS '状态';

COMMENT
ON COLUMN "public"."t_point"."remark" IS '状态';

COMMENT
ON COLUMN "public"."t_point"."create_time" IS '创建时间';

COMMENT
ON COLUMN "public"."t_point"."modify_time" IS '修改时间';

COMMENT
ON TABLE "public"."t_point" IS '收藏点位';

CREATE TABLE "public"."t_point_source"
(
    "id"          int4 NOT NULL,
    "point_id"    int4 NOT NULL,
    "type"        varchar(16) COLLATE "pg_catalog"."default",
    "source"      varchar(16) COLLATE "pg_catalog"."default",
    "title"       varchar(255) COLLATE "pg_catalog"."default",
    "content"     varchar(255) COLLATE "pg_catalog"."default",
    "url"         varchar(255) COLLATE "pg_catalog"."default",
    "state"       varchar(1) COLLATE "pg_catalog"."default",
    "remark"      varchar(255) COLLATE "pg_catalog"."default",
    "create_time" timestamp(6),
    "modify_time" timestamp(6),
    CONSTRAINT "t_point_source_pkey" PRIMARY KEY ("id")
)
;

ALTER TABLE "public"."t_point_source"
    OWNER TO "postgres";

COMMENT
ON COLUMN "public"."t_point_source"."id" IS '主键';

COMMENT
ON COLUMN "public"."t_point_source"."point_id" IS '地点id';

COMMENT
ON COLUMN "public"."t_point_source"."type" IS '类型';

COMMENT
ON COLUMN "public"."t_point_source"."source" IS '来源';

COMMENT
ON COLUMN "public"."t_point_source"."title" IS '标题';

COMMENT
ON COLUMN "public"."t_point_source"."content" IS '内容';

COMMENT
ON COLUMN "public"."t_point_source"."url" IS '链接';

COMMENT
ON COLUMN "public"."t_point_source"."state" IS '状态';

COMMENT
ON COLUMN "public"."t_point_source"."remark" IS '状态';

COMMENT
ON COLUMN "public"."t_point_source"."create_time" IS '创建时间';

COMMENT
ON COLUMN "public"."t_point_source"."modify_time" IS '修改时间';

COMMENT
ON TABLE "public"."t_point_source" IS '收藏点位来源';