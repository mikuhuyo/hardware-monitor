package com.example.backend.service.chain.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.emq.client.EmqClient;
import com.example.backend.mapper.chain.GpsMapper;
import com.example.backend.service.chain.IGpsService;
import com.example.chain.dto.GpsDTO;
import com.example.chain.pojo.Gps;
import com.example.chain.vo.GpsVO;
import com.example.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Slf4j
@Service
public class IGpsServiceImpl implements IGpsService {
    @Autowired
    private GpsMapper gpsMapper;
    @Autowired
    private EmqClient emqClient;

    @Override
    public GpsDTO findGps() throws BusinessException {
        Gps gps = gpsMapper.selectById(1);
        if (gps == null) {
            return null;
        }

        GpsDTO gpsDTO = new GpsDTO();

        BeanUtils.copyProperties(gps, gpsDTO);

        return gpsDTO;
    }

    @Override
    public void updateGpsById(GpsVO gpsVO) throws BusinessException {
        Gps gps = new Gps();

        BeanUtils.copyProperties(gpsVO, gps);

        gps.setId(1L);

        gpsMapper.updateById(gps);

        // 更新信息重新订阅主题
        emqClient.subscribe(gps.getSubject());
    }

    @Override
    public void removeGpsById(Long id) throws BusinessException {
        gpsMapper.deleteById(id);
    }

    @Override
    public void creatGps(GpsVO gpsVO) throws BusinessException {
        Gps gps = new Gps();

        BeanUtils.copyProperties(gpsVO, gps);

        gpsMapper.insert(gps);
    }
}
