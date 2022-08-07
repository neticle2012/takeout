package org.neticle.takeout.common;

/**
 * @author Faruku123
 * @version 1.0
 * 基于ThreadLocal封装的工具类，用于保存和获取当前登录用户的id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置值
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取值
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
