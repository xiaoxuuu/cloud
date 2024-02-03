package cc.xiaoxu.cloud.my.navigation.task;

import cc.xiaoxu.cloud.my.navigation.service.NavWebsiteIconService;
import cc.xiaoxu.cloud.my.navigation.service.NavWebsiteService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebsiteCheckTask {

    @Resource
    private NavWebsiteService navWebsiteService;

    @Resource
    private NavWebsiteIconService navWebsiteIconService;

    /**
     * 定时缓存数据到内存
     */
    @Scheduled(cron = "${app.config.task-corn}")
    public void refreshData() {

        log.debug("刷新数据至缓存...");
        navWebsiteService.setNavList(navWebsiteService.getList());
        navWebsiteIconService.setNavIconMap(navWebsiteIconService.getList());
    }

    /**
     * TODO 每天抓取一次数据到数据库
     */
}