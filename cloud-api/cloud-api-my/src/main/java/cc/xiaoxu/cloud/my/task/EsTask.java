package cc.xiaoxu.cloud.my.task;

import cc.xiaoxu.cloud.my.dao.es.NavWebsiteEsMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.easyes.annotation.IndexName;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EsTask {

    @Resource
    private NavWebsiteEsMapper navWebsiteEsMapper;

    /**
     * 创建 es 索引
     */
    public void createIndex() {

        IndexName indexName = navWebsiteEsMapper.getEntityClass().getAnnotation(IndexName.class);
        if (!navWebsiteEsMapper.existsIndex(indexName.value())) {
            log.warn("索引 {} 不存在，创建索引", indexName.value());
            navWebsiteEsMapper.createIndex();
        } else {
            log.info("索引 {} 存在", indexName.value());
        }
    }
}