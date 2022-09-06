package org.neticle.takeout.security.provider.backend;

import lombok.Data;
import org.neticle.takeout.security.authentication.backend.BackendUsernamePasswordAuthenticationToken;
import org.neticle.takeout.security.userdetail.backend.EmployeeDetailServiceImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author Faruku123
 * @version 1.0
 * 处理后台账号密码登录的Provider
 */
public class BackendDaoAuthentationProvider extends DaoAuthenticationProvider {
    public BackendDaoAuthentationProvider(EmployeeDetailServiceImpl userDetailsService) {
        super();
        //注意：一定要在这里对userDetailsService赋值，不然父类会因为其为null而报错！！！
        setUserDetailsService(userDetailsService);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return BackendUsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
