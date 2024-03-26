package cc.xiaoxu.cloud;

import cc.xiaoxu.cloud.core.utils.GetStartInfoUtils;
import cc.xiaoxu.cloud.my.navigation.task.WebsiteCheckTask;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableScheduling
@SpringBootApplication(scanBasePackages = "cc.xiaoxu.cloud")
@MapperScan(basePackages = {"cc.xiaoxu.cloud.my.**.dao.mysql"})
public class MyApplication {

    public static void main(String[] args) {

        // 获取 Spring Boot 上下文
        ConfigurableApplicationContext ctx = SpringApplication.run(MyApplication.class, args);
        log.error(GetStartInfoUtils.getLog(ctx));
        // 手动执行定时任务
        WebsiteCheckTask task = ctx.getBean(WebsiteCheckTask.class);
        log.warn("刷新数据");
        task.refreshData();
    }
}