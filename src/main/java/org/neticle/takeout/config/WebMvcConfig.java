package org.neticle.takeout.config;

import lombok.extern.slf4j.Slf4j;
import org.neticle.takeout.common.JacksonObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author Faruku123
 * @version 1.0
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("开始进行静态资源映射...");
        registry.addResourceHandler("/backend/**").addResourceLocations("classpath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("classpath:/front/");
    }

    /**
     * 扩展MVC框架的消息转换器
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");
        //创建消息转换器对象，可以将响应到浏览器的Java对象转换为Json格式的字符串
        MappingJackson2HttpMessageConverter messageConverter
                = new MappingJackson2HttpMessageConverter();
        //将对象转换器变更为我们自定义的JacksonObjectMapper对象
        messageConverter.setObjectMapper(new JacksonObjectMapper());
        //将上面的消息转换器对象追加到MVC框架的转换器集合（List集合）中，插入到索引0的位置最先调用
        converters.add(0, messageConverter);
    }
}
