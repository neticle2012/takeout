package org.neticle.takeout.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.neticle.takeout.common.R;
import org.neticle.takeout.pojo.User;
import org.neticle.takeout.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author Faruku123
 * @version 1.0
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户相关接口")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送邮箱验证码
     */
    @ApiOperation(value = "发送邮箱验证码接口")
    @ApiImplicitParam(name = "user", value = "用户", required = true)
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user){
        return userService.sendMsg(user);
    }

    /**
     * 移动端用户登录
     */
    @Deprecated
    @ApiOperation(value = "用户登录接口")
    @ApiImplicitParam(name = "map", value = "用户输入邮箱和验证码的Map集合", required = true)
//    @PostMapping("/login")
    public R<String> login(@RequestBody Map<String,String> map, HttpSession session){
        return userService.login(map, session);
    }

    /**
     * 移动端用户退出登录
     */
    @Deprecated
    @ApiOperation(value = "用户退出登录接口")
//    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request) {
        return userService.logout(request);
    }
}
