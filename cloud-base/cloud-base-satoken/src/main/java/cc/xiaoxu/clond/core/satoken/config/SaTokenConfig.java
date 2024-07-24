package cc.xiaoxu.clond.core.satoken.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * <p>路由权限拦截</p>
 *
 * @author 小徐
 * @since 2024/7/24 上午11:46
 */
@Configuration
@Slf4j
public class SaTokenConfig implements WebMvcConfigurer {

    private final ApiWhiteListProperties whiteList;

    public SaTokenConfig(ApiWhiteListProperties whiteList) {
        this.whiteList = whiteList;
    }

    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.warn("API WHITE LIST:{}", Arrays.stream(whiteList.getUrlAllArray()).toList());
        // 注册 Sa-Token 拦截器，校验规则为 StpUtil.checkLogin() 登录校验。
        registry.addInterceptor(new SaInterceptor(handle -> StpUtil.checkLogin()))
                .addPathPatterns("/**")
                .excludePathPatterns(whiteList.getUrlAllArray())
                // 手动放行
                .excludePathPatterns("/**/*.js", "/lang/*.json", "/**/*.css", "/**/*.js", "/**/*.map", "/**/*.html", "/**/*.png",
                        "/**/*.ico", "/**/*.jpg", "/favicon.ico", "/doc.html", "/webjars/**", "/swagger**/**", "/v2/**", "/v3/**");
    }

    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }
}