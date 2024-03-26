package cc.xiaoxu.cloud.core.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.ScheduledExecutorService;

/**
 * <p>定时任务配置</p>
 *
 * @author 小徐
 * @since 2023/7/8 12:01
 */
@Configuration
public class ScheduleConfig implements SchedulingConfigurer {

    @Resource
    private ScheduledExecutorService scheduledExecutorService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(scheduledExecutorService);
    }
}