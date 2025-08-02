package cc.xiaoxu.cloud.my.task.scheduled;

import cc.xiaoxu.cloud.my.manager.PointManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PointScheduled {

    @Resource
    private PointManager pointManager;

    @Scheduled(cron = "${app.config.refresh-data}")
    public void refreshData() {

        pointManager.updatePointList();
        pointManager.updatePointMapList();
        pointManager.updatePointSourceList();
    }
}