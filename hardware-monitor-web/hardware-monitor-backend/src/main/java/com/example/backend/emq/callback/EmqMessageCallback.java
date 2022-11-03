package com.example.backend.emq.callback;

import com.example.backend.emq.client.EmqClient;
import com.example.backend.service.chain.IAlarmLogService;
import com.example.backend.service.chain.IDeviceGeoService;
import com.example.backend.service.chain.IDevicesService;
import com.example.backend.service.chain.IQuotaService;
import com.example.chain.dto.QuotaDTO;
import com.example.chain.pojo.DeviceGeo;
import com.example.common.exception.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Slf4j
@Component
public class EmqMessageCallback implements MqttCallback {
    @Autowired
    private EmqClient emqClient;

    @Autowired
    private IQuotaService quotaService;
    @Autowired
    private IDevicesService devicesService;
    @Autowired
    private IAlarmLogService alarmLogService;
    @Autowired
    private IDeviceGeoService deviceGeoService;

    @Override
    public void connectionLost(Throwable throwable) {
        log.warn("com.example.backend.emq.callback.EmqMessageCallback#connectionLost() emq服务器连接丢失, 重试订阅主题");

        quotaService.findAll().forEach(quota -> {
            try {
                emqClient.subscribe(quota.getSubject());
                log.info("com.example.backend.emq.callback.EmqMessageCallback#connectionLost() 消息主题订阅成功 {}", quota.getSubject());

            } catch (Exception e) {
                throw new BusinessException("消息主题订阅失败 " + quota.getSubject());
            }
        });
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws IOException {
        String message = new String(mqttMessage.getPayload());

        log.info("com.example.backend.emq.callback.EmqMessageCallback#messageArrived() 接收订阅主题: {}", s);
        log.info("com.example.backend.emq.callback.EmqMessageCallback#messageArrived() 接收订阅消息: {}", message);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> payloadMap = mapper.readValue(message, Map.class);

        // 解析解析报文数据
        QuotaDTO quotaDTO = devicesService.analysis(s, payloadMap);
        if (quotaDTO != null) {
            // 保存告警日志并且更新设备信息
            alarmLogService.createAlarmLog(quotaDTO);
        }

        // 解析地理信息报文
        DeviceGeo deviceGeo = deviceGeoService.analysis(s, payloadMap);
        if (deviceGeo != null) {
            // 保存地理信息报文到ElasticSearch中
            deviceGeoService.createDeviceGeo(deviceGeo);
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
