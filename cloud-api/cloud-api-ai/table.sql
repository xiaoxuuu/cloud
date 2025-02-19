CREATE TABLE r_conversation_knowledge
(
    "id"                SERIAL PRIMARY KEY,
    "conversation_id"   INT        NOT NULL,
    "knowledge_base_id" INT        NOT NULL,
    "state"             VARCHAR(1) NOT NULL,
    "remark"            VARCHAR(16),
    "create_time"       TIMESTAMP  NOT NULL,
    "create_id"         INT,
    "modify_time"       TIMESTAMP,
    "modify_id"         INT
);
COMMENT ON COLUMN "r_conversation_knowledge"."id" IS '聊天主键';
COMMENT ON COLUMN "r_conversation_knowledge"."conversation_id" IS '会话id';
COMMENT ON COLUMN "r_conversation_knowledge"."knowledge_base_id" IS '知识库id';
COMMENT ON COLUMN "r_conversation_knowledge"."state" IS '状态';
COMMENT ON COLUMN "r_conversation_knowledge"."remark" IS '备注';
COMMENT ON COLUMN "r_conversation_knowledge"."create_time" IS '创建时间';
COMMENT ON COLUMN "r_conversation_knowledge"."create_id" IS '创建人id';
COMMENT ON COLUMN "r_conversation_knowledge"."modify_time" IS '编辑时间';
COMMENT ON COLUMN "r_conversation_knowledge"."modify_id" IS '编辑人id';
COMMENT ON TABLE "r_conversation_knowledge" IS '关系 - 会话知识库引用';

CREATE TABLE "t_conversation"
(
    "id"          SERIAL PRIMARY KEY,
    "name"        VARCHAR(255),
    "user_id"     INT        NOT NULL,
    "model_id" INT NOT NULL,
    "state"       VARCHAR(1) NOT NULL,
    "remark"      VARCHAR(16),
    "create_time" TIMESTAMP  NOT NULL,
    "create_id"   INT,
    "modify_time" TIMESTAMP,
    "modify_id"   INT
);
COMMENT ON COLUMN "t_conversation"."id" IS '对话主键';
COMMENT ON COLUMN "t_conversation"."name" IS '对话名称';
COMMENT ON COLUMN "t_conversation"."user_id" IS '所属用户';
COMMENT ON COLUMN "t_conversation"."model_id" IS '模型id';
COMMENT ON COLUMN "t_conversation"."state" IS '状态';
COMMENT ON COLUMN "t_conversation"."remark" IS '备注';
COMMENT ON COLUMN "t_conversation"."create_time" IS '创建时间';
COMMENT ON COLUMN "t_conversation"."create_id" IS '创建人id';
COMMENT ON COLUMN "t_conversation"."modify_time" IS '编辑时间';
COMMENT ON COLUMN "t_conversation"."modify_id" IS '编辑人id';
COMMENT ON TABLE "t_conversation" IS '对话';

CREATE TABLE "t_conversation_detail"
(
    "id"              SERIAL PRIMARY KEY,
    "conversation_id" INT         NOT NULL,
    "user_id"         INT         NOT NULL,
    "detail_id" VARCHAR(64),
    "object"          VARCHAR(32),
    "content_id"      INT         NOT NULL,
    "create_time"     TIMESTAMP   NOT NULL,
    "model_id"  INT NOT NULL,
    "role"            VARCHAR(16) NOT NULL,
    "token"           INT
);
COMMENT ON COLUMN "t_conversation_detail"."id" IS '对话内容主键';
COMMENT ON COLUMN "t_conversation_detail"."conversation_id" IS '对话id';
COMMENT ON COLUMN "t_conversation_detail"."user_id" IS '所属用户';
COMMENT ON COLUMN "t_conversation_detail"."detail_id" IS '模型内容id';
COMMENT ON COLUMN "t_conversation_detail"."content_id" IS '内容id';
COMMENT ON COLUMN "t_conversation_detail"."create_time" IS '创建时间';
COMMENT ON COLUMN "t_conversation_detail"."model_id" IS '模型id';
COMMENT ON COLUMN "t_conversation_detail"."role" IS '角色';
COMMENT ON COLUMN "t_conversation_detail"."token" IS '消耗token';
COMMENT ON TABLE "t_conversation_detail" IS '对话-内容';

