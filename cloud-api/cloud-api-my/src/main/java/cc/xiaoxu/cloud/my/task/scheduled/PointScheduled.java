package cc.xiaoxu.cloud.my.task.scheduled;

import cc.xiaoxu.cloud.my.service.PointService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PointScheduled {

    @Resource
    private PointService pointService;

    @Scheduled(cron = "${app.config.refresh-data}")
    public void refreshData() {

        log.debug("刷新点位数据至缓存...");
        pointService.updateCacheList();
    }
}