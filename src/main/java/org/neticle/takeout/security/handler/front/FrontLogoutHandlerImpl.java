package org.neticle.takeout.security.handler.front;

import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.security.userdetail.backend.EmployeeDetailImpl;
import org.neticle.takeout.security.userdetail.front.UserDetailImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Faruku123
 * @version 1.0
 * 自定义前台注销登录处理
 */
@Slf4j
@Component
public class FrontLogoutHandlerImpl implements LogoutHandler {
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) {
        UserDetailImpl userDetail = null;
        if (authentication != null) {
            userDetail = (UserDetailImpl) authentication.getPrincipal();
            log.info("用户 {} 退出登录", userDetail.getUser().getPhone());
        } else {
            log.error("authentication = null");
            throw new NullPointerException("authentication = null");
        }
    }
}
