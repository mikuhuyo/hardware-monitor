package com.example.backend.service.chain.impl;

import com.example.backend.mapper.chain.QuotaMapper;
import com.example.backend.service.chain.IDevicesService;
import com.example.backend.service.chain.IReportService;
import com.example.chain.dto.*;
import com.example.chain.pojo.AlarmLog;
import com.example.chain.pojo.Quota;
import com.example.common.constant.ElasticSearchConstant;
import com.example.common.exception.BusinessException;
import com.example.common.utils.DateUtil;
import com.example.common.utils.JsonUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Slf4j
@Service
public class IReportServiceImpl implements IReportService {
    @Autowired
    private IDevicesService devicesService;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private QuotaMapper quotaMapper;

    @Override
    public List<String> getDeviceNameByQuotaId(Long quotaId) {
        List<String> result = Lists.newArrayList();
        Quota quota = quotaMapper.selectById(quotaId);
        if (quota == null) {
            return result;
        }

        result.add(quota.getSnKey());

        return result;
    }

    @Override
    public List<AlarmLogDTO> getRealTimeAlarmLog(Long start, Long end) {
        SearchRequest searchRequest = new SearchRequest(ElasticSearchConstant.ES_ALARM_LOG_INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // ??????
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(6);
        searchSourceBuilder.trackTotalHits(true);
        // ????????????
        searchSourceBuilder.sort("alarmLogTime", SortOrder.DESC);

        // ????????????
        RangeQueryBuilder rangeQueryBuilder = this.timeRangeQueryBuilder(start, end);
        boolQueryBuilder.filter(rangeQueryBuilder);

        // ????????????
        searchSourceBuilder.query(boolQueryBuilder);
        // ????????????
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = this.esQuery(searchRequest);
        if (searchResponse.getHits().getTotalHits().value <= 0) {
            return Lists.newArrayList();
        }

        log.info("#getRealTimeAlarmLog() ES response -> {}", searchResponse.toString());

        AlarmLogDTO alarmLogDTO = new AlarmLogDTO();
        List<AlarmLogDTO> result = new ArrayList<>();
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            try {
                AlarmLog alarmLog = JsonUtil.getByJson(hit.getSourceAsString(), AlarmLog.class);
                BeanUtils.copyProperties(alarmLog, alarmLogDTO);

                alarmLogDTO.setTime(DateUtil.secondFormat(alarmLog.getAlarmLogTime()));
                alarmLogDTO.setOnline(devicesService.findDeviceById(alarmLog.getDeviceId()).getOnline());

                result.add(alarmLogDTO);
            } catch (IOException ioException) {
                log.error("#getRealTimeAlarmLog() ElasticSearch??????????????????");

                throw new BusinessException("??????????????????");
            }
        }

        return result;
    }

    @Override
    public List<DeviceHeapDTO> getDeviceAbnormalTop10(String start, String end, String type) {
        // ????????????
        SearchRequest deviceSearchRequest = new SearchRequest(ElasticSearchConstant.ES_ALARM_LOG_INDEX_NAME);

        // ???????????????
        SearchSourceBuilder deviceSearchSourceBuilder = new SearchSourceBuilder();
        // ??????????????????
        BoolQueryBuilder deviceBooleanQueryBuild = QueryBuilders.boolQuery();

        // ??????????????????
        deviceBooleanQueryBuild.must(QueryBuilders.termQuery("level", 2));
        // ????????????
        deviceBooleanQueryBuild.filter(timeRangeQueryBuilder(start, end));
        // ????????????
        deviceSearchSourceBuilder.aggregation(AggregationBuilders.terms("alarmLogGroup").field("deviceId.keyword").size(10));
        // ????????????
        deviceSearchSourceBuilder.sort("alarmLogTime", SortOrder.DESC);
        // ??????????????????
        deviceSearchSourceBuilder.query(deviceBooleanQueryBuild);

        // ????????????
        deviceSearchRequest.source(deviceSearchSourceBuilder);

        log.info("#getDeviceAbnormalTop10() ES query -> {}", deviceSearchSourceBuilder.toString());
        SearchResponse searchResponse = this.esQuery(deviceSearchRequest);

        List<DeviceHeapDTO> result = Lists.newArrayList();

        Terms terms = searchResponse.getAggregations().get("alarmLogGroup");
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        if (buckets.isEmpty()) {
            return result;
        }

        for (Terms.Bucket bucket : buckets) {
            DeviceHeapDTO deviceHeapDTO = new DeviceHeapDTO();
            deviceHeapDTO.setDeviceId(bucket.getKeyAsString());
            deviceHeapDTO.setHeapValue(String.valueOf(bucket.getDocCount()));

            result.add(deviceHeapDTO);
        }

        return result;
    }

