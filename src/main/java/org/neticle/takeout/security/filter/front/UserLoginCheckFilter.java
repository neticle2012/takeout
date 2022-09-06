package org.neticle.takeout.security.filter.front;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.security.authentication.front.FrontUsernamePasswordAuthenticationToken;
import org.neticle.takeout.utils.GetRequestJsonUtil;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Faruku123
 * @version 1.0
 * 前台用户登录验证Filter
 */
@Slf4j
public class UserLoginCheckFilter extends UsernamePasswordAuthenticationFilter {
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
			throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
		}
        JSONObject requestJson = GetRequestJsonUtil.getPostRequestJsonString(request);
        String username = requestJson.getString(getUsernameParameter());
		username = (username != null) ? username.trim() : "";
		String password = requestJson.getString(getPasswordParameter());
		password = (password != null) ? password : "";
		log.info("前台用户尝试登录，username = {}, password = {}", username, password);
		FrontUsernamePasswordAuthenticationToken authRequest
				= new FrontUsernamePasswordAuthenticationToken(username, password);
		this.setDetails(request, authRequest);
		return this.getAuthenticationManager().authenticate(authRequest);
    }
}
