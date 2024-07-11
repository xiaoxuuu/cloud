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
     * 待执行代码
     */
    private final Supplier<T> trySupplier;

    /**
     * 异常补偿代码（优先）
     */
    private Supplier<T> orSupplier;

    /**
     * 默认数据（其次）
     */
    private T defaultData;

    /**
     * 空数据（兜底）
     */
    private final T emptyData = null;

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
     * 出现异常执行的代码段
     *
     * @param orSupplier 待捕获代码段
     * @return 代码执行结果
     */
    public CatchUtils<T> or(Supplier<T> orSupplier) {

        Objects.requireNonNull(orSupplier);
        this.orSupplier = orSupplier;
        return this;
    }

    /**
     * 出现异常执行的代码段
     *
     * @param t 默认数据
     * @return 代码执行结果
     */
    public CatchUtils<T> t(T t) {

        Objects.requireNonNull(t);
        this.defaultData = t;
        return this;
    }

    /**
     * finally 执行语句，无论是否出现异常，均会执行此代码
     *
     * @param finalRunnable 最终执行
     * @return this
     */
    public CatchUtils<T> last(Runnable finalRunnable) {

        this.finalRunnable = finalRunnable;
        return this;
    }

    /**
     * 执行代码，如果出现异常，执行 orSupplier，或返回 指定数据或 null
     *
     * @return 结果
     */
    public T handle() {
        try {
            return trySupplier.get();
        } catch (Exception e) {
            e.printStackTrace();
            if (null != orSupplier) {
                return orSupplier.get();
            }
        } finally {
            if (null != finalRunnable) {
                finalRunnable.run();
            }
        }
        return null == defaultData ? emptyData : defaultData;
    }
}