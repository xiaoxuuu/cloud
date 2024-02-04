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

        log.warn("获取网站名称数据...");
        List<NavWebsite> navWebsiteList = navWebsiteService.getList();
        for (NavWebsite navWebsite : navWebsiteList) {
            log.warn("获取网站名称数据：【{}】【{}】", navWebsite.getShortName(), navWebsite.getUrl());
            String websiteTitle;
            boolean success = true;
            try {
                websiteTitle = WebsiteUtil.getWebsiteTitle(navWebsite.getUrl());
            } catch (Exception e) {
                websiteTitle = e.getMessage();
                success = false;
            }
            websiteTitle = StringUtils.isBlank(websiteTitle) ? "获取到空数据" : websiteTitle;
            log.warn("获取到网站名称：【{}】【{}】", websiteTitle, navWebsite.getShortName());
            if (success) {
                navWebsite.setLastAvailableTime(DateUtils.getNowTime());
            }
            // FIXME 无法保存
//            navWebsiteService.updateById(navWebsite);
//            navWebsiteService.lambdaUpdate()
//                    .eq(NavWebsite::getId, navWebsite.getId())
//                    .set(NavWebsite::getWebsiteName, websiteTitle)
//                    .set(success, NavWebsite::getLastAvailableTime, DateUtils.getNowTime())
//                    .update();
        }
        refreshData();
    }
}