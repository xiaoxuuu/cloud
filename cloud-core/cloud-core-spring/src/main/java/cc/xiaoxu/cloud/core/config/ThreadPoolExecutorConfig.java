package cc.xiaoxu.cloud.core.config;

import cc.xiaoxu.cloud.core.threads.Threads;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolExecutorConfig {

    /**
     * 当前机器的CPU核心数
     */
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    /**
     * 最大线程数量
     */
    private static final int MAX_POOL_SIZE = 50;

    /**
     * 核心线程数量
     */
    private static final int CORE_POOL_SIZE = 25;

    /**
     * 队列大小
     */
    private static final int QUEUE_CAPACITY = 25;

    /**
     * 存活时间
     */
    private static final int KEEP_ALIVE_SECONDS = 300;

    @Bean("threadPoolExecutor")
    public ThreadPoolTaskExecutor getAsyncExecutor() {

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

        // 核心线程数（默认线程数）
        taskExecutor.setCorePoolSize(CORE_POOL_SIZE);

        // 最大线程数
        taskExecutor.setMaxPoolSize(MAX_POOL_SIZE);

        // 缓冲队列大小
        taskExecutor.setQueueCapacity(QUEUE_CAPACITY);

        // 允许线程空闲时间（单位：默认为秒）
        taskExecutor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);

        // 线程池对拒绝任务的处理策略
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardOldestPolicy());
        taskExecutor.initialize();
        return taskExecutor;
    }

    @Bean(name = "threadPoolTaskExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(MAX_POOL_SIZE);
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setQueueCapacity(QUEUE_CAPACITY);
        executor.setKeepAliveSeconds(KEEP_ALIVE_SECONDS);
        // 线程名字前缀
        executor.setThreadNamePrefix("threadPool-");
        // 线程池对拒绝任务(无线程可用)的处理策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 设置核心线程池超时关闭,默认false一直存在
        executor.setAllowCoreThreadTimeOut(true);
        // 设置普通线程超时关闭时间
        executor.setKeepAliveSeconds(300);
        executor.initialize();
        return executor;
    }

    /**
     * 执行周期性或定时任务
     */
    @Bean(name = "scheduledExecutorService")
    protected ScheduledExecutorService scheduledExecutorService() {

        return new ScheduledThreadPoolExecutor(CORE_POOL_SIZE,
                new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d").daemon(true).build(),
                new ThreadPoolExecutor.CallerRunsPolicy()) {

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);
                Threads.printException(r, t);
            }
        };
    }
}