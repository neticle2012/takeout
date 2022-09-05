package org.neticle.takeout.security.filter;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.security.authentication.BackendUsernamePasswordAuthenticationToken;
import org.neticle.takeout.utils.GetRequestJsonUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Faruku123
 * @version 1.0
 * 后台员工登录验证Filter
 */
@Slf4j
public class EmpLoginCheckFilter extends UsernamePasswordAuthenticationFilter {
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response)
            throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            //注意！！！post请求提交数据的方式设置为application/json时，通过request.getParameter
            //无法获取到请求参数，需要以流的形式读取json字符串，然后使用fastjson进行解析
            JSONObject requestJson = GetRequestJsonUtil.getPostRequestJsonString(request);
            String username = requestJson.getString(getUsernameParameter());
            username = username != null ? username.trim() : "";
            String password = requestJson.getString(getPasswordParameter());
            password = password != null ? password : "";
            log.info("后台员工尝试登录，username = {}, password = {}", username, password);
            //TODO 对于后台的多方式登录（例如手机验证码、邮箱等），可以使用switch-case语句
            // 根据request的登录标志生成不同的 Authentication 接口对象，
            // 从而可以让providers集合中不同的provider来处理（当然，必须要在provider中
            // 重写supports方法来声明其支持的 Authentication 接口对象）
            BackendUsernamePasswordAuthenticationToken authRequest
                    = new BackendUsernamePasswordAuthenticationToken(username, password);
            this.setDetails(request, authRequest);
            return this.getAuthenticationManager().authenticate(authRequest);
        }
    }
}
