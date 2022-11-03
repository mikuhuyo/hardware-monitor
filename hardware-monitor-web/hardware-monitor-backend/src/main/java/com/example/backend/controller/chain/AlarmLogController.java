package com.example.backend.controller.chain;

import com.example.backend.service.chain.IAlarmLogService;
import com.example.chain.dto.AlarmLogDTO;
import com.example.chain.query.AlarmLogQuery;
import com.example.common.domain.RestPageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@RestController
@RequestMapping("/api")
public class AlarmLogController {
    @Autowired
    private IAlarmLogService alarmLogService;

    @PostMapping("/alarm-log/search")
    public RestPageResult<List<AlarmLogDTO>> searchAlarmLog(@RequestBody AlarmLogQuery alarmLogQuery) throws ParseException, IOException {
        return alarmLogService.searchAlarmLog(alarmLogQuery);
    }
}
