package org.neticle.takeout.security.handler.front;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.common.R;
import org.neticle.takeout.pojo.EmpInfo;
import org.neticle.takeout.security.userdetail.backend.EmployeeDetailImpl;
import org.neticle.takeout.security.userdetail.front.UserDetailImpl;
import org.neticle.takeout.service.UserService;
import org.neticle.takeout.utils.JwtUtil;
import org.neticle.takeout.utils.RedisCache;
import org.neticle.takeout.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Faruku123
 * @version 1.0
 * 自定义前台登录成功处理
 */
@Component
@Slf4j
public class FrontAuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {
    @Autowired
    UserService userService;
    @Autowired
    RedisCache redisCache;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        UserDetailImpl userDetail = (UserDetailImpl) authentication.getPrincipal();
        //1. 用户登录成功后，删除掉redis中缓存的验证码
        String mail = userDetail.getUser().getPhone();//获取邮箱名
        redisCache.deleteObject(mail);
        //2. 自己生成jwt，放入响应的header中返给前端
        String userId = userDetail.getUser().getId() + "";
        String jwt = JwtUtil.createJWT(userId);
        //默认情况下，只有七种simple response headers可以暴露给外部：Cache-Control、Content-Language
        //Content-Length、Content-Type、Expires、Last-Modified、Pragma
        response.setHeader("Access-Control-Expose-Headers", "authorization");
        response.setHeader("authorization", jwt);
        log.info("{} 登录成功[前台]，返回jwt给前端", mail);
        //2. 用户相关所有信息放入redis
        redisCache.setCacheObject("login:front:"+ userId, userDetail);
        WebUtils.renderString(response, JSON.toJSONString(R.success("登录成功")));
    }
}
