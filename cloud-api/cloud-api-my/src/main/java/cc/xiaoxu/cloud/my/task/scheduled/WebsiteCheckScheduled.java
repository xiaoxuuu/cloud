package cc.xiaoxu.cloud.my.task.scheduled;

import cc.xiaoxu.cloud.core.cache.CacheService;
import cc.xiaoxu.cloud.core.utils.DateUtils;
import cc.xiaoxu.cloud.my.bean.constant.CacheConstant;
import cc.xiaoxu.cloud.my.bean.mysql.NavWebsite;
import cc.xiaoxu.cloud.my.bean.mysql.NavWebsiteIcon;
import cc.xiaoxu.cloud.my.service.NavWebsiteIconService;
import cc.xiaoxu.cloud.my.service.NavWebsiteService;
import cc.xiaoxu.cloud.my.utils.WebsiteUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
public class WebsiteCheckScheduled {

    @Resource
    private NavWebsiteService navWebsiteService;

    @Resource
    private NavWebsiteIconService navWebsiteIconService;

    @Resource
    private CacheService cacheService;

    @Scheduled(cron = "${app.config.refresh-data}")
    public void refreshData() {

        refreshIcon();
        refreshUrl();
    }

    public void refreshIcon() {

        log.debug("刷新图标数据至 Redis...");
        List<NavWebsiteIcon> iconList = navWebsiteIconService.getList();
        Map<String, NavWebsiteIcon> iconMap = iconList.stream().collect(Collectors.toMap(NavWebsiteIcon::getId, a -> a));
        cacheService.setCacheObject(CacheConstant.NAV_ICON_MAP, iconMap);
    }

    public void refreshUrl() {

        log.debug("刷新网站数据至缓存...");
        List<NavWebsite> navWebsiteList = navWebsiteService.getList();
        navWebsiteService.setNavList(navWebsiteService.getList());
    }

    @Scheduled(cron = "${app.config.refresh-website-name}")
    public void getWebsiteName() {

        long currentTimeMillis = System.currentTimeMillis();
        List<NavWebsite> navWebsiteList = navWebsiteService.getList();
        log.info("获取网站名称数据 {}...", navWebsiteList.size());

        for (int i = 0; i < navWebsiteList.size(); i++) {
            NavWebsite navWebsite = navWebsiteList.get(i);
            String lastAvailableTime = navWebsite.getLastAvailableTime();
            // 跳过 72 小时内成功访问的数据
            if (StringUtils.isNotBlank(lastAvailableTime)) {
                long oldDateMillis = DateUtils.stringToLocalDateTime(lastAvailableTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                long time = currentTimeMillis - oldDateMillis;
                if (time < 1000 * 60 * 60 * 24 * 3) {
                    continue;
                }
            }

            String websiteTitle;
            boolean success = true;
            try {
                websiteTitle = WebsiteUtil.getTitle(navWebsite.getUrl());
            } catch (Exception e) {
                websiteTitle = "";
                success = false;
                log.warn("未获取到网站标题：【{}/{}】【{}】【{}】", (i + 1), navWebsiteList.size(), navWebsite.getShortName(), navWebsite.getUrl());
            }
            if (success) {
                log.info("获取到网站名称：【{}/{}】【{}】【{}】", (i + 1), navWebsiteList.size(), websiteTitle, navWebsite.getShortName());
                navWebsite.setLastAvailableTime(DateUtils.getNowString());
            }
            navWebsiteService.lambdaUpdate()
                    .eq(NavWebsite::getId, navWebsite.getId())
                    .set(NavWebsite::getWebsiteName, websiteTitle)
                    .set(success, NavWebsite::getLastAvailableTime, DateUtils.getNowString())
                    .update();
        }
        log.info("操作结束...");
        refreshUrl();
    }
}