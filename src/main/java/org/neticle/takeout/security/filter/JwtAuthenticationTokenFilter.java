package org.neticle.takeout.security.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.common.BaseContext;
import org.neticle.takeout.security.authentication.BackendUsernamePasswordAuthenticationToken;
import org.neticle.takeout.security.userdetail.EmployeeDetail;
import org.neticle.takeout.utils.JwtUtil;
import org.neticle.takeout.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author Faruku123
 * @version 1.0
 * 自定义认证过滤器
 */
@Slf4j
public class JwtAuthenticationTokenFilter extends BasicAuthenticationFilter {
    @Autowired
    private RedisCache redisCache;

    public JwtAuthenticationTokenFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //1. 获取请求header中的token
        String token = request.getHeader("authorization");
        // 这里如果没有token，直接放行，因为后面还有鉴权管理器等去判断是否拥有身份凭证，所以是可以放行的
        // 没有jwt相当于匿名访问，若有一些接口是需要权限的，则不能访问这些接口
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        //2. 解析token
        String empId;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            empId = claims.getSubject();
        } catch (ExpiredJwtException e) {
            log.warn("token已过期");
            throw new JwtException("token不合法！");
        } catch (Exception e) {
            log.warn("token错误");
            throw new JwtException("token错误");
        }
        //3. 获取empId，从redis中获取员工信息
        EmployeeDetail empDetail = redisCache.getCacheObject("login:"+ empId);
        if (Objects.isNull(empDetail)) {
            throw new RuntimeException("当前员工未登录！");
        }
        log.info("后台用户已登录，用户id为: {}", empId);
        BaseContext.setCurrentId(Long.parseLong(empId));//为当前请求对应的线程设置线程内共享empId
        log.info("线程id = {}", Thread.currentThread().getId());
        //4. 封装Authentication，这里密码为null，是因为已经提供了正确的JWT，实现了自动登录
        BackendUsernamePasswordAuthenticationToken bupat
                = new BackendUsernamePasswordAuthenticationToken(empDetail, null, null);
        //5. 存入SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(bupat);
        filterChain.doFilter(request, response);
    }
}
