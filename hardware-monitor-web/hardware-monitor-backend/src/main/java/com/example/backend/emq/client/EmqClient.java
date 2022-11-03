package com.example.backend.emq.client;

import com.example.backend.emq.callback.EmqMessageCallback;
import com.example.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Slf4j
@Component
public class EmqClient {
    @Value("${emq.uri}")
    private String emqUri;

    @Autowired
    private EmqMessageCallback emqMessageCallback;

    private MqttClient mqttClient;

    public void connect() {
        try {
            mqttClient = new MqttClient(emqUri, "monitor-" + UUID.randomUUID().toString().replace("-", ""));
            mqttClient.setCallback(emqMessageCallback);
            mqttClient.connect();
        } catch (MqttException e) {
            throw new BusinessException("mqtt消息服务器连接失败");
        }
    }

    public void publish(String topic, String msg) {
        try {
            MqttMessage mqttMessage = new MqttMessage(msg.getBytes());
            // 向某主题发送消息
            mqttClient.getTopic(topic).publish(mqttMessage);
        } catch (MqttException e) {
            throw new BusinessException("mqtt消息发送失败");
        }
    }

    public void subscribe(String topic) {
        try {
            mqttClient.subscribe(topic);
        } catch (MqttException e) {
            throw new BusinessException("mqtt消息订阅失败");
        }
    }
}
