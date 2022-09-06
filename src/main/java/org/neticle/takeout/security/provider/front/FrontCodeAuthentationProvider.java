package org.neticle.takeout.security.provider.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.common.R;
import org.neticle.takeout.pojo.User;
import org.neticle.takeout.security.authentication.backend.BackendUsernamePasswordAuthenticationToken;
import org.neticle.takeout.security.authentication.front.FrontUsernamePasswordAuthenticationToken;
import org.neticle.takeout.security.exception.BadCodeException;
import org.neticle.takeout.security.exception.CodeExpireException;
import org.neticle.takeout.security.userdetail.front.UserDetailServiceImpl;
import org.neticle.takeout.service.UserService;
import org.neticle.takeout.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * @author Faruku123
 * @version 1.0
 * 处理前台邮箱验证码登录的Provider
 */
@Slf4j
@Data
public class FrontCodeAuthentationProvider implements AuthenticationProvider {
    @Autowired
    private RedisCache redisCache;

    private UserDetailsService userDetailsService;
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //首先，从redis中获取用户对应的验证码，与用户输入的验证码进行匹配
        String mail = (String) authentication.getPrincipal();
        String inputCode = (String) authentication.getCredentials();
        String redisCode = redisCache.getCacheObject(mail);
        log.info("用户输入的验证码 = {}, redis中的验证码 = {}", inputCode, redisCode);
        if (redisCode == null) {
            throw new CodeExpireException("验证码已过期");
        }
        if (!redisCode.equals(inputCode)) {
            throw new BadCodeException("验证码输入有误");
        }
        //读取UserDetails接口对象
        UserDetails loadedUser = userDetailsService.loadUserByUsername(mail);
        //接下来，进行各种校验（账号锁定、账号禁用、账号过期、密码校验、密码过期）
        if (!loadedUser.isEnabled()) {
            log.warn("{} 被禁用了", mail);
            throw new DisabledException("账号已被禁用");
        }
        FrontUsernamePasswordAuthenticationToken result
                = new FrontUsernamePasswordAuthenticationToken(loadedUser, inputCode,
                this.authoritiesMapper.mapAuthorities(loadedUser.getAuthorities()));
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return FrontUsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
