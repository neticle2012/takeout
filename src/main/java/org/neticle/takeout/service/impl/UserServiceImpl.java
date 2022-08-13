package org.neticle.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.neticle.takeout.common.R;
import org.neticle.takeout.mapper.UserMapper;
import org.neticle.takeout.pojo.User;
import org.neticle.takeout.service.UserService;
import org.neticle.takeout.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author Faruku123
 * @version 1.0
 */
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    @Value("${spring.mail.username}")
    private String from; //发件人
    @Autowired
    private JavaMailSender javaMailSender;


    @Override
    public R<String> sendMsg(User user, HttpSession session) {
        String mail = user.getPhone();//获取邮箱账号
        log.info("user: {}", user.toString());
        String subject = "奥利给皇家餐厅登录验证码";
        if (StringUtils.isNotEmpty(mail)) {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();//生成随机的4位验证码
            log.info("code = {}", code);
            String context = "欢迎使用奥利给皇家餐厅，您的登录验证码是 "+ code +"，有效期为5分钟，请尽快验证。";
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(mail);
            message.setSubject(subject);
            message.setText(context);
            javaMailSender.send(message);
            session.setAttribute(mail, code);//将生成的验证码保存到session
            return R.success("验证码发送成功，请及时查看");
        }
        return R.error("验证码发送失败，请重新输入邮箱");
    }

    @Override
    public R<String> login(Map<String, String> map, HttpSession session) {
        log.info("map: {}", map.toString());
        String mail = map.get("phone");//获取邮箱
        String inputCode = map.get("code");//获取验证码
        String sessionCode = (String) session.getAttribute(mail);//从session中获取保存的验证码
        //进行验证码的比对（页面提交的验证码和session中保存的验证码比对）
        if (!(sessionCode != null && sessionCode.equals(inputCode))) {
            return R.error("登录失败，请重新登录！");
        }
        //如果能比对成功，说明登录成功
        //SELECT * FROM user WHERE phone = mail
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getPhone, mail);
        User user = null;
        //判断当前邮箱对应的用户是否为新用户，如果是新用户就自动完成注册
        if ((user = this.getOne(lqw)) == null) {
            user = new User();
            user.setPhone(mail);
            user.setStatus(1);
            this.save(user);
        }
        session.setAttribute("user", user.getId());
        return R.success("登录成功");
    }

    @Override
    public R<String> logout(HttpServletRequest request) {
        //清理Session中保存的当前登录用户的id
        request.getSession().removeAttribute("user");
        return R.success("退出成功");
    }
}
