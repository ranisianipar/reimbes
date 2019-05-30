package com.reimbes.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
public class WebConfig extends WebMvcConfigurerAdapter {
    private static Logger log = LoggerFactory.getLogger(WebConfig.class);

    // Instead using @Autowired (will make LoggerInterceptor null)
    @Bean
    public LoggerInterceptor loggerInterceptor() {
        return new LoggerInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("Interceptor just added");
        registry.addInterceptor(loggerInterceptor());
    }
}
