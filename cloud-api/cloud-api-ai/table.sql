CREATE TABLE t_knowledge
(
    id          SERIAL PRIMARY KEY,
    user_id     VARCHAR(8),
    type        VARCHAR(32),
    name        VARCHAR(255),
    file_id     TEXT,
    file_info   TEXT,
    status      VARCHAR(16),
    state       VARCHAR(1) NOT NULL,
    remark      VARCHAR(16),
    create_time TIMESTAMP  NOT NULL,
    create_id   INT,
    modify_time TIMESTAMP,
    modify_id   INT
);
COMMENT ON TABLE t_knowledge IS '知识库表';
COMMENT ON COLUMN t_knowledge.id IS '主键';
COMMENT ON COLUMN t_knowledge.user_id IS '用户id';
COMMENT ON COLUMN t_knowledge.type IS '资源类型：文件、数据表等';
COMMENT ON COLUMN t_knowledge.name IS '名称';
COMMENT ON COLUMN t_knowledge.file_id IS '文件 id';
COMMENT ON COLUMN t_knowledge.file_info IS '文件信息';
COMMENT ON COLUMN t_knowledge.status IS '文件处理状态';
COMMENT ON COLUMN t_knowledge.state IS '状态';
COMMENT ON COLUMN t_knowledge.remark IS '备注';
COMMENT ON COLUMN t_knowledge.create_time IS '创建时间';
COMMENT ON COLUMN t_knowledge.create_id IS '创建人id';
COMMENT ON COLUMN t_knowledge.modify_time IS '编辑时间';
COMMENT ON COLUMN t_knowledge.modify_id IS '编辑人id';

CREATE TABLE t_knowledge_section
(
    id           SERIAL PRIMARY KEY,
    knowledge_id INT        NOT NULL,
    user_id   VARCHAR(8),
    cut_content  TEXT,
    embedding vector(1024),
    state        VARCHAR(1) NOT NULL,
    remark       VARCHAR(16),
    create_time  TIMESTAMP  NOT NULL,
    create_id    INT,
    modify_time  TIMESTAMP,
    modify_id    INT
);
COMMENT ON TABLE t_knowledge_section IS '知识库数据切片';
COMMENT ON COLUMN t_knowledge_section.id IS '主键';
COMMENT ON COLUMN t_knowledge_section.knowledge_id IS '知识id';
COMMENT ON COLUMN t_knowledge_section.user_id IS '用户id';
COMMENT ON COLUMN t_knowledge_section.cut_content IS '知识切片内容';
COMMENT ON COLUMN t_knowledge_section.embedding IS '知识切片向量，1024 维';
COMMENT ON COLUMN t_knowledge_section.state IS '状态';
COMMENT ON COLUMN t_knowledge_section.remark IS '备注';
COMMENT ON COLUMN t_knowledge_section.create_time IS '创建时间';
COMMENT ON COLUMN t_knowledge_section.create_id IS '创建人id';
COMMENT ON COLUMN t_knowledge_section.modify_time IS '编辑时间';
COMMENT ON COLUMN t_knowledge_section.modify_id IS '编辑人id';