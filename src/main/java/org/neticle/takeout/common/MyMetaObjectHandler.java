package org.neticle.takeout.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author Faruku123
 * @version 1.0
 * 自定义的元数据对象处理器
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     * 插入操作，自动填充
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]...");
        log.info(metaObject.toString());
        if (metaObject.hasSetter("createTime")) metaObject.setValue("createTime", LocalDateTime.now());
        if (metaObject.hasSetter("updateTime")) metaObject.setValue("updateTime", LocalDateTime.now());
        if (metaObject.hasSetter("createUser")) metaObject.setValue("createUser", BaseContext.getCurrentId());
        if (metaObject.hasSetter("updateUser")) metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }

    /**
     * 更新操作，自动填充
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]...");
        log.info(metaObject.toString());
        log.info("线程id = {}", Thread.currentThread().getId());
        if (metaObject.hasSetter("updateTime")) metaObject.setValue("updateTime", LocalDateTime.now());
        if (metaObject.hasSetter("updateUser")) metaObject.setValue("updateUser", BaseContext.getCurrentId());
    }
}
