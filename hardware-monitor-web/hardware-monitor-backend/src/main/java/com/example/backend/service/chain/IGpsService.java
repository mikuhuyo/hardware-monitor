package com.example.backend.service.chain;

import com.example.chain.dto.GpsDTO;
import com.example.chain.vo.GpsVO;
import com.example.common.exception.BusinessException;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
public interface IGpsService {
    /**
     * 获取gps位置信息
     *
     * @return com.example.chain.dto.GpsDTO
     * @throws BusinessException 业务异常
     */
    GpsDTO findGps() throws BusinessException;

    /**
     * 更新gps位置信息
     *
     * @param gpsVO
     * @throws BusinessException
     */
    void updateGpsById(GpsVO gpsVO) throws BusinessException;

    /**
     * 移除gps位置信信息
     *
     * @param id java.lang.Long
     * @throws BusinessException 业务异常
     */
    void removeGpsById(Long id) throws BusinessException;

    /**
     * 新增gps位置信息
     * gps位置信息有一点特殊, 实际上只是需要配置一个gps基本信息
     *
     * @param gpsVO com.example.chain.vo.GpsVO
     * @throws BusinessException 业务异常
     */
    void creatGps(GpsVO gpsVO) throws BusinessException;
}
