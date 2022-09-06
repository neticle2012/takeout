package org.neticle.takeout.security.handler.front;

import com.alibaba.fastjson.JSON;
import org.neticle.takeout.common.R;
import org.neticle.takeout.security.exception.BadCodeException;
import org.neticle.takeout.security.exception.CodeExpireException;
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
 * 自定义前台登录失败处理
 */
@Component
public class FrontAuthenticationFailureHandlerImpl implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {
        //验证码过期时，抛出CodeExpireException
        //验证码错误时，抛出BadCodeException
        //用户被禁用时，抛出DisabledException，这三种异常都是AuthenticationException的子类
        //它们都是在调用自定义的FrontCodeAuthentationProvider类的authenticate方法
        // 去进行用户信息的验证时抛出来的
        if (exception instanceof CodeExpireException) {
            WebUtils.renderString(response, JSON.toJSONString(R.error("验证码已过期！")));
        } else if (exception instanceof BadCodeException) {
            WebUtils.renderString(response, JSON.toJSONString(R.error("验证码输入有误！")));
        } else if (exception instanceof DisabledException) {
            WebUtils.renderString(response, JSON.toJSONString(R.error("账号已禁用！")));
        } else {
            WebUtils.renderString(response, JSON.toJSONString(R.error("未知错误，请联系系统管理员！")));
        }
    }
}
