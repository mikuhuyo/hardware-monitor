package com.example.backend.service.chain.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.emq.client.EmqClient;
import com.example.backend.mapper.chain.QuotaMapper;
import com.example.backend.service.chain.IQuotaService;
import com.example.chain.dto.QuotaDTO;
import com.example.chain.pojo.Quota;
import com.example.chain.query.QuotaQuery;
import com.example.chain.vo.QuotaVO;
import com.example.common.domain.RestPageResult;
import com.example.common.exception.BusinessException;
import com.mysql.cj.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Slf4j
@Service
public class IQuotaServiceImpl implements IQuotaService {
    @Autowired
    private QuotaMapper quotaMapper;
    @Autowired
    private EmqClient emqClient;

    @Override
    public List<QuotaDTO> findQuotaListByDeviceId(String deviceId) throws BusinessException {
        List<Quota> quotas = quotaMapper.selectList(new QueryWrapper<Quota>().lambda().eq(Quota::getSnKey, deviceId));

        List<QuotaDTO> items = new ArrayList<>();
        quotas.forEach(quota -> {
            QuotaDTO quotaDTO = new QuotaDTO();
            BeanUtils.copyProperties(quota, quotaDTO);

            items.add(quotaDTO);
        });

        return items;
    }

    @Override
    public List<QuotaDTO> findQuotaByNumber() throws BusinessException {
        List<Quota> quotas = quotaMapper.selectList(new QueryWrapper<Quota>().lambda().eq(Quota::getValueType, "Integer"));

        List<QuotaDTO> items = new ArrayList<>();
        quotas.forEach(quota -> {
            QuotaDTO quotaDTO = new QuotaDTO();
            BeanUtils.copyProperties(quota, quotaDTO);

            items.add(quotaDTO);
        });

        return items;
    }

    @Override
    public RestPageResult<List<QuotaDTO>> searchQuotaByNumber(QuotaQuery quotaQuery) throws BusinessException {
        LambdaQueryWrapper<Quota> lambda = new QueryWrapper<Quota>().lambda().eq(Quota::getValueType, "Integer");

        return pageLimit(quotaQuery.getPage(), quotaQuery.getPageSize(), lambda);
    }

    @Override
    public RestPageResult<List<QuotaDTO>> searchQuota(QuotaQuery quotaQuery) throws BusinessException {
        LambdaQueryWrapper<Quota> lambda = new QueryWrapper<Quota>().lambda();

        if (!StringUtils.isNullOrEmpty(quotaQuery.getQuotaName())) {
            lambda.like(Quota::getName, quotaQuery.getQuotaName());
        }

        return pageLimit(quotaQuery.getPage(), quotaQuery.getPageSize(), lambda);
    }

    private RestPageResult<List<QuotaDTO>> pageLimit(Long page, Long pageSize, LambdaQueryWrapper<Quota> queryWrapper) {
        IPage<Quota> pages = quotaMapper.selectPage(new Page<>(page, pageSize), queryWrapper);

        List<QuotaDTO> items = new ArrayList<>();
        pages.getRecords().forEach(quota -> {
            QuotaDTO quotaDTO = new QuotaDTO();
            BeanUtils.copyProperties(quota, quotaDTO);

            items.add(quotaDTO);
        });

        RestPageResult<List<QuotaDTO>> result = new RestPageResult<>();
        long total = pages.getTotal();
        Long totalPages = total % pageSize == 0 ? total / pageSize : total / pageSize + 1;

        result.setItems(items);
        result.setPage(page);
        result.setPageSize(pageSize);
        result.setCounts(total);
        result.setPages(totalPages);

        return result;
    }

    @Override
    public boolean updateQuotaById(QuotaVO quotaVO) throws BusinessException {
        Quota quota = new Quota();
        BeanUtils.copyProperties(quotaVO, quota);

        return quotaMapper.updateById(quota) == 1;
    }

    @Override
    public boolean removeQuotaById(Long id) throws BusinessException {
        return quotaMapper.deleteById(id) == 1;
    }

    @Override
    public QuotaDTO findQuotaById(Long id) throws BusinessException {
        Quota quota = quotaMapper.selectById(id);
        QuotaDTO quotaDTO = new QuotaDTO();

        BeanUtils.copyProperties(quota, quotaDTO);

        return quotaDTO;
    }

    @Override
    public List<Quota> findAll() throws BusinessException {
        return quotaMapper.selectList(null);
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public boolean createQuota(QuotaVO quotaVO) throws BusinessException {
        Quota quota = new Quota();
        BeanUtils.copyProperties(quotaVO, quota);

        quotaMapper.insert(quota);

        try {
            // 新增指标时订阅主题
            emqClient.subscribe("$queue/" + quota.getSubject());
            log.info("{} 添加指标订阅主题成功", quota.getSubject());
        } catch (Exception e) {
            throw new BusinessException("#createQuota() 主题订阅失败 " + quota.getSubject());
        }

        return true;
    }
}