CREATE TABLE "t_conversation_detail_content"
(
    "id"      SERIAL PRIMARY KEY,
    "content" text NOT NULL
);
COMMENT ON COLUMN "t_conversation_detail_content"."id" IS '对话内容主键';
COMMENT ON COLUMN "t_conversation_detail_content"."content" IS '内容';
COMMENT ON TABLE "t_conversation_detail_content" IS '对话-内容-详情';

CREATE TABLE t_knowledge
(
    id                SERIAL PRIMARY KEY,
    knowledge_base_id INT        NOT NULL,
    user_id INT NOT NULL,
    type              VARCHAR(32),
    name              VARCHAR(255),
    file_id           TEXT,
    file_info         TEXT,
    status            VARCHAR(16),
    state             VARCHAR(1) NOT NULL,
    remark            VARCHAR(16),
    create_time       TIMESTAMP  NOT NULL,
    create_id         INT,
    modify_time       TIMESTAMP,
    modify_id         INT
);
COMMENT ON TABLE t_knowledge IS '知识库表';
COMMENT ON COLUMN t_knowledge.id IS '主键';
COMMENT ON COLUMN "t_knowledge"."knowledge_base_id" IS '知识库id';
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

CREATE TABLE "t_knowledge_base"
(
    "id"          SERIAL PRIMARY KEY,
    "user_id"     INT        NOT NULL,
    "name"        VARCHAR(255),
    "state"       VARCHAR(1) NOT NULL,
    "remark"      VARCHAR(16),
    "create_time" TIMESTAMP  NOT NULL,
    "create_id"   INT,
    "modify_time" TIMESTAMP,
    "modify_id"   INT
);
COMMENT ON COLUMN "t_knowledge_base"."id" IS '主键';
COMMENT ON COLUMN "t_knowledge_base"."user_id" IS '用户id';
COMMENT ON COLUMN "t_knowledge_base"."name" IS '知识库名称';
COMMENT ON COLUMN "t_knowledge_base"."state" IS '状态';
COMMENT ON COLUMN "t_knowledge_base"."remark" IS '备注';
COMMENT ON COLUMN "t_knowledge_base"."create_time" IS '创建时间';
COMMENT ON COLUMN "t_knowledge_base"."create_id" IS '创建人id';
COMMENT ON COLUMN "t_knowledge_base"."modify_time" IS '编辑时间';
COMMENT ON COLUMN "t_knowledge_base"."modify_id" IS '编辑人id';
COMMENT ON TABLE "t_knowledge_base" IS '知识库';

CREATE TABLE t_knowledge_section
(
    id                SERIAL PRIMARY KEY,
    knowledge_base_id INT        NOT NULL,
    knowledge_id      INT        NOT NULL,
    user_id INT NOT NULL,
    cut_content       TEXT,
    embedding         vector(1024),
    state             VARCHAR(1) NOT NULL,
    remark            VARCHAR(16),
    create_time       TIMESTAMP  NOT NULL,
    create_id         INT,
    modify_time       TIMESTAMP,
    modify_id         INT
);
COMMENT ON TABLE t_knowledge_section IS '知识库数据切片';
COMMENT ON COLUMN t_knowledge_section.id IS '主键';
COMMENT ON COLUMN "t_knowledge_section"."knowledge_base_id" IS '知识库id';
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

CREATE TABLE "t_model_info"
(
    "id"           SERIAL PRIMARY KEY,
    "company"      VARCHAR(16),
    "name"         VARCHAR(64),
    "type" VARCHAR(16),
    "model"        VARCHAR(64),
    "introduction" VARCHAR(255),
    "url"          VARCHAR(255),
    "sort"    int2,
    "api_key" VARCHAR(255)
);
COMMENT ON COLUMN "t_model_info"."id" IS '模型主键';
COMMENT ON COLUMN "t_model_info"."company" IS '模型公司';
COMMENT ON COLUMN "t_model_info"."name" IS '模型名称';
COMMENT
ON COLUMN "t_model_info"."type" IS '类型';
COMMENT ON COLUMN "t_model_info"."model" IS '模型类型';
COMMENT ON COLUMN "t_model_info"."introduction" IS '简介';
COMMENT ON COLUMN "t_model_info"."url" IS 'api url';
COMMENT ON COLUMN "t_model_info"."sort" IS '排序';
COMMENT ON COLUMN "t_model_info"."api_key" IS 'API KEY';
COMMENT ON TABLE "t_model_info" IS '模型配置';