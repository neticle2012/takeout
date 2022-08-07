package org.neticle.takeout.common;

/**
 * @author Faruku123
 * @version 1.0
 * 自定义业务异常类
 */
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
