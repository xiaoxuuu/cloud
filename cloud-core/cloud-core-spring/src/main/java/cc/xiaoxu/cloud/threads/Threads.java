package cc.xiaoxu.cloud.threads;

import java.util.concurrent.*;

/**
 * <p>线程相关工具类</p>
 *
 * @author 小徐
 * @since 2023/6/12 17:10
 */
public class Threads {

    /**
     * sleep 等待，单位为毫秒
     */
    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * <p>停止线程池</p>
     * <p>先使用 shutdown，停止接收新任务并尝试完成所有已存在任务</p>
     * <p>如果超时，则调用 shutdownNow，取消在 workQueue 中 Pending 的任务，并中断所有阻塞函数</p>
     * <p>如果仍然超時，則強制退出</p>
     * <p>另对在 shutdown 时线程本身被调用中断做了处理</p>
     */
    public static void shutdownAndAwaitTermination(ExecutorService pool) {
        if (pool != null && !pool.isShutdown()) {
            pool.shutdown();
            try {
                if (!pool.awaitTermination(120, TimeUnit.SECONDS)) {
                    pool.shutdownNow();
                    if (!pool.awaitTermination(120, TimeUnit.SECONDS)) {
                        throw new RuntimeException("Pool did not terminate");
                    }
                }
            } catch (InterruptedException ie) {
                pool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 打印线程异常信息
     */
    public static void printException(Runnable r, Throwable t) {
        if (t == null && r instanceof Future<?>) {
            try {
                Future<?> future = (Future<?>) r;
                if (future.isDone()) {
                    future.get();
                }
            } catch (CancellationException ce) {
                t = ce;
            } catch (ExecutionException ee) {
                t = ee.getCause();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        if (t != null) {
            throw new RuntimeException(t.getMessage(), t);
        }
    }
}