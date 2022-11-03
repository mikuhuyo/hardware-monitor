package com.example.backend.service.chain;

import com.example.chain.dto.AlarmDTO;
import com.example.chain.query.AlarmQuery;
import com.example.chain.vo.AlarmVO;
import com.example.common.domain.RestPageResult;
import com.example.common.exception.BusinessException;

import java.util.List;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
public interface IAlarmService {

    /**
     * 分页条件查询报警规则
     *
     * @param alarmQuery com.example.chain.query.AlarmQuery
     * @return <code>com.example.common.domain.RestPageResult< List< AlarmDTO>></code>
     * @throws BusinessException 业务异常
     */
    RestPageResult<List<AlarmDTO>> searchAlarm(AlarmQuery alarmQuery) throws BusinessException;

    /**
     * 根据id查询告警规则
     *
     * @param id java.lang.Long
     * @return com.example.chain.dto.AlarmDTO
     * @throws BusinessException 业务异常
     */
    AlarmDTO findAlarmById(Long id) throws BusinessException;

    /**
     * 更新告警规则
     *
     * @param alarmVO com.example.chain.vo.AlarmVO
     * @throws BusinessException 业务异常
     */
    void updateAlarmById(AlarmVO alarmVO) throws BusinessException;

    /**
     * 移除告警规则
     *
     * @param id java.lang.Long
     * @throws BusinessException 业务异常
     */
    void removeAlarmById(Long id) throws BusinessException;

    /**
     * 创建告警规则
     *
     * @param alarmVO com.example.chain.vo.AlarmVO
     * @throws BusinessException 业务异常
     */
    void createAlarm(AlarmVO alarmVO) throws BusinessException;
}
