package com.example.backend.emq.config;

import com.example.backend.emq.client.EmqClient;
import com.example.backend.service.chain.IGpsService;
import com.example.backend.service.chain.IQuotaService;
import com.example.chain.dto.GpsDTO;
import com.example.chain.pojo.Quota;
import com.example.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Slf4j
@Component
public class EmqMonitoringConfig {
    @Autowired
    private EmqClient emqClient;
    @Autowired
    private IQuotaService quotaService;
    @Autowired
    private IGpsService gpsService;

    @PostConstruct
    public void subscribe() {
        emqClient.connect();

        List<Quota> all = quotaService.findAll();
        if (all.size() == 0) {
            log.info("com.example.backend.emq.config.EmqMonitoringConfig#subscribe() 指标信息为空, 主题订阅失败");
        }

        all.forEach(s -> {
            try {
                emqClient.subscribe("$queue/" + s);
                log.info("com.example.backend.emq.config.EmqMonitoringConfig#subscribe() 指标信息主题[{}]订阅成功", s.getSubject());

            } catch (Exception e) {
                throw new BusinessException("指标主题订阅失败 " + s.getSubject());
            }
        });

        GpsDTO gps = gpsService.findGps();
        if (gps == null) {
            log.info("com.example.backend.emq.config.EmqMonitoringConfig#subscribe() 地理信息数据为空, 主题订阅失败");
        }

        try {
            assert gps != null;
            emqClient.subscribe(gps.getSubject());
            log.info("com.example.backend.emq.config.EmqMonitoringConfig#subscribe() 地理信息主题[{}]订阅成功", gps.getSubject());
        } catch (Exception e) {
            throw new BusinessException("地理信息主题订阅失败 " + gps.getSubject());
        }

    }
}
