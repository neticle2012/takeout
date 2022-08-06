package org.neticle.takeout.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author Faruku123
 * @version 1.0
 * 全局异常处理
 */
//对标了@RestController和@Controller注解的@Controller进行处理
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 处理SQLIntegrityConstraintViolationException异常的方法
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        String message = ex.getMessage();
        log.error(message);
        if (message.contains("Duplicate entry")){//唯一约束字段重复了
            String[] split = message.split(" ");
            return R.error(split[2] + "已存在");
        }
        return R.error("未知错误");
    }
}
