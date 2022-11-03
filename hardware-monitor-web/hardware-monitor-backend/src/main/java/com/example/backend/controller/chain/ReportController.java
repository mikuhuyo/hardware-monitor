package com.example.backend.controller.chain;

import com.example.backend.service.chain.IReportService;
import com.example.chain.dto.*;
import com.example.chain.query.BaseQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@RestController
@RequestMapping("/api")
public class ReportController {
    @Autowired
    private IReportService reportService;

    // todo 数据预览
    // @GetMapping("/report/preview")

    // todo 指标看板数据
    // @PostMapping("/report/board/data")

    @PostMapping("/report/devices")
    public List<String> getDeviceIdsListByQuotaId(@RequestBody BaseQuery baseQuery) {
        return reportService.getDeviceNameByQuotaId(baseQuery.getId());
    }

    @GetMapping(value = {"/report/alarm/real-time", "/report/alarm/real-time/{seconds}"})
    public List<AlarmLogDTO> realTimeAlarmLog(@PathVariable(required = false, value = "seconds") Integer seconds) {
        // 结束时间
        long end = System.currentTimeMillis() / 1000;
        long start = 0;
        if (seconds != null) {
            // 开始时间
            start = end - seconds;
        }

        start = end - 5L;

        return reportService.getRealTimeAlarmLog(start, end);
    }

    @PostMapping("/report/alarm/top10")
    public List<DeviceHeapDTO> getDeviceAlarmTop10(@RequestBody BaseQuery baseQuery) {
        return reportService.getDeviceAbnormalTop10(baseQuery.getStartTime(), baseQuery.getEndTime(), baseQuery.getType());
    }

    @PostMapping("/report/alarm/trend")
    public List<DeviceTrendDTO> getDeviceAlarmTrend(@RequestBody BaseQuery baseQuery) {
        return reportService.getDeviceAbnormalTrendList(baseQuery.getStartTime(), baseQuery.getEndTime(), baseQuery.getType());
    }

    @GetMapping("/report/device/monitor")
    public DeviceMonitorDTO getDeviceMonitor() {
        return reportService.getDeviceMonitor();
    }

    @GetMapping("/report/device/status-collect")
    public List<DevicePieDTO> getDeviceStatusCollect() {
        return reportService.getStatusCollect();
    }
}
