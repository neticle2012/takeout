package org.neticle.takeout.security.handler;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.common.R;
import org.neticle.takeout.utils.WebUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Faruku123
 * @version 1.0
 * 自定义认证失败处理，对应未登录（没有token）就访问资源、token过期、token错误
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {
        //这里需要将R对象手动转换为json返回
        //而在Controller中是将R对象自动转为json（SpringBoot自动配置）放入响应体（@RestController注解）
        log.warn("用户未登录或者token错误，请先登录");
        WebUtils.renderString(response, JSON.toJSONString(R.error("NOTLOGIN")));
    }
}
