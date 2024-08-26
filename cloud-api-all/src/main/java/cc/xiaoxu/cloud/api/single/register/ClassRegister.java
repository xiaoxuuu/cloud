package cc.xiaoxu.cloud.api.single.register;

import cc.xiaoxu.cloud.api.single.handler.RequestUrlFilter;
import jakarta.annotation.Resource;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class ClassRegister {

    @Resource
    private RequestUrlFilter requestUrlFilter;

    @Bean
    public FilterRegistrationBean<RequestUrlFilter> registerFilter() {
        FilterRegistrationBean<RequestUrlFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(requestUrlFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}