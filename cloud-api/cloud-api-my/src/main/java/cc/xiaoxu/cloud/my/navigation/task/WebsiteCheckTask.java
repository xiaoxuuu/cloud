package cc.xiaoxu.cloud.my.navigation.task;

import cc.xiaoxu.cloud.core.utils.DateUtils;
import cc.xiaoxu.cloud.my.navigation.bean.entity.NavWebsite;
import cc.xiaoxu.cloud.my.navigation.service.NavWebsiteIconService;
import cc.xiaoxu.cloud.my.navigation.service.NavWebsiteService;
import cc.xiaoxu.cloud.my.navigation.utils.WebsiteUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;

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
    @Scheduled(cron = "${app.config.refresh-data}")
    public void refreshData() {

        log.debug("刷新数据至缓存...");
        navWebsiteService.setNavList(navWebsiteService.getList());
        navWebsiteIconService.setNavIconMap(navWebsiteIconService.getList());
    }

    /**
     * 每天抓取一次数据到数据库
     */
    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "${app.config.refresh-website-name}")
    public void getWebsiteName() {

        long currentTimeMillis = System.currentTimeMillis();
        List<NavWebsite> navWebsiteList = navWebsiteService.getList();
        log.info("获取网站名称数据...");
        for (NavWebsite navWebsite : navWebsiteList) {
            long oldDateMillis = DateUtils.stringToLocalDateTime(navWebsite.getLastAvailableTime()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            long time = currentTimeMillis - oldDateMillis;
            if (time < 1000 * 60 * 60 * 23) {
                continue;
            }

            log.debug("获取网站名称数据：【{}】【{}】", navWebsite.getShortName(), navWebsite.getUrl());
            String websiteTitle;
            boolean success = true;
            try {
                websiteTitle = WebsiteUtil.getWebsiteTitle(navWebsite.getUrl());
            } catch (Exception e) {
                websiteTitle = e.getMessage();
                success = false;
                log.error("未获取到网站标题：【{}】【{}】", navWebsite.getShortName(), navWebsite.getUrl());
            }
            websiteTitle = StringUtils.isBlank(websiteTitle) ? "获取到空数据" : websiteTitle;
            log.info("获取到网站名称：【{}】【{}】", websiteTitle, navWebsite.getShortName());
            if (success) {
                navWebsite.setLastAvailableTime(DateUtils.getNowTime());
            }
            navWebsiteService.lambdaUpdate()
                    .eq(NavWebsite::getId, navWebsite.getId())
                    .set(NavWebsite::getWebsiteName, websiteTitle)
                    .set(success, NavWebsite::getLastAvailableTime, DateUtils.getNowTime())
                    .update();
        }
        refreshData();
    }
}