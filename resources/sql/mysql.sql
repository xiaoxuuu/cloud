CREATE TABLE `t_file_record`
(
    `id`            int(11)      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `original_name` varchar(255) NOT NULL COMMENT '文件原名',
    `name`          varchar(32)  NOT NULL COMMENT '新文件名',
    `path`          varchar(64)  NOT NULL COMMENT '文件路径',
    `suffix`        varchar(8)   NOT NULL COMMENT '文件类型',
    `md5`           char(16)              DEFAULT NULL COMMENT '文件md5',
    `file_size`     bigint(20)            DEFAULT NULL COMMENT '大小(字节)',
    `state`         varchar(1)   NOT NULL DEFAULT 'E' COMMENT '状态：E 正常，D 删除 L 禁用',
    `remark`        varchar(255)          DEFAULT NULL COMMENT '备注',
    `create_time`   datetime              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_id`     int(11)               DEFAULT NULL COMMENT '创建人id',
    `modify_time`   datetime              DEFAULT NULL COMMENT '编辑时间',
    `modify_id`     int(11)               DEFAULT NULL COMMENT '编辑人id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='文件记录表';