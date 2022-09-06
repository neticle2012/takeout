package org.neticle.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.neticle.takeout.common.R;
import org.neticle.takeout.mapper.UserMapper;
import org.neticle.takeout.pojo.User;
import org.neticle.takeout.service.UserService;
import org.neticle.takeout.utils.RedisCache;
import org.neticle.takeout.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    private RedisCache redisCache;


    @Override
    public R<String> sendMsg(User user) {
        String mail = user.getPhone();//获取邮箱账号
        log.info("user: {}", user.toString());
        String subject = "奥利给皇家餐厅登录验证码";
        if (StringUtils.isNotEmpty(mail)) {
            String code = ValidateCodeUtils.generateValidateCode(4).toString();//生成随机的4位验证码
            log.info("code = {}", code);
//            String context = "欢迎使用奥利给皇家餐厅，您的登录验证码是 "+ code +"，有效期为5分钟，请尽快验证。";
//            SimpleMailMessage message = new SimpleMailMessage();
//            message.setFrom(from);
//            message.setTo(mail);
//            message.setSubject(subject);
//            message.setText(context);
//            javaMailSender.send(message);
            //将生成的验证码缓存到Redis中，并且设置过期时间 = 5min
            redisCache.setCacheObject(mail, code, 5, TimeUnit.MINUTES);
            return R.success("验证码发送成功，请及时查看");
        }
        return R.error("验证码发送失败，请重新输入邮箱");
    }
}
