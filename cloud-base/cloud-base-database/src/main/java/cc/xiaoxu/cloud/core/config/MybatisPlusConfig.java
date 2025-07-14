package cc.xiaoxu.cloud.core.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * <p>MybatisPlusConfig 配置包扫面和开启全局事务管理</p>
 *
 * @author 小徐
 * @since 2024/3/15 15:57
 */
@Slf4j
@Configuration
@MapperScan(basePackages = {"cc.xiaoxu.cloud.**.dao"})
@EnableTransactionManagement
public class MybatisPlusConfig {

    /**
     * 新的分页插件，一缓和二缓遵循 MyBatis 的规则，需要设置 MybatisConfiguration#useDeprecatedExecutor = false 避免缓存出现问题
     */
    @Bean
    @ConditionalOnExpression("#{environment.getProperty('spring.datasource.url').startsWith('jdbc:mysql')}")
    public MybatisPlusInterceptor mybatisPlusInterceptorMysql() {
        log.error("使用 MySQL");
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    @ConditionalOnExpression("#{environment.getProperty('spring.datasource.url').startsWith('jdbc:sqlite')}")
    public MybatisPlusInterceptor mybatisPlusInterceptorSqlite() {
        log.error("使用 sqlite");
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.SQLITE));
        return interceptor;
    }

    @Bean
    @ConditionalOnExpression("#{environment.getProperty('spring.datasource.url').startsWith('jdbc:postgresql')}")
    public MybatisPlusInterceptor mybatisPlusInterceptorPostgresql() {
        log.error("使用 postgresql");
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.POSTGRE_SQL));
        return interceptor;
    }
}