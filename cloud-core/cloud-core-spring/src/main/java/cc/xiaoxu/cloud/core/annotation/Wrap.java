package cc.xiaoxu.cloud.core.annotation;

import java.lang.annotation.*;

/**
 * <p>用于将 controller 返回值用 {@link cc.xiaoxu.cloud.core.bean.vo.R ResponseVO} 包裹</p>
 * <p>可作用于方法于类上</p>
 * <p>依赖 {@link cc.xiaoxu.cloud.core.advice.ResponseBodyWrapAdvice JSONResponseBodyAdvice}</p>
 *
 * @author 小徐
 * @since 2022/10/18 13:58
 */
// 作用范围
@Target({ElementType.TYPE, ElementType.METHOD})
// 作用范围
@Retention(RetentionPolicy.RUNTIME)
// 作用范围
@Documented
// 表明该使用该注解的类可被继承，继承类同样会被标记上此注解
@Inherited
public @interface Wrap {

    /**
     * 是否禁用
     */
    boolean disabled() default false;
}