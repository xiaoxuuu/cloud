package cc.xiaoxu.cloud.core.annotation;

import java.lang.annotation.*;

/**
 * <p>防止重复提交</p>
 *
 * @author 小徐
 * @since 2024/6/10 上午11:13
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RepeatSubmit {

    /**
     * 间隔时间(ms)，小于此时间视为重复提交
     *
     * @return interval
     */
    int interval() default 1000;

    /**
     * 提示信息
     *
     * @return message
     */
    String message() default "不允许重复提交，请 {interval}后再试";
}