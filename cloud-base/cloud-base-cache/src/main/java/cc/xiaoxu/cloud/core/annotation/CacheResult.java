package cc.xiaoxu.cloud.core.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 缓存出参
 * <p>
 * 2022.03.23 上午 10:59
 *
 * @author XiaoXu
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CacheResult {

    /**
     * 实际出参 class 类型
     */
    Class<?> clazz();

    /**
     * 出参是否为集合类型
     */
    boolean list() default false;

    /**
     * TODO 出参是否需要被 ResponseVO 包裹
     */
    boolean wrapped() default false;

    /**
     * 自动续签：当从缓存读取到数据时，是否重置此数据过期时间
     */
    boolean renewal() default true;

    /**
     * 默认超时时间
     */
    long timeout() default 1L;

    /**
     * 默认超时时间单位 {@link TimeUnit TimeUnit}
     */
    TimeUnit timeUnit() default TimeUnit.HOURS;

    /**
     * dev 环境不进行缓存
     */
    boolean devSkip() default true;
}