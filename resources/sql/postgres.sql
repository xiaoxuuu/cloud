DROP TABLE IF EXISTS "t_constant";
CREATE TABLE t_constant
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(16) NOT NULL,
    value       TEXT        NOT NULL,
    state       VARCHAR(1)  NOT NULL,
    remark      VARCHAR(16),
    create_time TIMESTAMP   NOT NULL,
    create_id   INT,
    modify_time TIMESTAMP,
    modify_id   INT
);
COMMENT
ON TABLE t_constant IS '常量表';
COMMENT
ON COLUMN t_constant.id IS '主键';
COMMENT
ON COLUMN t_constant.name IS '名称';
COMMENT
ON COLUMN t_constant.value IS '值';
COMMENT
ON COLUMN t_constant.state IS '状态';
COMMENT
ON COLUMN t_constant.remark IS '备注';
COMMENT
ON COLUMN t_constant.create_time IS '创建时间';
COMMENT
ON COLUMN t_constant.create_id IS '创建人id';
COMMENT
ON COLUMN t_constant.modify_time IS '编辑时间';
COMMENT
ON COLUMN t_constant.modify_id IS '编辑人id';