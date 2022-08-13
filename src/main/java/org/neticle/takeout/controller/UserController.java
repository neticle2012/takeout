package org.neticle.takeout.controller;

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
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 发送邮箱验证码
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        return userService.sendMsg(user, session);
    }

    /**
     * 移动端用户登录
     */
    @PostMapping("/login")
    public R<String> login(@RequestBody Map<String,String> map, HttpSession session){
        return userService.login(map, session);
    }

    /**
     * 移动端用户退出登录
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpServletRequest request) {
        return userService.logout(request);
    }
}
