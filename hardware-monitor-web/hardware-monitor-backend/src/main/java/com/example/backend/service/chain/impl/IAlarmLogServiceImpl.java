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
            throw new BusinessException("告警日志告警添加失败, 失败原因空数据");
        }

        DevicesDTO devicesDTO = new DevicesDTO();
        AlarmLog alarmLog = new AlarmLog();

        // 预期设备信息一切正常
        devicesDTO.setDeviceId(quotaDTO.getSnKey());
        devicesDTO.setOnline(true);
        devicesDTO.setStatus(true);
        devicesDTO.setLevel(0);
        devicesDTO.setAlarm(false);
        devicesDTO.setAlarmName("normal");
        devicesDTO.setTag(quotaDTO.getTag());

        // 获取数值
        String value = quotaDTO.getValue();

        alarmLog.setDeviceId(quotaDTO.getSnKey());
        // 报警时间-当前秒
        alarmLog.setAlarmLogTime(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).getEpochSecond());
        // 指标id
        alarmLog.setQuotaId(quotaDTO.getId());
        // 告警单位
        alarmLog.setUnit(quotaDTO.getUnit());
        // 参考值
        alarmLog.setReferenceValue(quotaDTO.getReferenceValue());
        // 指标名称
        alarmLog.setQuotaName(quotaDTO.getName());

        // 获取告警规则信息
        List<Alarm> alarmRuleList = alarmMapper.selectList(new QueryWrapper<Alarm>().lambda().eq(Alarm::getSubject, quotaDTO.getSubject()));
        // 使用forEach遍历会出现脏数据, 想要结束forEach只能使用抛异常的方式
        for (Alarm alarmRule : alarmRuleList) {
            // 告警名称
            alarmLog.setAlarmName(alarmRule.getName());
            // 获取告警规则 告警级别
            alarmLog.setLevel(alarmRule.getLevel());

            // todo 推送告警信息到其它服务(比如说邮件服务, 短信服务通) 使用RestTemplate或者其它工具进行消息推送
            // 获取告警规则 告警钩子
            webhook = alarmRule.getWebhook();

            // 处理属性赋值
            boolean isDo = handleOperator(value, alarmRule.getThreshold(), alarmRule.getOperator(), alarmLog, devicesDTO);
            // 数据未处理进行下一次循环
            if (!isDo) {
                continue;
            }

            // 获取告警规则 告警沉默时间(分钟)
            cycle = alarmRule.getCycle();
            // 构建redis key
            String redisKey = String.format(RedisConstant.CYCLE_KEY, alarmLog.getDeviceId(), quotaDTO.getValueKey(), alarmLog.getLevel());
            // 如果数据在默认周期中, 则不存储并且不更新设备状态
            boolean cache = checkRedisCache(redisKey);
            if (cache) {
                log.info("{} 数据沉默", alarmLog);
                return;
            }

            // 添加告警日志
            saveAlarmLog2elasticsearch(alarmLog);
            // 更新设备状态
            devicesService.saveDeviceInfo(devicesDTO);
            // 缓存告警记录
            redisTemplate.boundValueOps(redisKey).set("flag", Long.parseLong(cycle.toString()), TimeUnit.MINUTES);
            log.info("<{}> 缓存 <{}> 成功", redisKey, alarmLog);

            return;
        }
    }

    /**
     * 获取是否已缓存
     *
     * @param redisKey 缓存key
     * @return true存在 false不存在
     */
    private boolean checkRedisCache(String redisKey) {
        String flag = redisTemplate.boundValueOps(redisKey).get();

        if (!StringUtils.isNullOrEmpty(flag)) {
            return true;
        }

        return false;
    }

    /**
     * 处理不同的数据类型
     *
     * @param value      数值
     * @param threshold  参考值
     * @param operator   运算符
     * @param alarmLog   实体类
     * @param devicesDTO 实体类
     * @return true数据处理 false数据未处理
     */
    private boolean handleOperator(String value, String threshold, String operator, AlarmLog alarmLog, DevicesDTO devicesDTO) {
        BigDecimal parseValue = new BigDecimal(value);
        BigDecimal parseThreshold = new BigDecimal(threshold);

        // 等于预期的值
        if (parseValue.compareTo(parseThreshold) == 0 && "=".equals(operator)) {
            alarmLog.setValue(value);
            alarmLog.setLevel(alarmLog.getLevel());
            alarmLog.setAlarm(1);

            devicesDTO.setAlarm(true);
            devicesDTO.setAlarmName(alarmLog.getAlarmName());
            devicesDTO.setLevel(alarmLog.getLevel() + 1);

            return true;
        }

        // 小于预期的值
        if (parseValue.compareTo(parseThreshold) == -1 && "<".equals(operator)) {
            alarmLog.setValue(value);
            alarmLog.setLevel(alarmLog.getLevel());
            alarmLog.setAlarm(1);

            devicesDTO.setAlarm(true);
            devicesDTO.setAlarmName(alarmLog.getAlarmName());
            devicesDTO.setLevel(alarmLog.getLevel() + 1);

            return true;
        }

        // 大于预期的值
        if (parseValue.compareTo(parseThreshold) == 1 && ">".equals(operator)) {
            alarmLog.setValue(value);
            alarmLog.setLevel(alarmLog.getLevel());
            alarmLog.setAlarm(1);

            devicesDTO.setAlarm(true);
            devicesDTO.setAlarmName(alarmLog.getAlarmName());
            devicesDTO.setLevel(alarmLog.getLevel() + 1);

            return true;
        }

        // 正常值
        if (alarmLog.getReferenceValue().contains("-") && value != null) {
            String[] split = alarmLog.getReferenceValue().split("-");
            String min = split[0];
            String max = split[1];

            BigDecimal minValue = new BigDecimal(min);
            BigDecimal maxValue = new BigDecimal(max);

            if (minValue.compareTo(parseValue) < 1 && maxValue.compareTo(parseValue) > -1) {
                // 在指定范围内的正常值
                alarmLog.setValue(value);
                alarmLog.setLevel(0);
                alarmLog.setAlarm(0);
                alarmLog.setAlarmName("normal");
                // 设置正常参数沉默周期为30分钟
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
     * 将指定对象存储到ElasticSearch中
     *
     * @param alarmLog com.example.chain.pojo.AlarmLog
     */
    private void saveAlarmLog2elasticsearch(AlarmLog alarmLog) {
        // elasticsearch存储告警日志
        IndexRequest alarm = new IndexRequest(ElasticSearchConstant.ES_ALARM_LOG_INDEX_NAME);
        try {
            String json = JsonUtil.serialize(alarmLog);
            Map map = JsonUtil.getByJson(json, Map.class);
            alarm.source(map);
            // 使用雪花算法生成唯一id
            alarm.id(IdWorker.getId() + "");
            alarm.type("_doc");

            restHighLevelClient.index(alarm, RequestOptions.DEFAULT);
        } catch (JsonProcessingException e) {
            throw new BusinessException("序列化异常");
        } catch (IOException e) {
            throw new BusinessException("设备添加失败");
        }

        log.info("{} 添加报警日志成功", alarmLog);
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

        // 条件查询
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 设备编号
        if (!Strings.isNullOrEmpty(alarmLogQuery.getDeviceId())) {
            // should 或者
            // must 必须
            // * 做字符模糊
            // ? 单字符模糊
            boolQueryBuilder.must(QueryBuilders.matchQuery("deviceId", alarmLogQuery.getDeviceId()));
        }
        // 告警名称
        if (alarmLogQuery.getAlarm() != null) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("level", alarmLogQuery.getAlarm()));
        }
        // 开始时间-结束时间
        if (!Strings.isNullOrEmpty(alarmLogQuery.getStart()) && !Strings.isNullOrEmpty(alarmLogQuery.getEnd())) {
            // 获取开始时间秒
            Long startTime = DateUtil.datetimeFormat2second(alarmLogQuery.getStart());
            // 获取结束时间秒
            Long endTime = DateUtil.datetimeFormat2second(alarmLogQuery.getEnd());

            boolQueryBuilder.filter(QueryBuilders.rangeQuery("alarmLogTime").gt(startTime).lt(endTime));
        }
        // 告警级别
        if (alarmLogQuery.getAlarm() != null) {
            boolQueryBuilder.must(QueryBuilders.termQuery("alarm", alarmLogQuery.getAlarm()));
        }

        sourceBuilder.query(boolQueryBuilder);
        // 分页
        sourceBuilder.from((alarmLogQuery.getPage().intValue() - 1) * alarmLogQuery.getPageSize().intValue());
        sourceBuilder.size(alarmLogQuery.getPageSize().intValue());
        sourceBuilder.trackTotalHits(true);

        // 排序
        // 新的记录排在前面
        sourceBuilder.sort("alarmLogTime", SortOrder.DESC);
        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse;
        try {
            searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException("查询失败");
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

        // 封装结果
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

        // 总记录数
        pageResult.setCounts(totalHits);
        // 当前页
        pageResult.setPage(alarmLogQuery.getPage());
        // 响应数据
        pageResult.setItems(alarmLogDTOList);
        // 每页记录数
        pageResult.setPageSize(alarmLogQuery.getPageSize());
        // 总页数
        pageResult.setPages(pages);

        return pageResult;

    }
}
