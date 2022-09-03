package org.neticle.takeout.config;

import org.neticle.takeout.filter.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author Faruku123
 * @version 1.0
 */
@SuppressWarnings({"all"})
@Configuration
public class SecurityConfig {
    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable() //关闭csrf
                //不通过Session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                // 对于登录接口 允许匿名访问
                .antMatchers("/employee/login").anonymous()
                //放行请求
                .antMatchers("/backend/**", "/front/**", "/common/**").permitAll()
                .antMatchers("/user/sendMsg", "/user/login").permitAll()
                .antMatchers("/doc.html", "/webjars/**", "/swagger-resources/**", "/v2/api-docs").permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated()
                .and()
                //使用iframe的内嵌页面
                .headers().frameOptions().disable();
        //将自定义的jwt认证过滤器放在SpringSecurity过滤器链中UsernamePasswordAuthenticationFilter过滤器之前
        http.addFilterBefore(jwtAuthenticationTokenFilter,
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
