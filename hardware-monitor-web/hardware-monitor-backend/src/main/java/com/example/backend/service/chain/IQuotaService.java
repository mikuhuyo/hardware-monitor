package com.example.backend.service.chain;

import com.example.chain.dto.QuotaDTO;
import com.example.chain.pojo.Quota;
import com.example.chain.query.QuotaQuery;
import com.example.chain.vo.QuotaVO;
import com.example.common.domain.RestPageResult;
import com.example.common.exception.BusinessException;

import java.util.List;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
public interface IQuotaService {
    /**
     * 根据设备id查询设备对应的指标列表
     *
     * @param deviceId 设备id
     * @return 指标列表
     * @throws BusinessException 业务异常
     */
    List<QuotaDTO> findQuotaListByDeviceId(String deviceId) throws BusinessException;

    /**
     * 获取全部数据类型指标
     *
     * @return <code>java.util.List< QuotaDTO></code>
     * @throws BusinessException 业务异常
     */
    List<QuotaDTO> findQuotaByNumber() throws BusinessException;

    /**
     * 分页查询数据类型指标
     *
     * @param quotaQuery <code>com.example.chain.query.QuotaQuery</code>
     * @return <code>com.example.common.domain.RestPageResult< List< QuotaDTO>></code>
     * @throws BusinessException 业务异常
     */
    RestPageResult<List<QuotaDTO>> searchQuotaByNumber(QuotaQuery quotaQuery) throws BusinessException;

    /**
     * 分页查询
     *
     * @param quotaQuery <code>com.example.chain.query.QuotaQuery</code>
     * @return <code>com.example.common.domain.RestPageResult< List< QuotaDTO>></code>
     * @throws BusinessException 业务异常
     */
    RestPageResult<List<QuotaDTO>> searchQuota(QuotaQuery quotaQuery) throws BusinessException;

    /**
     * 更新指标
     *
     * @param quotaVO com.example.chain.vo.QuotaVO
     * @return java.lang.boolean 更新指标是否成功 true成功 false失败
     * @throws BusinessException 业务异常
     */
    boolean updateQuotaById(QuotaVO quotaVO) throws BusinessException;

    /**
     * 移除指标
     *
     * @param id java.lang.Long 指标id
     * @return java.lang.boolean 移除指标是否成功 true成功 false失败
     * @throws BusinessException
     */
    boolean removeQuotaById(Long id) throws BusinessException;

    /**
     * 根据id查询指标
     *
     * @param id java.lang.Long 指标id
     * @return com.example.chain.dto.QuotaDTO
     * @throws BusinessException 业务异常
     */
    QuotaDTO findQuotaById(Long id) throws BusinessException;

    /**
     * 获取全部指标信息
     *
     * @return java.util.List<Quota>
     * @throws BusinessException 业务异常
     */
    List<Quota> findAll() throws BusinessException;

    /**
     * 新增指标
     *
     * @param quotaVO com.example.chain.vo.QuotaVO
     * @return java.lang.boolean 是否添加成功
     * @throws BusinessException 业务异常
     */
    boolean createQuota(QuotaVO quotaVO) throws BusinessException;
}
