package org.neticle.takeout.security.config;

import org.neticle.takeout.security.filter.EmpLogoutFilter;
import org.neticle.takeout.security.filter.JwtAuthenticationTokenFilter;
import org.neticle.takeout.security.handler.*;
import org.neticle.takeout.security.filter.EmpLoginCheckFilter;
import org.neticle.takeout.security.provider.BackendDaoAuthentationProvider;
import org.neticle.takeout.security.userdetail.EmployeeDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * @author Faruku123
 * @version 1.0
 */
@Configuration
public class SecurityConfig {
    @Autowired
    private EmployeeDetailServiceImpl employeeDetailServiceImpl;
    @Autowired
    private BackendAuthenticationSuccessHandlerImpl backendAuthenticationSuccessHandlerImpl;
    @Autowired
    private BackendAuthenticationFailureHandlerImpl backendAuthenticationFailureHandlerImpl;
    @Autowired
    private JwtAuthenticationEntryPointImpl jwtAuthenticationEntryPoint;
    @Autowired
    private BackendLogoutHandlerImpl backendLogoutHandlerImpl;
    @Autowired
    private BackendLogoutSuccessHandlerImpl backendLogoutSuccessHandlerImpl;
    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    BackendDaoAuthentationProvider backendDaoAuthentationProvider() {
        BackendDaoAuthentationProvider backendDaoAuthentationProvider
                = new BackendDaoAuthentationProvider();
        backendDaoAuthentationProvider.setUserDetailsService(employeeDetailServiceImpl);
        backendDaoAuthentationProvider.setPasswordEncoder(passwordEncoder());
        return new BackendDaoAuthentationProvider();
    }

    @Bean
    JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() throws Exception {
        return new JwtAuthenticationTokenFilter(authenticationManager());
    }

    @Bean
    EmpLoginCheckFilter empLoginCheckFilter() throws Exception {
        EmpLoginCheckFilter empLoginCheckFilter = new EmpLoginCheckFilter();
        //设置要求的用户名和密码对应的请求参数名
        empLoginCheckFilter.setUsernameParameter("username");
        empLoginCheckFilter.setPasswordParameter("password");
        //设置该过滤器应该拦截的请求
        empLoginCheckFilter.setFilterProcessesUrl("/employee/login");
        //设置该过滤器的AuthenticationManager
        empLoginCheckFilter.setAuthenticationManager(authenticationManager());
        //自定义登录成功的处理
        empLoginCheckFilter.setAuthenticationSuccessHandler(backendAuthenticationSuccessHandlerImpl);
        //自定义登录失败的处理
        empLoginCheckFilter.setAuthenticationFailureHandler(backendAuthenticationFailureHandlerImpl);
        return empLoginCheckFilter;
    }

    @Bean
    EmpLogoutFilter empLogoutFilter() {
        EmpLogoutFilter empLogoutFilter
                = new EmpLogoutFilter(backendLogoutSuccessHandlerImpl, backendLogoutHandlerImpl);
        empLogoutFilter.setLogoutRequestMatcher(new OrRequestMatcher(
                new AntPathRequestMatcher("/employee/logout") //可以匹配多个请求路径
        ));
        return empLogoutFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // 对于前台和后台的登录接口 允许匿名访问
                //匿名访问的应用：可以在不登录的情况下阅览网站的一些内容
                .antMatchers("/employee/login").anonymous()
                //放行请求（静态资源、前台短信验证码发送、swagger文档）
                .antMatchers("/backend/**", "/front/**", "/common/**").permitAll()
                //注销登录放行加不加都一样（因为处理注销登录的Filter在过滤器链前面）
                .antMatchers("/employee/logout").permitAll()
                .antMatchers("/user/sendMsg").permitAll()
                .antMatchers("/doc.html", "/webjars/**", "/swagger-resources/**", "/v2/api-docs").permitAll()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated()
                .and()
                .authenticationManager(authenticationManager())
                .authenticationProvider(backendDaoAuthentationProvider())
                .exceptionHandling()//自定义登录/认证/授权中的异常处理
                //认证失败，对应未登录情况下访问资源
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .csrf().disable() //关闭csrf
                //不通过Session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                //使用iframe的内嵌页面
                .headers().frameOptions().disable();
        // at: 用来某个 filter 替换过滤器链中哪个 filter
        // before: 放在过滤器链中哪个 filter 之前
        // after: 放在过滤器链中那个 filter 之后
        //注意：LogoutFilter原本位于UsernamePasswordAuthenticationFilter之前
        //如果在注销登录时想使用authentication，就必须将自定义的jwtAuthenticationTokenFilter放在
        //自定义的empLogoutFilter之前
        http.addFilterAt(empLoginCheckFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthenticationTokenFilter(), LogoutFilter.class)
            .addFilterAt(empLogoutFilter(), LogoutFilter.class);
        return http.build();
    }
}
