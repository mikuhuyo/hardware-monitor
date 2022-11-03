package com.example.backend.service.chain.impl;

import com.example.backend.service.chain.IDeviceGeoService;
import com.example.backend.service.chain.IDevicesService;
import com.example.backend.service.chain.IGpsService;
import com.example.backend.service.chain.IQuotaService;
import com.example.chain.dto.DeviceGeoDetails;
import com.example.chain.dto.DevicesDTO;
import com.example.chain.dto.GpsDTO;
import com.example.chain.dto.QuotaDTO;
import com.example.chain.pojo.DeviceGeo;
import com.example.common.constant.ElasticSearchConstant;
import com.example.common.exception.BusinessException;
import com.example.common.utils.JsonUtil;
import com.google.common.collect.Lists;
import com.mysql.cj.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author yuelimin
 * @version 1.0.0
 * @since 11
 */
@Slf4j
@Service
public class IDeviceGeoServiceImpl implements IDeviceGeoService {
    @Autowired
    private IGpsService gpsService;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    @Autowired
    private IDevicesService devicesService;
    @Autowired
    private IQuotaService quotaService;

    @Override
    public List<DeviceGeoDetails> searchDeviceGeoLocationDetails(Double longitude, Double latitude, String distance) throws BusinessException {
        // 按范围查询设备
        List<DeviceGeo> deviceGeoList = this.searchDeviceGeoLocation(longitude, latitude, distance);

        if (deviceGeoList.isEmpty()) {
            return null;
        }

        List<DeviceGeoDetails> deviceGeoDetailsList = Lists.newArrayList();

        // 查询设备详情
        deviceGeoList.forEach(deviceLocation -> {
            DeviceGeoDetails deviceGeoDetails = new DeviceGeoDetails();

            // 在线状态和告警状态
            DevicesDTO deviceDTO = devicesService.findDeviceById(deviceLocation.getDeviceId());
            // 设备id
            deviceGeoDetails.setDeviceId(deviceLocation.getDeviceId());
            // 坐标(ElasticSearch中的经纬度 纬度在前 经度在后)
            deviceGeoDetails.setLatitude(deviceLocation.getLocation().split(",")[0]);
            deviceGeoDetails.setLongitude(deviceLocation.getLocation().split(",")[1]);
            deviceGeoDetails.setLevel(deviceDTO.getLevel());
            deviceGeoDetails.setOnline(deviceDTO.getOnline());
            deviceGeoDetails.setAlarm(deviceDTO.getAlarm());

            List<QuotaDTO> quotaDTOList = quotaService.findQuotaListByDeviceId(deviceLocation.getDeviceId());
            deviceGeoDetails.setQuotaList(quotaDTOList);

            deviceGeoDetailsList.add(deviceGeoDetails);
        });

        return deviceGeoDetailsList;
    }

    @Override
    public List<DeviceGeo> searchDeviceGeoLocation(Double longitude, Double latitude, String distance) throws BusinessException {
        SearchRequest searchRequest = new SearchRequest(ElasticSearchConstant.ES_DEVICE_GEO_INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 中心点及半径构建
        GeoDistanceQueryBuilder geoDistanceQueryBuilder = new GeoDistanceQueryBuilder("location");
        geoDistanceQueryBuilder.distance(distance, DistanceUnit.KILOMETERS);
        geoDistanceQueryBuilder.point(latitude, longitude);

        searchSourceBuilder.query(geoDistanceQueryBuilder);
        // 只取前200个
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(200);

        searchRequest.source(searchSourceBuilder);

        // 从近到远排序规则构建
        // GeoDistanceSortBuilder distanceSortBuilder = new GeoDistanceSortBuilder("location", latitude, longitude);
        // distanceSortBuilder.unit(DistanceUnit.KILOMETERS);
        // distanceSortBuilder.order(SortOrder.ASC);
        // distanceSortBuilder.geoDistance(GeoDistance.ARC);
        // searchSourceBuilder.sort(distanceSortBuilder);

        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            if (hits.getTotalHits().value <= 0) {
                return Lists.newArrayList();
            }
            List<DeviceGeo> deviceLocationList = Lists.newArrayList();
            Arrays.stream(hits.getHits()).forEach(h -> {
                DeviceGeo deviceLocation = new DeviceGeo();

                deviceLocation.setDeviceId(h.getId());
                deviceLocation.setLocation(h.getSourceAsMap().get("location").toString());

                deviceLocationList.add(deviceLocation);
            });

            return deviceLocationList;
        } catch (IOException e) {
            throw new BusinessException("地理信息查询失败");
        }
    }

    @Override
    public void createDeviceGeo(DeviceGeo deviceGeo) throws BusinessException {
        IndexRequest deviceGeoIndexRequest = new IndexRequest(ElasticSearchConstant.ES_DEVICE_GEO_INDEX_NAME);

        String json;
        Map map;

        try {
            json = JsonUtil.serialize(deviceGeo);
            map = JsonUtil.getByJson(json, Map.class);
        } catch (IOException ioException) {
            throw new BusinessException("地理信息序列化异常");
        }

        deviceGeoIndexRequest.source(map);
        deviceGeoIndexRequest.id(deviceGeo.getDeviceId());
        deviceGeoIndexRequest.type("_doc");

        try {
            restHighLevelClient.index(deviceGeoIndexRequest, RequestOptions.DEFAULT);

            log.info("com.example.backend.service.chain.impl.IDeviceGeoServiceImpl#createDeviceGeo() 地理信息 {} 添加成功", deviceGeo);
        } catch (IOException e) {
            throw new BusinessException("添加地理信息失败");
        }

    }

    @Override
    public DeviceGeo analysis(String topic, Map<String, Object> payload) throws BusinessException {
        // 主题是否为空
        if (StringUtils.isNullOrEmpty(topic)) {
            throw new BusinessException("设备地理信息主题为空");
        }
        // 报文是否为空
        if (payload.isEmpty()) {
            throw new BusinessException("设备地理信息报文为空");
        }

        GpsDTO gps = gpsService.findGps();

        DeviceGeo deviceGeo = new DeviceGeo();

        String snKey = payload.get("sn").toString();
        if (StringUtils.isNullOrEmpty(snKey)) {
            throw new BusinessException("设备号为空");
        }

        // 单字段
        if ("1".equals(gps.getSingleField())) {
            // 地理信息
            String geoInfo = payload.get(gps.getValueKey()).toString().replace(gps.getSeparation(), ",");

            deviceGeo.setDeviceId(snKey);
            deviceGeo.setLocation(geoInfo);
        }
        // 双字段
        if ("2".equals(gps.getSingleField())) {
            String longitude = payload.get(gps.getLongitude()).toString();
            String latitude = payload.get(gps.getLatitude()).toString();

            if (StringUtils.isNullOrEmpty(longitude)) {
                throw new BusinessException("经度为空");
            }
            if (StringUtils.isNullOrEmpty(latitude)) {
                throw new BusinessException("纬度为空");
            }

            deviceGeo.setDeviceId(snKey);
            deviceGeo.setLocation(latitude + "," + longitude);
        }

        log.info("com.example.backend.service.chain.impl.IDeviceGeoServiceImpl#analysis() 设备地理信息解析成功, 解析报文: {}", deviceGeo);

        return deviceGeo;
    }
}
