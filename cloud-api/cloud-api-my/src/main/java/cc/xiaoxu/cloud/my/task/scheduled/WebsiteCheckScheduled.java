package cc.xiaoxu.cloud.my.task.scheduled;

import cc.xiaoxu.cloud.assistant.ChatAssistant;
import cc.xiaoxu.cloud.bean.WebExtractDTO;
import cc.xiaoxu.cloud.bean.WebExtractResultDTO;
import cc.xiaoxu.cloud.core.utils.DateUtils;
import cc.xiaoxu.cloud.core.utils.bean.JsonUtils;
import cc.xiaoxu.cloud.core.utils.set.ListUtils;
import cc.xiaoxu.cloud.my.entity.NavWebsite;
import cc.xiaoxu.cloud.my.entity.NavWebsiteIcon;
import cc.xiaoxu.cloud.my.service.NavWebsiteIconService;
import cc.xiaoxu.cloud.my.service.NavWebsiteService;
import cc.xiaoxu.cloud.my.utils.WebsiteUtil;
import cc.xiaoxu.cloud.utils.SearchManager;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Date;
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
    private ChatAssistant chatAssistant;

    @Resource
    private SearchManager searchManager;

    @Scheduled(cron = "${app.config.refresh-data}")
    public void refreshData() {

        refreshIcon();
        refreshUrl();
    }

    public void refreshIcon() {

        log.debug("刷新图标数据至 Redis...");
        List<NavWebsiteIcon> iconList = navWebsiteIconService.getList();
        navWebsiteIconService.setNavIconMap(iconList);
    }

    public void refreshUrl() {

        log.debug("刷新网站数据至缓存...");
        navWebsiteService.setNavList(navWebsiteService.getList());
    }

    @SneakyThrows
    @Scheduled(cron = "${app.config.refresh-website-name}")
    public void getWebsiteDesc() {

        List<NavWebsite> navWebsiteList = navWebsiteService.getList();
        log.info("获取网站简介数据 {}...", navWebsiteList.size());

        navWebsiteList = navWebsiteList.stream().filter(k -> StringUtils.isBlank(k.getDescription())).toList();

        // 每次从 navWebsiteList 取出 5 条数据
        List<List<NavWebsite>> lists = ListUtils.splitList(navWebsiteList, 5);
        for (List<NavWebsite> websiteList : lists) {
            List<String> urlList = websiteList.stream().map(NavWebsite::getUrl).toList();
            Map<String, NavWebsite> websiteMap = websiteList.stream().collect(Collectors.toMap(NavWebsite::getUrl, a -> a));

            WebExtractDTO extract = searchManager.extract(urlList);
            // 成功
            for (WebExtractResultDTO result : extract.getResults()) {
                NavWebsite website = websiteMap.get(result.getUrl());
                if (null == website) {
                    continue;
                }

                String desc = chatAssistant.analysis(website.getUrl(), website.getShortName(), website.getWebsiteName(), result.getRawContent());
                ChatRes chatRes = JsonUtils.parse(desc, ChatRes.class);
                navWebsiteService.lambdaUpdate()
                        .eq(NavWebsite::getId, website.getId())
                        .set(NavWebsite::getDescription, chatRes.description)
                        .set(StringUtils.isBlank(website.getWebsiteName()), NavWebsite::getWebsiteName, chatRes.website_name)
                        .set(NavWebsite::getLastAvailableTime, DateUtils.getNowDate())
                        .update();
                websiteMap.remove(result.getUrl());

                Thread.sleep(30 * 1000);
            }

            if (!websiteMap.isEmpty()) {
                navWebsiteService.lambdaUpdate()
                        .in(NavWebsite::getId, websiteMap.values().stream().map(NavWebsite::getId).toList())
                        .set(NavWebsite::getDescription, "-")
                        .update();
            }
        }
        refreshUrl();
    }

    public record ChatRes(String website_name, String short_name, String description) {
    }

    @Scheduled(cron = "${app.config.refresh-website-name}")
    public void getWebsiteName() {

        long currentTimeMillis = System.currentTimeMillis();
        List<NavWebsite> navWebsiteList = navWebsiteService.getList();
        log.info("获取网站名称数据 {}...", navWebsiteList.size());

        for (int i = 0; i < navWebsiteList.size(); i++) {
            NavWebsite navWebsite = navWebsiteList.get(i);
            Date lastAvailableTime = navWebsite.getLastAvailableTime();
            // 跳过 72 小时内成功访问的数据
            if (null == lastAvailableTime) {
                long oldDateMillis = DateUtils.toLocalDateTime(lastAvailableTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                long time = currentTimeMillis - oldDateMillis;
                if (time < 1000 * 60 * 60 * 24 * 3) {
                    continue;
                }
            }

            // 网站标题处理
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
                navWebsite.setLastAvailableTime(DateUtils.getNowDate());
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