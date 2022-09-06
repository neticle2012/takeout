package org.neticle.takeout.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author Faruku123
 * @version 1.0
 */
public class CodeExpireException extends AuthenticationException {
    public CodeExpireException(String msg) {
        super(msg);
    }
}
