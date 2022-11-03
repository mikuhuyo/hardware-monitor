package com.example.backend.controller.chain;

import com.example.backend.service.chain.IAlarmService;
import com.example.chain.dto.AlarmDTO;
import com.example.chain.query.AlarmQuery;
import com.example.chain.vo.AlarmVO;
import com.example.common.domain.RestPageResult;
import com.example.common.domain.RestResponse;
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
public class AlarmController {
    @Autowired
    private IAlarmService alarmService;

    @PostMapping("/alarm/search")
    public RestPageResult<List<AlarmDTO>> searchAlarm(@RequestBody AlarmQuery alarmQuery) {
        return alarmService.searchAlarm(alarmQuery);
    }

    @PutMapping("/alarm")
    public RestResponse<String> updateById(@RequestBody AlarmVO alarmVO) {
        alarmService.updateAlarmById(alarmVO);

        return RestResponse.success();
    }

    @DeleteMapping("/alarm/{id}")
    public RestResponse<String> removeById(@PathVariable("id") Long id) {
        alarmService.removeAlarmById(id);

        return RestResponse.success();
    }

    @PostMapping("/alarm")
    public RestResponse<String> createAlarm(@RequestBody AlarmVO alarmVO) {
        alarmService.createAlarm(alarmVO);

        return RestResponse.success();
    }
}
