package org.neticle.takeout.security.provider;

import org.neticle.takeout.security.authentication.BackendUsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

/**
 * @author Faruku123
 * @version 1.0
 * 处理后台账号密码登录的Provider
 */
public class BackendDaoAuthentationProvider extends DaoAuthenticationProvider {
    @Override
    public boolean supports(Class<?> authentication) {
        return BackendUsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
