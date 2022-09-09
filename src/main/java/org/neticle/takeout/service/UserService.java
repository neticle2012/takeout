package org.neticle.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.neticle.takeout.common.R;
import org.neticle.takeout.pojo.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author Faruku123
 * @version 1.0
 */
public interface UserService extends IService<User> {
    R<String> sendMsg(User user);
    @Deprecated
    R<String> login(Map<String,String> map, HttpSession session);
    @Deprecated
    R<String> logout(HttpServletRequest request);
}
