package org.neticle.takeout;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * @author Faruku123
 * @version 1.0
 */
@Slf4j
@SpringBootApplication
@ServletComponentScan
public class TakeoutApp {
    public static void main(String[] args) {
        SpringApplication.run(TakeoutApp.class, args);
        log.info("项目启动成功...");
    }
}
