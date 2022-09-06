package org.neticle.takeout.security.userdetail.front;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.neticle.takeout.pojo.User;
import org.neticle.takeout.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author Faruku123
 * @version 1.0
 * UserDetailService对于用户的实现子类，用于从数据库中查询用户信息
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据邮箱从数据库中查询用户信息，如果没有信息则注册新用户
        String mail = username;
        //SELECT * FROM user WHERE phone = mail
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getPhone, mail);
        User user = null;
        //判断当前邮箱对应的用户是否为新用户，如果是新用户就自动完成注册
        if ((user = userService.getOne(lqw)) == null) {
            user = new User();
            user.setPhone(mail);
            user.setStatus(1);
            userService.save(user);
        }
        //返回UserDetails的实现子类
        return new UserDetailImpl(user);
    }
}
