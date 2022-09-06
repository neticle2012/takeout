package org.neticle.takeout.security.handler.front;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.common.R;
import org.neticle.takeout.security.userdetail.backend.EmployeeDetailImpl;
import org.neticle.takeout.security.userdetail.front.UserDetailImpl;
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
 * 自定义前台注销登录成功处理
 */
@Component
@Slf4j
public class FrontLogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    @Autowired
    private RedisCache redisCache;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication)
            throws IOException, ServletException {
        UserDetailImpl userDetail = null;
        if (authentication != null) {
            userDetail = (UserDetailImpl) authentication.getPrincipal();
            redisCache.deleteObject("login:front:" + userDetail.getUser().getId());
            new SecurityContextLogoutHandler().logout(request, response, authentication);
            log.info("用户 {} 退出登录成功", userDetail.getUser().getPhone());
            WebUtils.renderString(response, JSON.toJSONString(R.success("退出成功")));
        } else {
            log.error("authentication = null");
            throw new NullPointerException("authentication = null");
        }
    }
}
