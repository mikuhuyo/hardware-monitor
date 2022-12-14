package com.example.backend.service.chain.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.backend.mapper.chain.QuotaMapper;
import com.example.backend.service.chain.IAlarmLogService;
import com.example.backend.service.chain.IDevicesService;
import com.example.chain.dto.AlarmLogDTO;
import com.example.chain.dto.DeviceQuotaDTO;
import com.example.chain.dto.DevicesDTO;
import com.example.chain.dto.QuotaDTO;
import com.example.chain.pojo.Quota;
import com.example.chain.query.DevicesQuery;
import com.example.chain.vo.DevicesVO;
import com.example.common.constant.ElasticSearchConstant;
import com.example.common.domain.RestPageResult;
import com.example.common.exception.BusinessException;
import com.example.common.utils.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mysql.cj.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Slf4j
@Service
public class IDevicesServiceImpl implements IDevicesService {
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private QuotaMapper quotaMapper;
    @Autowired
    private IAlarmLogService alarmLogService;

    @Override
    public Long getOnlineDeviceCountByNormal() throws BusinessException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("online", true));
        searchSourceBuilder.query(QueryBuilders.matchQuery("status", true));
        searchSourceBuilder.query(QueryBuilders.matchQuery("alarm", false));

        CountRequest countRequest = new CountRequest(ElasticSearchConstant.ES_DEVICES_INDEX_NAME);
        countRequest.source(searchSourceBuilder);

        CountResponse response;
        try {
            response = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("#getOnlineDeviceCountByNormal() ??????????????????????????????");
        }

        return response.getCount();
    }

    @Override
    public Long getOnlineDeviceCountByAlarm() throws BusinessException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("online", true));
        searchSourceBuilder.query(QueryBuilders.matchQuery("status", true));
        searchSourceBuilder.query(QueryBuilders.matchQuery("alarm", true));

        CountRequest countRequest = new CountRequest(ElasticSearchConstant.ES_DEVICES_INDEX_NAME);
        countRequest.source(searchSourceBuilder);

        CountResponse response;
        try {
            response = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("#getOnlineDeviceCountByAlarm() ??????????????????????????????");
        }

        return response.getCount();
    }

    @Override
    public Long getOnlineDeviceCount() throws BusinessException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("online", true));
        searchSourceBuilder.query(QueryBuilders.matchQuery("status", true));

        CountRequest countRequest = new CountRequest(ElasticSearchConstant.ES_DEVICES_INDEX_NAME);
        countRequest.source(searchSourceBuilder);

        CountResponse response;
        try {
            response = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("#getOnlineDeviceCount() ??????????????????????????????");
        }

        return response.getCount();
    }

    @Override
    public Long getOfflineDeviceCount() throws BusinessException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("status", false));
        searchSourceBuilder.query(QueryBuilders.matchQuery("online", false));

        CountRequest countRequest = new CountRequest(ElasticSearchConstant.ES_DEVICES_INDEX_NAME);
        countRequest.source(searchSourceBuilder);

        CountResponse response;
        try {
            response = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("#getOfflineDeviceCount() ??????????????????????????????");
        }

        return response.getCount();
    }

    @Override
    public Long getAllDeviceCount() throws BusinessException {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        CountRequest countRequest = new CountRequest(ElasticSearchConstant.ES_DEVICES_INDEX_NAME);
        countRequest.source(searchSourceBuilder);

        CountResponse response;
        try {
            response = restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("#getAllDeviceCount() ????????????????????????");
        }

        return response.getCount();
    }

    @Override
    public RestPageResult<List<DeviceQuotaDTO>> deviceQuotaDetails(DevicesQuery devicesQuery) throws BusinessException, IOException, ParseException {
        RestPageResult<List<DeviceQuotaDTO>> result = new RestPageResult<>();
        result.setCounts(0L);
        result.setPage(devicesQuery.getPage());
        result.setPageSize(devicesQuery.getPageSize());

        // ?????????????????????????????????
        RestPageResult<List<DevicesDTO>> listRestPageResult = this.searchDevice(devicesQuery);
        if (listRestPageResult.getItems().isEmpty()) {
            return result;
        }

        // ??????????????????
        List<DevicesDTO> items = listRestPageResult.getItems();
        List<DeviceQuotaDTO> lastItems = new ArrayList<>();

        // ??????????????????, ??????????????????
        for (DevicesDTO devicesDTO : items) {
            AlarmLogDTO alarmLogByTimeDesc = alarmLogService.findAlarmLogByTimeDesc(devicesDTO.getDeviceId());
            if (alarmLogByTimeDesc == null) {
                return result;
            }

            DeviceQuotaDTO deviceQuotaDTO = new DeviceQuotaDTO();
            BeanUtils.copyProperties(devicesDTO, deviceQuotaDTO);

            List<QuotaDTO> quotaDTOList = new ArrayList<>();
            List<Quota> quotas = quotaMapper.selectList(new QueryWrapper<Quota>().lambda().eq(Quota::getSnKey, devicesDTO.getDeviceId()));
            quotas.forEach(quota -> {
                QuotaDTO quotaDTO = new QuotaDTO();
                BeanUtils.copyProperties(quota, quotaDTO);

                // ???????????????
                if (devicesDTO.getAlarm() && quota.getId().equals(alarmLogByTimeDesc.getQuotaId())) {
                    quotaDTO.setReferenceValue(alarmLogByTimeDesc.getReferenceValue());
                    quotaDTO.setValue(alarmLogByTimeDesc.getValue());
                    quotaDTO.setLevel(alarmLogByTimeDesc.getLevel());
                } else {
                    quotaDTO.setValue("normal");
                }

                quotaDTOList.add(quotaDTO);
            });

            deviceQuotaDTO.setQuotaList(quotaDTOList);
            lastItems.add(deviceQuotaDTO);
        }

        result.setItems(lastItems);
        result.setPage(listRestPageResult.getPage());
        result.setPageSize(listRestPageResult.getPageSize());
        result.setCounts(listRestPageResult.getCounts());
        result.setPages(listRestPageResult.getPages());

        return result;
    }

    @Override
    public boolean saveDeviceInfo(DevicesDTO devicesDTO) throws BusinessException {
        // ???????????? ??????????????????, ???????????????????????????
        DevicesDTO device = this.findDeviceById(devicesDTO.getDeviceId());
        if (device != null && !device.getStatus()) {
            return false;
        }

        // ???????????????????????????, ??????
        if (device == null) {
            this.addDevices(devicesDTO);
        } else {
            // ?????????????????????, ??????????????????
            DevicesVO devicesVO = new DevicesVO();

            BeanUtils.copyProperties(devicesDTO, devicesVO);

            this.updateDeviceAlarmById(devicesVO);
        }

        return true;
    }

    @Override
    public QuotaDTO analysis(String topic, Map<String, Object> payloadMap) throws BusinessException {

        QuotaDTO quotaDTO = new QuotaDTO();
        Quota quota = quotaMapper.selectOne(new QueryWrapper<Quota>().lambda().eq(Quota::getSubject, topic));
        if (payloadMap.containsKey(quota.getValueKey()) && payloadMap.containsKey("tag") && payloadMap.containsKey("sn")) {

            String valueString = payloadMap.get(quota.getValueKey()).toString();
            String tagString = payloadMap.get("tag").toString();
            String snString = payloadMap.get("sn").toString();

            if (StringUtils.isNullOrEmpty(valueString) || StringUtils.isNullOrEmpty(tagString) || StringUtils.isNullOrEmpty(snString)) {
                throw new BusinessException("??????????????????, ??????????????????????????????????????????????????????");
            }

            BeanUtils.copyProperties(quota, quotaDTO);
            quotaDTO.setSnKey(snString);
            quotaDTO.setValue(valueString);
            quotaDTO.setTag(tagString);
        }

        log.info("{} ??????????????????", quotaDTO);

        return quotaDTO;
    }

    @Override
    public RestPageResult<List<DevicesDTO>> searchDevice(DevicesQuery devicesQueryVO) throws BusinessException, IOException {
        SearchRequest searchRequest = new SearchRequest(ElasticSearchConstant.ES_DEVICES_INDEX_NAME);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // ????????????
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // ????????????
        if (!Strings.isNullOrEmpty(devicesQueryVO.getDeviceId())) {
            // should ??????
            // must ??????
            // * ???????????????
            // ? ???????????????
            boolQueryBuilder.must(QueryBuilders.matchQuery("deviceId", devicesQueryVO.getDeviceId()));
        }

        // ??????
        if (!Strings.isNullOrEmpty(devicesQueryVO.getTag())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("tag", "*" + devicesQueryVO.getTag() + "*"));
        }

        // ??????(???????????????????????????)
        // 0 ??????
        // 1 ??????
        // 2 ????????????
        // 3 ????????????
        if (devicesQueryVO.getState() != null) {
            if (devicesQueryVO.getState() == 0) {
                boolQueryBuilder.must(QueryBuilders.termQuery("online", true));
            }
            if (devicesQueryVO.getState() == 1) {
                boolQueryBuilder.must(QueryBuilders.termQuery("online", false));
            }
            if (devicesQueryVO.getState() == 2) {
                boolQueryBuilder.must(QueryBuilders.termQuery("level", 1));
            }
            if (devicesQueryVO.getState() == 3) {
                boolQueryBuilder.must(QueryBuilders.termQuery("level", 2));
            }
        }

        sourceBuilder.query(boolQueryBuilder);
        // ??????
        sourceBuilder.from((devicesQueryVO.getPage().intValue() - 1) * devicesQueryVO.getPageSize().intValue());
        sourceBuilder.size(devicesQueryVO.getPageSize().intValue());
        sourceBuilder.trackTotalHits(true);

        // ??????
        // ???????????????????????????
        sourceBuilder.sort("level", SortOrder.DESC);
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse;

        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("????????????");
        }

        SearchHits hits = searchResponse.getHits();
        if (hits.getTotalHits().value <= 0) {
            RestPageResult<List<DevicesDTO>> result = new RestPageResult<>();

            result.setCounts(0L);
            result.setPage(devicesQueryVO.getPage());
            result.setPageSize(devicesQueryVO.getPage());

            return result;
        }

        SearchHit[] searchHits = hits.getHits();
        List<DevicesDTO> devices = Lists.newArrayList();
        // ????????????
        for (SearchHit hit : searchHits) {
            String hitResult = hit.getSourceAsString();
            DevicesDTO deviceDTO = JsonUtil.getByJson(hitResult, DevicesDTO.class);
            devices.add(deviceDTO);
        }

        RestPageResult<List<DevicesDTO>> pageResult = new RestPageResult<>();

        Long totalHits = searchResponse.getHits().getTotalHits().value;
        Long pages = totalHits % devicesQueryVO.getPageSize() == 0 ? totalHits / devicesQueryVO.getPageSize() : totalHits / devicesQueryVO.getPageSize() + 1;

        // ????????????
        pageResult.setCounts(totalHits);
        // ?????????
        pageResult.setPage(devicesQueryVO.getPage());
        // ????????????
        pageResult.setItems(devices);
        // ???????????????
        pageResult.setPageSize(devicesQueryVO.getPageSize());
        // ?????????
        pageResult.setPages(pages);

        return pageResult;
    }

    @Override
    public boolean updateDeviceOnlineById(String deviceId, Boolean online) throws BusinessException {
        UpdateRequest updateRequest = new UpdateRequest(ElasticSearchConstant.ES_DEVICES_INDEX_NAME, "_doc", deviceId).doc("online", online);

        try {
            restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new BusinessException("??????????????????????????????");
        }

        return true;
    }

    @Override
    public boolean updateDeviceAlarmById(DevicesVO devicesVO) throws BusinessException {
        UpdateRequest updateRequest = new UpdateRequest(ElasticSearchConstant.ES_DEVICES_INDEX_NAME, "_doc", devicesVO.getDeviceId())
                .doc("alarmName", devicesVO.getAlarmName(), "level", devicesVO.getLevel(), "alarm", devicesVO.getAlarm());

        try {
            restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new BusinessException("??????????????????????????????");
        }

        return true;
    }

    @Override
    public boolean updateDeviceTagsById(String deviceId, String tags) throws BusinessException {
        UpdateRequest updateRequest = new UpdateRequest(ElasticSearchConstant.ES_DEVICES_INDEX_NAME, "_doc", deviceId).doc("tag", tags);

        try {
            restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new BusinessException("????????????????????????");
        }

        return true;
    }

    @Override
    public boolean updateDeviceStatusById(String deviceId, Boolean status) throws BusinessException {
        UpdateRequest updateRequest = new UpdateRequest(ElasticSearchConstant.ES_DEVICES_INDEX_NAME, "_doc", deviceId).doc("status", status);

        try {
            restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new BusinessException("????????????????????????");
        }

        // ?????????????????????????????????
        this.updateDeviceOnlineById(deviceId, status);

        return true;
    }

    @Override
    public DevicesDTO findDeviceById(String deviceId) throws BusinessException {
        SearchRequest searchRequest = new SearchRequest(ElasticSearchConstant.ES_DEVICES_INDEX_NAME);

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.termQuery("_id", deviceId));

        searchRequest.source(searchSourceBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

            SearchHits hits = searchResponse.getHits();
            long hitsCount = hits.getTotalHits().value;
            if (hitsCount <= 0) {
                return null;
            }

            DevicesDTO deviceDTO = null;
            for (SearchHit hit : hits) {
                String hitResult = hit.getSourceAsString();
                deviceDTO = JsonUtil.getByJson(hitResult, DevicesDTO.class);
                deviceDTO.setDeviceId(deviceId);
                break;
            }

            return deviceDTO;
        } catch (IOException e) {
            throw new BusinessException("??????????????????");
        }
    }

    @Override
    public void addDevices(DevicesDTO devicesDTO) throws BusinessException {
        if (devicesDTO == null) {
            throw new BusinessException("????????????????????????");
        }

        // ????????????
        IndexRequest devices = new IndexRequest(ElasticSearchConstant.ES_DEVICES_INDEX_NAME);
        try {
            String json = JsonUtil.serialize(devicesDTO);
            Map map = JsonUtil.getByJson(json, Map.class);
            devices.source(map);
            devices.id(devicesDTO.getDeviceId());
            devices.type("_doc");

            restHighLevelClient.index(devices, RequestOptions.DEFAULT);
        } catch (JsonProcessingException e) {
            throw new BusinessException("???????????????");
        } catch (IOException e) {
            throw new BusinessException("??????????????????");
        }
    }
}
