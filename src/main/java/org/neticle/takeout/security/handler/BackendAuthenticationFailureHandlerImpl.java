package org.neticle.takeout.security.handler;

import com.alibaba.fastjson.JSON;
import org.neticle.takeout.common.R;
import org.neticle.takeout.utils.WebUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Faruku123
 * @version 1.0
 * 自定义后台登录失败处理
 */
@Component
public class BackendAuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {
        //用户不存在时，抛出UsernameNotFoundException
        //密码错误时，抛出BadCredentialsException
        //用户被禁用时，抛出DisabledException，这三种异常都是AuthenticationException的子类
        //UsernameNotFoundException是从EmployeeDetailServiceImpl类中抛出来的
        //BadCredentialsException和DisabledException是在自定义的BackendDaoAuthentationProvider类
        // 调用其父类DaoAuthenticationProvider等的authenticate方法去进行用户信息的验证时抛出来的
        if (exception instanceof UsernameNotFoundException ||
                exception instanceof BadCredentialsException) {
            WebUtils.renderString(response, JSON.toJSONString(R.error("登录失败，用户名或密码错误！")));
        } else if (exception instanceof DisabledException) {
            WebUtils.renderString(response, JSON.toJSONString(R.error("账号已禁用！")));
        } else {
            WebUtils.renderString(response, JSON.toJSONString(R.error("未知错误，请联系系统管理员！")));
        }
    }
}
