package org.neticle.takeout.security.handler;

import com.alibaba.fastjson.JSON;
import org.neticle.takeout.common.R;
import org.neticle.takeout.security.userdetail.EmployeeDetail;
import org.neticle.takeout.utils.RedisCache;
import org.neticle.takeout.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Faruku123
 * @version 1.0
 * 自定义后台注销登录成功处理
 */
@Component
public class BackendLogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication)
            throws IOException, ServletException {
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        WebUtils.renderString(response, JSON.toJSONString(R.success("退出成功")));
    }
}
