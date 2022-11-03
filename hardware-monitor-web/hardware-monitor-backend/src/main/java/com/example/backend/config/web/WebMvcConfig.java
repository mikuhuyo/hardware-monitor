package com.example.backend.config.web;

import com.example.backend.filter.RequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private RequestFilter requestFilter;

    /**
     * 添加拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestFilter)
                // 拦截所有请求
                .addPathPatterns("/**")
                // 放行资源
                .excludePathPatterns("/api/login", "/rsa-key/**")
                .excludePathPatterns("/applications/**", "/actuator/**", "/instance/**")
                .excludePathPatterns("/error/**")
                .excludePathPatterns("/static/**")
                .excludePathPatterns("/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**");
    }

    /**
     * 处理跨域问题
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 添加允许跨域的路径
        registry.addMapping("/**")
                // 允许跨域的域名
                .allowedOrigins("*")
                /// 是否允许证书, 此处不开启
                .allowCredentials(true)
                // 允许请求方法
                .allowedMethods("*")
                // 请求头
                .allowedHeaders("*")
                // 跨域允许时间
                .maxAge(3600);
    }

    /**
     * 类型转换 转换器
     *
     * @return
     */
    @Bean
    public MappingJackson2HttpMessageConverter jackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = new ObjectMapper();

        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);

        mapper.registerModule(simpleModule);

        converter.setObjectMapper(mapper);
        return converter;
    }

    /**
     * 添加转换器
     *
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(jackson2HttpMessageConverter());
    }
}
