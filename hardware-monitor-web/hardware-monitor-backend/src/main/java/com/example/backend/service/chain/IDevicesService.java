package com.example.backend.service.chain;

import com.example.chain.dto.DeviceQuotaDTO;
import com.example.chain.dto.DevicesDTO;
import com.example.chain.dto.QuotaDTO;
import com.example.chain.query.DevicesQuery;
import com.example.chain.vo.DevicesVO;
import com.example.common.domain.RestPageResult;
import com.example.common.exception.BusinessException;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
public interface IDevicesService {
    /**
     * 获取在线正常设备总数量
     *
     * @return 在线正常设备总数
     * @throws BusinessException 业务异常
     */
    Long getOnlineDeviceCountByNormal() throws BusinessException;

    /**
     * 获取在线异常设备数量
     *
     * @return 在线异常设备总数
     * @throws BusinessException 业务异常
     */
    Long getOnlineDeviceCountByAlarm() throws BusinessException;

    /**
     * 获取在线设备总数
     *
     * @return 在线设备数量
     * @throws BusinessException 业务异常
     */
    Long getOnlineDeviceCount() throws BusinessException;

    /**
     * 获取离线设备总数
     *
     * @return 离线设备数量
     * @throws BusinessException 业务异常
     */
    Long getOfflineDeviceCount() throws BusinessException;

    /**
     * 获取设备总数
     *
     * @return 设备总数量
     * @throws BusinessException 业务异常
     */
    Long getAllDeviceCount() throws BusinessException;

    /**
     * 分页条件查询设备指标详情
     *
     * @param devicesQuery <code>com.example.chain.query.DevicesQuery</code>
     * @return <code>com.example.common.domain.RestPageResult< List< DeviceQuotaDTO>></code>
     * @throws BusinessException 业务异常
     * @throws IOException       jackson序列化异常
     */
    RestPageResult<List<DeviceQuotaDTO>> deviceQuotaDetails(DevicesQuery devicesQuery) throws BusinessException, IOException, ParseException;

    /**
     * 存储设备信息
     *
     * @param devicesDTO com.example.chain.dto.DevicesDTO
     * @return java.lang.boolean
     * @throws BusinessException 业务异常
     */
    boolean saveDeviceInfo(DevicesDTO devicesDTO) throws BusinessException;

    /**
     * 解析报文
     *
     * @param topic      主题
     * @param payloadMap 报文
     * @return com.example.chain.dto.QuotaDto
     * @throws BusinessException 业务异常
     */
    QuotaDTO analysis(String topic, Map<String, Object> payloadMap) throws BusinessException;

    /**
     * 分页查询
     *
     * @param devicesQueryVO com.example.chain.query.DevicesQueryVO
     * @return com.example.common.domain.RestPageResult 自定义分页结果集
     * @throws BusinessException 自定义异常
     * @throws IOException       jackson序列化异常
     */
    RestPageResult<List<DevicesDTO>> searchDevice(DevicesQuery devicesQueryVO) throws BusinessException, IOException;

    /**
     * 更新设备在线状态
     *
     * @param deviceId 设备id
     * @param online   在线状态
     * @return java.lang.boolean
     * @throws BusinessException 自定义业务异常
     */
    boolean updateDeviceOnlineById(String deviceId, Boolean online) throws BusinessException;

    /**
     * 更新设备告警信息
     *
     * @param devicesVO com.example.chain.vo.DevicesVO
     * @return java.lang.boolean
     * @throws BusinessException 自定义业务异常
     */
    boolean updateDeviceAlarmById(DevicesVO devicesVO) throws BusinessException;

    /**
     * 更新设备标签
     *
     * @param deviceId 设备id
     * @param tags     设备标签
     * @return java.lang.boolean
     * @throws BusinessException 自定义业务异常
     */
    boolean updateDeviceTagsById(String deviceId, String tags) throws BusinessException;

    /**
     * 根据id修改设备状态
     *
     * @param deviceId 设备id
     * @param status   设备状态
     * @return java.lang.boolean
     * @throws BusinessException 自定义业务异常
     */
    boolean updateDeviceStatusById(String deviceId, Boolean status) throws BusinessException;

    /**
     * 根据id查询设备信息
     *
     * @param deviceId 设备id
     * @return com.example.chain.dto.DevicesDTO
     * @throws BusinessException 自定义业务异常
     */
    DevicesDTO findDeviceById(String deviceId) throws BusinessException;

    /**
     * 添加设备
     *
     * @param devicesDTO com.example.chain.dto.DevicesDTO 设备信息
     * @throws BusinessException 自定义业务异常
     */
    void addDevices(DevicesDTO devicesDTO) throws BusinessException;
}
