package org.neticle.takeout.redistmp;

import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author Faruku123
 * @version 1.0
 */
//@Slf4j
//@Component
//public class RedisMsTest implements ApplicationRunner {
//    @Autowired
//    private RedisCache redisCache;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        for (int i = 0; i < 100; i++) {
//            try {
//                redisCache.setCacheObject("k" + i, "v" + i);
//                log.info("set value success: [{}]", i);
//                String val = redisCache.getCacheObject("k" + i);
//                log.info("get value success: [{}]", val);
//                TimeUnit.SECONDS.sleep(1);
//            } catch (Exception e) {
//                //这里必须捕捉异常！！！如果直接抛出应用会停止
//                //主要是由于主机宕机而发生的CancellationException
//                log.error("错误: {}", e.getMessage());
//            }
//        }
//        log.info("finished...");
//    }
//}
