package org.neticle.takeout.filter;

import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.common.BaseContext;
import org.neticle.takeout.dto.EmployeeDto;
import org.neticle.takeout.utils.JwtUtil;
import org.neticle.takeout.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
// OncePerRequestFilter 只走一次，在请求前执行
@Component
@Slf4j
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
    @Autowired
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //1. 获取请求header中的token
        String token = request.getHeader("Authorization");
        //不存在token则放行，让SpringSecurity自带的后续过滤器来处理
        if (!StringUtils.hasText(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        //2. 解析token
        String empId;
        try {
            Claims claims = JwtUtil.parseJWT(token);
            empId = claims.getSubject();
        } catch (Exception e) {
            throw new RuntimeException("token不合法！");
        }
        //3. 获取empId，从redis中获取员工信息
        EmployeeDto empDto = redisCache.getCacheObject("login:"+ empId);
        if (Objects.isNull(empDto)) {
            throw new RuntimeException("当前员工未登录！");
        }
        log.info("后台用户已登录，用户id为: {}", empId);
        BaseContext.setCurrentId(Long.parseLong(empId));//为当前请求对应的线程设置线程内共享empId
        //4. 封装Authentication
        UsernamePasswordAuthenticationToken upat
                = new UsernamePasswordAuthenticationToken(empDto, null, null);
        //5. 存入SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(upat);
        filterChain.doFilter(request, response);
    }
}
