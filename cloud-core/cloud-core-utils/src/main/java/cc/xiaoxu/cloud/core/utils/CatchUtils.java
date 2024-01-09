package cc.xiaoxu.cloud.core.utils;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * <p>异常捕获</p>
 *
 * @author 小徐
 * @since 2023/6/29 14:16
 */
public class CatchUtils<T> {

    /**
     * 消费者
     */
    private final Supplier<T> trySupplier;

    /**
     * 最终执行
     */
    private Runnable finalRunnable;

    /**
     * 禁止空参实例化
     */
    private CatchUtils() {
        throw new IllegalAccessError(this.getClass().getName());
    }

    private CatchUtils(Supplier<T> trySupplier) {
        this.trySupplier = trySupplier;
    }

    /**
     * 构造，传递待捕获代码段
     *
     * @param trySupplier 待捕获代码段
     * @param <T>         代码执行结果类型
     * @return 代码执行结果
     */
    public static <T> CatchUtils<T> of(Supplier<T> trySupplier) {

        Objects.requireNonNull(trySupplier);
        return new CatchUtils<>(trySupplier);
    }

    /**
     * finally 执行语句
     *
     * @param finalRunnable 最终执行
     * @return this
     */
    public CatchUtils<T> last(Runnable finalRunnable) {

        this.finalRunnable = finalRunnable;
        return this;
    }

    /**
     * 执行代码，如果出现异常，执行 catchSupplier
     *
     * @param catchSupplier 出现异常后的处理代码
     * @return 结果
     */
    public T handle(Supplier<T> catchSupplier) {
        try {
            return trySupplier.get();
        } catch (Exception e) {
            e.printStackTrace();
            return catchSupplier.get();
        } finally {
            if (null != finalRunnable) {
                finalRunnable.run();
            }
        }
    }
}