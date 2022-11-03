package com.example.backend.service.chain;

import com.example.chain.dto.AlarmLogDTO;
import com.example.chain.dto.QuotaDTO;
import com.example.chain.query.AlarmLogQuery;
import com.example.common.domain.RestPageResult;
import com.example.common.exception.BusinessException;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
public interface IAlarmLogService {
    /**
     * 根据设备id获取最新一条告警日志
     *
     * @param deviceId java.lang.String
     * @return com.example.chain.dto.AlarmLogDTO
     * @throws BusinessException 业务异常
     */
    AlarmLogDTO findAlarmLogByTimeDesc(String deviceId) throws BusinessException, IOException, ParseException;

    /**
     * 分页条件查询告警日志
     *
     * @param alarmLogQuery
     * @return
     * @throws BusinessException
     */
    RestPageResult<List<AlarmLogDTO>> searchAlarmLog(AlarmLogQuery alarmLogQuery) throws BusinessException, ParseException, IOException;

    /**
     * 添加报警日志
     *
     * @param quotaDTO com.example.chain.dto.QuotaDTO
     * @throws BusinessException 业务异常
     */
    void createAlarmLog(QuotaDTO quotaDTO) throws BusinessException;
}
