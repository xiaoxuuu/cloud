package cc.xiaoxu.cloud.core.advice;

import cc.xiaoxu.cloud.core.annotation.Wrap;
import cc.xiaoxu.cloud.core.bean.vo.R;
import cc.xiaoxu.cloud.core.utils.bean.JsonUtils;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Objects;

/**
 * <p>对所有 cc.xiaoxu.cloud 目录下加了 @RestWrapper 注解的控制器进行处理</p>
 *
 * @author 小徐
 * @since 2021/11/18 15:46
 */
@RestControllerAdvice(basePackages = "cc.xiaoxu.cloud")
public class ResponseBodyWrapAdvice implements ResponseBodyAdvice<Object> {

    /**
     * 该组件是否支持给定的控制器方法返回类型
     *
     * @param converterType 选择的转换器类型
     * @return {@code true} {@link #beforeBodyWrite}应该被调用;
     * {@code false}否则无任何操作
     */
    @Override
    public boolean supports(@NotNull MethodParameter returnType, @NotNull Class<? extends HttpMessageConverter<?>> converterType) {

        return true;
    }

    /**
     * 确认需要拦截后执行此方法对 Response 返回值进行拦截处理
     * 在{@code HttpMessageConverter}被选中之后和之前调用
     * 它的 write 方法被调用。
     *
     * @param body                要写入的主体
     * @param returnType          通过内容协商选择的内容类型
     * @param selectedContentType 选择写入响应的转换器类型
     * @param request             当前请求
     * @param response            当前响应
     * @return 传入的主体或修改过的(可能是新的)实例
     */
    @Override
    @SuppressWarnings("all")
    public Object beforeBodyWrite(Object body, @NotNull MethodParameter returnType, @NotNull MediaType selectedContentType,
                                  @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response) {

        boolean needWrapper = needWrapper(returnType);
        if (!needWrapper) {
            return body;
        }

        boolean bodyIsString = returnType.getParameterType() == String.class;
        R<Object> r;
        Object data;
        if (body instanceof R) {
            return body;
        } else {
            data = body;
            r = R.success();
        }

        // 未被包裹，手动包裹
        r.setData(data);
        return bodyIsString ? JsonUtils.toString(r) : r;
    }

    private boolean needWrapper(MethodParameter methodParameter) {

        // 解析方法上的注解
        if (methodParameter.getAnnotatedElement().isAnnotationPresent(Wrap.class)) {
            Wrap wrap = Objects.requireNonNull(methodParameter.getMethod()).getAnnotation(Wrap.class);
            // 输出注解属性
            return !wrap.disabled();
        }
        return true;
    }
}