package com.example.monitoring;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * <p>
 * <code>@EnableAdminServer</code> 开启管理端
 * <code>@ComponentScan(basePackages = {"com.example.monitoring.config"})</code> 扫描配置类
 * </p>
 *
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@EnableAdminServer
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        RedisAutoConfiguration.class,
        MongoAutoConfiguration.class
})
@ComponentScan(basePackages = {"com.example.monitoring.config"})
public class WebMonitoringApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebMonitoringApplication.class, args);
    }
}
