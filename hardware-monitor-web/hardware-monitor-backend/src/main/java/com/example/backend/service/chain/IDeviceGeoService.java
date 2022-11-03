package com.example.backend.service.chain;

import com.example.chain.dto.DeviceGeoDetails;
import com.example.chain.pojo.DeviceGeo;
import com.example.common.exception.BusinessException;

import java.util.List;
import java.util.Map;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
public interface IDeviceGeoService {
    /**
     * 查询指定范围内的设备详情
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @param distance  范围
     * @return 设备详情
     * @throws BusinessException 业务异常
     */
    List<DeviceGeoDetails> searchDeviceGeoLocationDetails(Double longitude, Double latitude, String distance) throws BusinessException;

    /**
     * 搜索指定范围内的设备列表
     *
     * @param longitude 经度
     * @param latitude  纬度
     * @param distance  指定范围
     * @return 设备列表
     * @throws BusinessException 业务异常
     */
    List<DeviceGeo> searchDeviceGeoLocation(Double longitude, Double latitude, String distance) throws BusinessException;

    /**
     * 保存地理信息到ElasticSearch
     *
     * @param deviceGeo com.example.chain.pojo.DeviceGeo 地理信息实体类
     * @throws BusinessException 业务异常
     */
    void createDeviceGeo(DeviceGeo deviceGeo) throws BusinessException;

    /**
     * 设备地理信息报文解析
     *
     * @param topic   设备地理信息主题
     * @param payload 设备地理信息报文
     * @return com.example.chain.pojo.DeviceGeo 地理信息实体类
     * @throws BusinessException 业务异常
     */
    DeviceGeo analysis(String topic, Map<String, Object> payload) throws BusinessException;
}
