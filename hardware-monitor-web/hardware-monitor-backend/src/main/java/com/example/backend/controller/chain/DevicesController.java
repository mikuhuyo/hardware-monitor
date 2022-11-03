package com.example.backend.controller.chain;

import com.example.backend.service.chain.IDevicesService;
import com.example.chain.dto.DeviceQuotaDTO;
import com.example.chain.dto.DevicesDTO;
import com.example.chain.query.DevicesQuery;
import com.example.chain.vo.DevicesVO;
import com.example.common.domain.RestPageResult;
import com.example.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class DevicesController {
    @Autowired
    private IDevicesService devicesService;

    @PostMapping("/device/deviceQuota")
    public RestPageResult<List<DeviceQuotaDTO>> deviceQuota(@RequestBody DevicesQuery devicesQuery) throws IOException, ParseException {
        return devicesService.deviceQuotaDetails(devicesQuery);
    }

    @PostMapping("/device/client")
    public void client(@RequestBody Map<String, Object> data) {
        log.info("接收webhook报文: {}", data);

        if (data == null) {
            throw new BusinessException("报文数据为null");
        }

        String clientId = data.get("clientId").toString();
        String action = data.get("action").toString();

        // 以webclient开头的client为系统前端, monitor开头的是服务端
        if (clientId.startsWith("webclient") || clientId.startsWith("monitor")) {
            return;
        }

        // 处理设备上线状态
        if ("client_connected".equals(action)) {
            devicesService.updateDeviceOnlineById(clientId, true);
        }
        if ("client_disconnected".equals(action)) {
            devicesService.updateDeviceOnlineById(clientId, false);
        }
    }

    @PostMapping("/device")
    public RestPageResult<List<DevicesDTO>> searchDevice(@RequestBody DevicesQuery devicesQuery) throws IOException {
        return devicesService.searchDevice(devicesQuery);
    }

    @PutMapping("/device/status")
    public boolean updateState(@RequestBody DevicesVO devicesVO) {
        return devicesService.updateDeviceStatusById(devicesVO.getDeviceId(), devicesVO.getStatus());
    }

    @PutMapping("/device/tag")
    public boolean updateTag(@RequestBody DevicesVO devicesVO) {
        return devicesService.updateDeviceTagsById(devicesVO.getDeviceId(), devicesVO.getTag());
    }

    @PutMapping("/device/alarm")
    public boolean updateAlarm(@RequestBody DevicesVO devicesVO) {
        return devicesService.updateDeviceAlarmById(devicesVO);
    }

    @PutMapping("/device/online")
    public boolean updateOnline(@RequestBody DevicesVO devicesVO) {
        return devicesService.updateDeviceOnlineById(devicesVO.getDeviceId(), devicesVO.getOnline());
    }

    @GetMapping("/device/{deviceId}")
    public DevicesDTO findDeviceById(@PathVariable("deviceId") String deviceId) {
        return devicesService.findDeviceById(deviceId);
    }
}
