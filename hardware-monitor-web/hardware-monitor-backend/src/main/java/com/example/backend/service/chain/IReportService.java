package com.example.backend.service.chain;

import com.example.chain.dto.*;
import com.example.common.exception.BusinessException;

import java.util.List;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
public interface IReportService {

    /**
     * 根据指标id获取设备列表
     *
     * @param quotaId 指标id
     * @return <code>java.util.List< java.lang.String > </code>
     */
    List<String> getDeviceNameByQuotaId(Long quotaId);

    /**
     * 获取实时告警日志
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return <code>java.util.List< com.example.chain.dto.AlarmLogDTO ></code>
     */
    List<AlarmLogDTO> getRealTimeAlarmLog(Long start, Long end);

    /**
     * 获取异常率top10的设备
     *
     * @param start 开始时间
     * @param end   结束时间
     * @param type  类型 1-小时 2-天 3-周
     * @return <code>java.util.List< com.example.chain.dto.DeviceHeapDTO ></code>
     */
    List<DeviceHeapDTO> getDeviceAbnormalTop10(String start, String end, String type);

    /**
     * todo 获取异常趋势数据
     *
     * @param start 开始时间
     * @param end   结束时间
     * @param type  类型 1-小时 2-天 3-周
     * @return <code>java.util.List< com.example.chain.dto.DeviceTrendDTO ></code>
     */
    List<DeviceTrendDTO> getDeviceAbnormalTrendList(String start, String end, String type);

    /**
     * 实时设备监控
     *
     * @return com.example.chain.dto.DeviceMonitorDTO
     * @throws BusinessException 业务异常
     */
    DeviceMonitorDTO getDeviceMonitor() throws BusinessException;

    /**
     * 获取状态分布
     *
     * @return <code>java.util.List< com.example.chain.dto.PieDTO ></code>
     * @throws BusinessException 业务异常
     */
    List<DevicePieDTO> getStatusCollect() throws BusinessException;
}
