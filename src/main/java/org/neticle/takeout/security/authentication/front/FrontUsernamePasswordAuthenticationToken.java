package org.neticle.takeout.security.authentication.front;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author Faruku123
 * @version 1.0
 * 封装前台登录信息的Authentication对象
 */
public class FrontUsernamePasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {
    public FrontUsernamePasswordAuthenticationToken(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public FrontUsernamePasswordAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
