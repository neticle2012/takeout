package org.neticle.takeout.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Faruku123
 * @version 1.0
 * 配置MP的分页插件
 */
@Configuration
public class MybatisPlusConfig {
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor mpi = new MybatisPlusInterceptor();
        mpi.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return mpi;
    }
}
