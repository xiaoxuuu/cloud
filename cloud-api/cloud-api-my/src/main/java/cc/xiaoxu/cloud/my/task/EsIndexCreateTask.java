package cc.xiaoxu.cloud.my.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EsIndexCreateTask {

    /**
     * 创建 es 索引
     */
    public void refreshData() {

        log.debug("刷新数据至缓存...");
    }
}