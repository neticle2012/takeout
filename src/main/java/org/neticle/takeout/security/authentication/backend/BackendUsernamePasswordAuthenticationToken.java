package org.neticle.takeout.security.authentication.backend;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author Faruku123
 * @version 1.0
 * 封装后台登录信息的Authentication对象
 */
public class BackendUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {
    public BackendUsernamePasswordAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public BackendUsernamePasswordAuthenticationToken(Object principal, Object credentials,
                                                      Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
