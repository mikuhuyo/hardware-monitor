package com.example.backend.service.chain.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.backend.mapper.chain.AlarmMapper;
import com.example.backend.mapper.chain.QuotaMapper;
import com.example.backend.service.chain.IAlarmService;
import com.example.chain.dto.AlarmDTO;
import com.example.chain.pojo.Alarm;
import com.example.chain.query.AlarmQuery;
import com.example.chain.vo.AlarmVO;
import com.example.common.domain.RestPageResult;
import com.example.common.exception.BusinessException;
import com.mysql.cj.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Slf4j
@Service
public class IAlarmServiceImpl implements IAlarmService {
    @Autowired
    private AlarmMapper alarmMapper;
    @Autowired
    private QuotaMapper quotaMapper;

    @Override
    public RestPageResult<List<AlarmDTO>> searchAlarm(AlarmQuery alarmQuery) throws BusinessException {
        LambdaQueryWrapper<Alarm> lambda = new QueryWrapper<Alarm>().lambda();

        if (!StringUtils.isNullOrEmpty(alarmQuery.getName())) {
            lambda.like(Alarm::getName, alarmQuery.getName());
        }

        if (alarmQuery.getQuotaId() != null) {
            lambda.eq(Alarm::getQuotaId, alarmQuery.getQuotaId());
        }

        IPage<Alarm> selectPage = alarmMapper.selectPage(new Page<>(alarmQuery.getPage(), alarmQuery.getPageSize()), lambda);
        List<AlarmDTO> alarmDTOList = new ArrayList<>();
        List<Alarm> records = selectPage.getRecords();

        if (records.size() == 0) {
            RestPageResult<List<AlarmDTO>> result = new RestPageResult<>();

            result.setCounts(0L);
            result.setPage(alarmQuery.getPage());
            result.setPageSize(alarmQuery.getPageSize());

            return result;
        }

        records.forEach(alarm -> {
            AlarmDTO alarmDTO = new AlarmDTO();

            BeanUtils.copyProperties(alarm, alarmDTO);
            alarmDTO.setQuotaName(quotaMapper.selectById(alarmDTO.getQuotaId()).getName());

            alarmDTOList.add(alarmDTO);
        });

        RestPageResult<List<AlarmDTO>> result = new RestPageResult<>();

        result.setCounts(selectPage.getTotal());
        result.setItems(alarmDTOList);
        result.setPages(selectPage.getPages());
        result.setPage(alarmQuery.getPage());
        result.setPageSize(alarmQuery.getPageSize());

        return result;
    }

    @Override
    public AlarmDTO findAlarmById(Long id) throws BusinessException {
        Alarm alarm = alarmMapper.selectById(id);

        AlarmDTO alarmDTO = new AlarmDTO();
        BeanUtils.copyProperties(alarm, alarmDTO);

        return alarmDTO;
    }

    @Override
    public void updateAlarmById(AlarmVO alarmVO) throws BusinessException {
        Alarm alarm = new Alarm();

        BeanUtils.copyProperties(alarmVO, alarm);

        String subject = quotaMapper.selectById(alarmVO.getQuotaId()).getSubject();
        alarm.setSubject(subject);

        alarmMapper.updateById(alarm);
    }

    @Override
    public void removeAlarmById(Long id) throws BusinessException {
        alarmMapper.deleteById(id);
    }

    @Override
    public void createAlarm(AlarmVO alarmVO) throws BusinessException {
        Alarm alarm = new Alarm();
        LambdaQueryWrapper<Alarm> queryWrapper = new QueryWrapper<Alarm>().lambda().eq(Alarm::getQuotaId, alarmVO.getQuotaId()).eq(Alarm::getName, alarmVO.getName());
        boolean flag = alarmMapper.selectCount(queryWrapper) != 0;
        if (flag) {
            throw new BusinessException("告警添加重复");
        }

        String subject = quotaMapper.selectById(alarmVO.getQuotaId()).getSubject();

        BeanUtils.copyProperties(alarmVO, alarm);
        alarm.setSubject(subject);

        alarmMapper.insert(alarm);
    }
}
