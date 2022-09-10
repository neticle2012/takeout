package org.neticle.takeout;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author Faruku123
 * @version 1.0
 */
public class PasswordTest {
   @Test
   public void encryptPwdAndCheck() {
       String pwd = "123456";
       BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
       String pwdEnc = encoder.encode(pwd);
       System.out.println("pwdEnc = "+ pwdEnc);
       System.out.println(encoder.matches(pwd, pwdEnc));
       System.out.println(encoder.matches("12345", pwdEnc));
   }
}
