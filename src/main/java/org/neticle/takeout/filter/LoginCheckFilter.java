package org.neticle.takeout.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.common.R;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Faruku123
 * @version 1.0
 * 检查用户是否已经完成登录
 */
@Slf4j
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 1、获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("拦截到请求: {}", requestURI);
        // 定义不需要处理的请求路径：servlet请求（/employee/login、employee/logout）
        // 静态资源请求（/backend/**、/front/**）
        String[] urls = {"/employee/login", "employee/logout", "/backend/**", "/front/**"};
        // 2、判断本次请求是否需要处理，如果不需要处理，则直接放行
        boolean check = check(urls, requestURI);
        if (check){
            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        // 3、判断登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户id为: {}", request.getSession().getAttribute("employee"));
            filterChain.doFilter(request, response);
            return;
        }
        // 4、如果未登录则返回未登录结果，通过输出流向客户端页面响应数据
        //这里还需要将R对象手动转换为json返回
        //而在Controller中是将R对象自动转为json（SpringBoot自动配置）放入响应体（@RestController注解）
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     */
    public boolean check(String[] urls, String requestURI){
        for (String url : urls) {
            if (PATH_MATCHER.match(url, requestURI)){
                return true;
            }
        }
        return false;
    }
}