    @Override
    public List<DeviceTrendDTO> getDeviceAbnormalTrendList(String start, String end, String type) {
        List<DeviceTrendDTO> result = Lists.newArrayList();
        return Lists.newArrayList();
    }

    private RangeQueryBuilder timeRangeQueryBuilder(String start, String end) {

        // ????????????-???
        Long startSecond = 0L;
        // ????????????-???
        Long endSecond = 0L;
        try {
            startSecond = DateUtil.datetimeFormat2second(start);
            endSecond = DateUtil.datetimeFormat2second(end);
        } catch (ParseException e) {
            log.error("#timeRangeQueryBuilder() ?????????????????????");

            throw new BusinessException("??????????????????");
        }

        // ??????????????????
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("alarmLogTime");

        // ????????????????????????
        rangeQueryBuilder.lte(endSecond);
        // ????????????????????????
        rangeQueryBuilder.gte(startSecond);

        return rangeQueryBuilder;
    }

    private RangeQueryBuilder timeRangeQueryBuilder(Long start, Long end) {
        // ??????????????????
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("alarmLogTime");

        // ????????????????????????
        rangeQueryBuilder.gte(start);
        // ????????????????????????
        rangeQueryBuilder.lte(end);

        return rangeQueryBuilder;
    }

    private SearchResponse esQuery(SearchRequest searchRequest) {
        SearchResponse searchResponse;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            log.info("#esQuery() ES response -> {}", searchResponse.toString());
        } catch (Exception e) {
            log.error("#esQuery() ES search error");
            throw new BusinessException("??????????????????");
        }

        return searchResponse;
    }

    @Override
    public DeviceMonitorDTO getDeviceMonitor() throws BusinessException {
        // ??????????????????
        Long onlineDeviceCount = devicesService.getOnlineDeviceCount();
        // ??????????????????
        Long onlineDeviceCountByAlarm = devicesService.getOnlineDeviceCountByAlarm();

        return new DeviceMonitorDTO().setAlarmCount(onlineDeviceCountByAlarm).setDeviceCount(onlineDeviceCount);
    }

    @Override
    public List<DevicePieDTO> getStatusCollect() throws BusinessException {
        // ????????????
        // Long allDeviceCount = devicesService.getAllDeviceCount();
        // ????????????
        Long offlineDeviceCount = devicesService.getOfflineDeviceCount();
        // ????????????
        // Long onlineDeviceCount = devicesService.getOnlineDeviceCount();
        // ??????????????????
        Long onlineDeviceCountByAlarm = devicesService.getOnlineDeviceCountByAlarm();
        // ??????????????????
        Long onlineDeviceCountByNormal = devicesService.getOnlineDeviceCountByNormal();

        List<DevicePieDTO> pieDTOList = Lists.newArrayList();

        // ????????????
        pieDTOList.add(new DevicePieDTO().setName("????????????").setValue(onlineDeviceCountByNormal));
        // ????????????
        pieDTOList.add(new DevicePieDTO().setName("????????????").setValue(onlineDeviceCountByAlarm));
        // ????????????
        pieDTOList.add(new DevicePieDTO().setName("????????????").setValue(offlineDeviceCount));

        return pieDTOList;
    }
}
