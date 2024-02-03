package cc.xiaoxu.cloud.core.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <p>高并发场景下 {@link System#currentTimeMillis} 的性能问题的优化</p>
 * <p>时间戳打印建议使用</p>
 *
 * 2022.01.27 下午 3:13
 *
 * @author 小徐
 */
public class SystemClockUtils {

    private static final String THREAD_NAME = "system.clock";
    private static final SystemClockUtils MILLIS_CLOCK = new SystemClockUtils(1);
    private final long precision;
    private final AtomicLong now;

    private SystemClockUtils(long precision) {
        this.precision = precision;
        now = new AtomicLong(System.currentTimeMillis());
        scheduleClockUpdating();
    }

    public static SystemClockUtils millisClock() {
        return MILLIS_CLOCK;
    }

    private void scheduleClockUpdating() {

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, THREAD_NAME);
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleAtFixedRate(() ->
                now.set(System.currentTimeMillis()), precision, precision, TimeUnit.MILLISECONDS);
    }

    public long now() {
        return now.get();
    }
}