package org.neticle.takeout.security.handler.backend;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.common.R;
import org.neticle.takeout.pojo.EmpInfo;
import org.neticle.takeout.security.userdetail.backend.EmployeeDetailImpl;
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
 * 自定义后台登录成功处理
 */
@Component
@Slf4j
public class BackendAuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {
    @Autowired
    RedisCache redisCache;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {
        EmployeeDetailImpl empDetail = (EmployeeDetailImpl) authentication.getPrincipal();
        //1. 自己生成jwt，放入响应的header中返给前端
        String empId = empDetail.getEmployee().getId() + "";
        String empUsername = empDetail.getEmployee().getUsername();//获取用户名（登录时输入的）
        String empName = empDetail.getEmployee().getName();//获取用户姓名（昵称）
        String jwt = JwtUtil.createJWT(empId);
        //默认情况下，只有七种simple response headers可以暴露给外部：Cache-Control、Content-Language
        //Content-Length、Content-Type、Expires、Last-Modified、Pragma
        response.setHeader("Access-Control-Expose-Headers", "authorization");
        response.setHeader("authorization", jwt);
        log.info("{} 登录成功[后台]，返回jwt给前端", empUsername);
        //2. 员工相关所有信息放入redis
        redisCache.setCacheObject("login:backend:"+ empId, empDetail);
        EmpInfo empInfo = new EmpInfo(empUsername, empName);
        WebUtils.renderString(response, JSON.toJSONString(R.success(empInfo)));
    }
}
