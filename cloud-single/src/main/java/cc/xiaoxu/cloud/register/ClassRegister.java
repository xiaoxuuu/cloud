package cc.xiaoxu.cloud.register;

import indi.easy.handler.RequestUrlFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

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
