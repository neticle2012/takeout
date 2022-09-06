package org.neticle.takeout.security.handler.backend;

import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.security.userdetail.backend.EmployeeDetailImpl;
import org.neticle.takeout.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Faruku123
 * @version 1.0
 * 自定义后台注销登录处理
 */
@Slf4j
@Component
public class BackendLogoutHandlerImpl implements LogoutHandler {
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response,
                       Authentication authentication) {
        EmployeeDetailImpl empDetail = null;
        if (authentication != null) {
            empDetail = (EmployeeDetailImpl) authentication.getPrincipal();
            log.info("员工 {} 退出登录", empDetail.getEmployee().getUsername());
        } else {
            log.error("authentication = null");
            throw new NullPointerException("authentication = null");
        }
    }
}
