package com.example.backend.service.chain.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.example.backend.mapper.chain.AlarmMapper;
import com.example.backend.service.chain.IAlarmLogService;
import com.example.backend.service.chain.IDevicesService;
import com.example.chain.dto.AlarmLogDTO;
import com.example.chain.dto.DevicesDTO;
import com.example.chain.dto.QuotaDTO;
import com.example.chain.pojo.Alarm;
import com.example.chain.pojo.AlarmLog;
import com.example.chain.query.AlarmLogQuery;
import com.example.common.constant.ElasticSearchConstant;
import com.example.common.constant.RedisConstant;
import com.example.common.domain.RestPageResult;
import com.example.common.exception.BusinessException;
import com.example.common.utils.DateUtil;
import com.example.common.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mysql.cj.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Slf4j
@Service
public class IAlarmLogServiceImpl implements IAlarmLogService {
    public IAlarmLogServiceImpl() {
    }

    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private AlarmMapper alarmMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private IDevicesService devicesService;

    private Integer cycle = 0;
    private String webhook = "https://github.com/mikuhuyo";

    @Override
    public void createAlarmLog(QuotaDTO quotaDTO) throws BusinessException {
        if (quotaDTO == null) {
            throw new BusinessException("??????????????????????????????, ?????????????????????");
        }

        DevicesDTO devicesDTO = new DevicesDTO();
        AlarmLog alarmLog = new AlarmLog();

        // ??????????????????????????????
        devicesDTO.setDeviceId(quotaDTO.getSnKey());
        devicesDTO.setOnline(true);
        devicesDTO.setStatus(true);
        devicesDTO.setLevel(0);
        devicesDTO.setAlarm(false);
        devicesDTO.setAlarmName("normal");
        devicesDTO.setTag(quotaDTO.getTag());

        // ????????????
        String value = quotaDTO.getValue();

        alarmLog.setDeviceId(quotaDTO.getSnKey());
        // ????????????-?????????
        alarmLog.setAlarmLogTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).getEpochSecond());
        // ??????id
        alarmLog.setQuotaId(quotaDTO.getId());
        // ????????????
        alarmLog.setUnit(quotaDTO.getUnit());
        // ?????????
        alarmLog.setReferenceValue(quotaDTO.getReferenceValue());
        // ????????????
        alarmLog.setQuotaName(quotaDTO.getName());

        // ????????????????????????
        List<Alarm> alarmRuleList = alarmMapper.selectList(new QueryWrapper<Alarm>().lambda().eq(Alarm::getSubject, quotaDTO.getSubject()));
        // ??????forEach????????????????????????, ????????????forEach??????????????????????????????
        for (Alarm alarmRule : alarmRuleList) {
            // ????????????
            alarmLog.setAlarmName(alarmRule.getName());
            // ?????????????????? ????????????
            alarmLog.setLevel(alarmRule.getLevel());

            // todo ?????????????????????????????????(?????????????????????, ???????????????) ??????RestTemplate????????????????????????????????????
            // ?????????????????? ????????????
            webhook = alarmRule.getWebhook();

            // ??????????????????
            boolean isDo = handleOperator(value, alarmRule.getThreshold(), alarmRule.getOperator(), alarmLog, devicesDTO);
            // ????????????????????????????????????
            if (!isDo) {
                continue;
            }

            // ?????????????????? ??????????????????(??????)
            cycle = alarmRule.getCycle();
            // ??????redis key
            String redisKey = String.format(RedisConstant.CYCLE_KEY, alarmLog.getDeviceId(), quotaDTO.getValueKey(), alarmLog.getLevel());
            // ??????????????????????????????, ???????????????????????????????????????
            boolean cache = checkRedisCache(redisKey);
            if (cache) {
                log.info("{} ????????????", alarmLog);
                return;
            }

            // ??????????????????
            saveAlarmLog2elasticsearch(alarmLog);
            // ??????????????????
            devicesService.saveDeviceInfo(devicesDTO);
            // ??????????????????
            redisTemplate.boundValueOps(redisKey).set("flag", Long.parseLong(cycle.toString()), TimeUnit.MINUTES);
            log.info("<{}> ?????? <{}> ??????", redisKey, alarmLog);

            return;
        }
    }

    /**
     * ?????????????????????
     *
     * @param redisKey ??????key
     * @return true?????? false?????????
     */
    private boolean checkRedisCache(String redisKey) {
        String flag = redisTemplate.boundValueOps(redisKey).get();

        if (!StringUtils.isNullOrEmpty(flag)) {
            return true;
        }

        return false;
    }

    /**
     * ???????????????????????????
     *
     * @param value      ??????
     * @param threshold  ?????????
     * @param operator   ?????????
     * @param alarmLog   ?????????
     * @param devicesDTO ?????????
     * @return true???????????? false???????????????
     */
    private boolean handleOperator(String value, String threshold, String operator, AlarmLog alarmLog, DevicesDTO devicesDTO) {
        BigDecimal parseValue = new BigDecimal(value);
        BigDecimal parseThreshold = new BigDecimal(threshold);

        // ??????????????????
        if (parseValue.compareTo(parseThreshold) == 0 && "=".equals(operator)) {
            alarmLog.setValue(value);
            alarmLog.setLevel(alarmLog.getLevel());
            alarmLog.setAlarm(1);

            devicesDTO.setAlarm(true);
            devicesDTO.setAlarmName(alarmLog.getAlarmName());
            devicesDTO.setLevel(alarmLog.getLevel() + 1);

            return true;
        }

        // ??????????????????
        if (parseValue.compareTo(parseThreshold) == -1 && "<".equals(operator)) {
            alarmLog.setValue(value);
            alarmLog.setLevel(alarmLog.getLevel());
            alarmLog.setAlarm(1);

            devicesDTO.setAlarm(true);
            devicesDTO.setAlarmName(alarmLog.getAlarmName());
            devicesDTO.setLevel(alarmLog.getLevel() + 1);

            return true;
        }

        // ??????????????????
        if (parseValue.compareTo(parseThreshold) == 1 && ">".equals(operator)) {
            alarmLog.setValue(value);
            alarmLog.setLevel(alarmLog.getLevel());
            alarmLog.setAlarm(1);

            devicesDTO.setAlarm(true);
            devicesDTO.setAlarmName(alarmLog.getAlarmName());
            devicesDTO.setLevel(alarmLog.getLevel() + 1);

            return true;
        }

        // ?????????
        if (alarmLog.getReferenceValue().contains("-") && value != null) {
            String[] split = alarmLog.getReferenceValue().split("-");
            String min = split[0];
            String max = split[1];

            BigDecimal minValue = new BigDecimal(min);
            BigDecimal maxValue = new BigDecimal(max);

            if (minValue.compareTo(parseValue) < 1 && maxValue.compareTo(parseValue) > -1) {
                // ??????????????????????????????
                alarmLog.setValue(value);
                alarmLog.setLevel(0);
                alarmLog.setAlarm(0);
                alarmLog.setAlarmName("normal");
                // ?????????????????????????????????30??????
                cycle = 30;

                devicesDTO.setAlarm(false);
                devicesDTO.setAlarmName("normal");
                devicesDTO.setLevel(0);

                return true;
            }
        }

        return false;
    }

    /**
     * ????????????????????????ElasticSearch???
     *
     * @param alarmLog com.example.chain.pojo.AlarmLog
     */
    private void saveAlarmLog2elasticsearch(AlarmLog alarmLog) {
        // elasticsearch??????????????????
        IndexRequest alarm = new IndexRequest(ElasticSearchConstant.ES_ALARM_LOG_INDEX_NAME);
        try {
            String json = JsonUtil.serialize(alarmLog);
            Map map = JsonUtil.getByJson(json, Map.class);
            alarm.source(map);
            // ??????????????????????????????id
            alarm.id(IdWorker.getId() + "");
            alarm.type("_doc");

            restHighLevelClient.index(alarm, RequestOptions.DEFAULT);
        } catch (JsonProcessingException e) {
            throw new BusinessException("???????????????");
        } catch (IOException e) {
            throw new BusinessException("??????????????????");
        }

        log.info("{} ????????????????????????", alarmLog);
    }

    @Override
    public AlarmLogDTO findAlarmLogByTimeDesc(String deviceId) throws BusinessException, IOException, ParseException {
        AlarmLogQuery alarmLogQuery = new AlarmLogQuery();
        alarmLogQuery.setDeviceId(deviceId);
        alarmLogQuery.setPage(1L);
        alarmLogQuery.setPageSize(1L);

        RestPageResult<List<AlarmLogDTO>> listRestPageResult = this.searchAlarmLog(alarmLogQuery);
        if (listRestPageResult == null || listRestPageResult.getItems() == null) {
            return null;
        }

        return listRestPageResult.getItems().get(0);
    }

    @Override
    public RestPageResult<List<AlarmLogDTO>> searchAlarmLog(AlarmLogQuery alarmLogQuery) throws BusinessException, ParseException, IOException {
        SearchRequest searchRequest = new SearchRequest(ElasticSearchConstant.ES_ALARM_LOG_INDEX_NAME);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // ????????????
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // ????????????
        if (!Strings.isNullOrEmpty(alarmLogQuery.getDeviceId())) {
            // should ??????
            // must ??????
            // * ???????????????
            // ? ???????????????
            boolQueryBuilder.must(QueryBuilders.matchQuery("deviceId", alarmLogQuery.getDeviceId()));
        }
        // ????????????
        if (alarmLogQuery.getAlarm() != null) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("level", alarmLogQuery.getAlarm()));
        }
        // ????????????-????????????
        if (!Strings.isNullOrEmpty(alarmLogQuery.getStart()) && !Strings.isNullOrEmpty(alarmLogQuery.getEnd())) {
            // ?????????????????????
            Long startTime = DateUtil.datetimeFormat2second(alarmLogQuery.getStart());
            // ?????????????????????
            Long endTime = DateUtil.datetimeFormat2second(alarmLogQuery.getEnd());

            boolQueryBuilder.filter(QueryBuilders.rangeQuery("alarmLogTime").gt(startTime).lt(endTime));
        }
        // ????????????
        if (alarmLogQuery.getAlarm() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery("alarm", alarmLogQuery.getAlarm()));
        }

        sourceBuilder.query(boolQueryBuilder);
        // ??????
        sourceBuilder.from((alarmLogQuery.getPage().intValue() - 1) * alarmLogQuery.getPageSize().intValue());
        sourceBuilder.size(alarmLogQuery.getPageSize().intValue());
        sourceBuilder.trackTotalHits(true);

        // ??????
        // ????????????????????????
        sourceBuilder.sort("alarmLogTime", SortOrder.DESC);
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("????????????");
        }

        SearchHits hits = searchResponse.getHits();
        if (hits.getTotalHits().value <= 0) {
            RestPageResult<List<AlarmLogDTO>> result = new RestPageResult<>();

            result.setCounts(0L);
            result.setPage(alarmLogQuery.getPage());
            result.setPageSize(alarmLogQuery.getPageSize());

            return result;
        }

        SearchHit[] searchHits = hits.getHits();
        List<AlarmLogDTO> alarmLogDTOList = Lists.newArrayList();

        // ????????????
        for (SearchHit hit : searchHits) {
            String hitResult = hit.getSourceAsString();
            AlarmLog alarmLog = JsonUtil.getByJson(hitResult, AlarmLog.class);
            AlarmLogDTO alarmLogDTO = new AlarmLogDTO();

            BeanUtils.copyProperties(alarmLog, alarmLogDTO);
            alarmLogDTO.setTime(DateUtil.secondFormat(alarmLog.getAlarmLogTime()));

            alarmLogDTOList.add(alarmLogDTO);
        }

        RestPageResult<List<AlarmLogDTO>> pageResult = new RestPageResult<>();

        Long totalHits = searchResponse.getHits().getTotalHits().value;
        Long pages = totalHits % alarmLogQuery.getPageSize() == 0 ? totalHits / alarmLogQuery.getPageSize() : totalHits / alarmLogQuery.getPageSize() + 1;

        // ????????????
        pageResult.setCounts(totalHits);
        // ?????????
        pageResult.setPage(alarmLogQuery.getPage());
        // ????????????
        pageResult.setItems(alarmLogDTOList);
        // ???????????????
        pageResult.setPageSize(alarmLogQuery.getPageSize());
        // ?????????
        pageResult.setPages(pages);

        return pageResult;

    }
}
