package com.hp.sh.expv3.pj.module.dataSummarization.action;

import com.gitee.hupadev.base.exceptions.BizException;
import com.hp.sh.expv3.bb.extension.api.BbAccountExtendApi;
import com.hp.sh.expv3.bb.extension.api.BbOrderTradeExtApi;
import com.hp.sh.expv3.commons.bean.PageResult;
import com.hp.sh.expv3.pj.constant.UserError;
import com.hp.sh.expv3.pj.module.admin.action.AdminBaseAction;
import com.hp.sh.expv3.pj.module.dataSummarization.constant.DataSummarizationError;
import com.hp.sh.expv3.pj.module.dataSummarization.vo.UserRegisterCountData;
import com.hp.sh.expv3.pj.module.dataSummarization.vo.UserRegisterCountVo;
import com.hp.sh.expv3.pj.module.externalOrder.vo.ExternalOrderListDataVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据汇总模块相关接口
 *
 * @author BaiLiJun  on 2020/6/11
 */
@RestController
@RequestMapping("/admin/dataSummarization")
public class DataSummarizationApiAction extends AdminBaseAction {
    private static final Logger logger = LoggerFactory.getLogger(DataSummarizationApiAction.class);

    @Value("${user.registerCount.apiAddr}")
    private String apiAddr;

    @Autowired
    private BbOrderTradeExtApi bbOrderTradeExtApi;

    @Autowired
    private BbAccountExtendApi bbAccountExtendApi;

    @RequestMapping("/query")
    public Map<String, Object> list(@RequestParam("asset") String asset, @RequestParam("symbol") String symbol,
                                    @RequestParam(value = "begin_time", required = false) String begin_time, @RequestParam(value = "end_time", required = false) String end_time) {
        logger.info("进入查询数据汇总接口，收到参数为：asset={},symbol={},begin_time={},end_time={}", asset, symbol, begin_time, end_time);
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol)) {
            throw new BizException(DataSummarizationError.PARAM_NULL_ERROR);
        }
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Long startTime = null;
        Long endTime = null;
        if (StringUtils.isEmpty(begin_time)) {
            Long[] startAndEndTime = getDefaultStartAndEndTime(begin_time);
            startTime = startAndEndTime[0];
            endTime = startAndEndTime[1];
        } else {
            startTime = LocalDateTime.parse(begin_time, dtf).toInstant(ZoneOffset.UTC).toEpochMilli();
            endTime = LocalDateTime.parse(end_time, dtf).toInstant(ZoneOffset.UTC).toEpochMilli();
        }

        Map<String, Object> map = new HashMap<>();
        LocalDateTime localDateTime = LocalDateTime.now(TimeZone.getTimeZone("UTC").toZoneId());
        Long currentTimeMillis = localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
        //http://192.168.0.38:8000/v1/http/user/query_user_count?
        // pass_word=exp2000&start_time=1587093130477&end_time=1587093150477&time=1587093150477
        String url = apiAddr + "&start_time=" + startTime + "&end_time=" + endTime + "&time=" + currentTimeMillis;
        logger.info("url={}", url);
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
            HttpEntity<String> entity = new HttpEntity<String>(headers);
            ResponseEntity<UserRegisterCountVo> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, UserRegisterCountVo.class);
            UserRegisterCountVo body = responseEntity.getBody();
            if (body.getCode() == 0) {
                UserRegisterCountData data = body.getData();
                String count = data.getCount();
                map.put("userRegisterCount", count);
            } else {
                throw new BizException(DataSummarizationError.CALLING_THIRD_INTERFACE_ERROR);
            }

            //每4小时用户成交量
            BigDecimal tradeNumberTotal = bbOrderTradeExtApi.queryTradeNumberTotalByTime(asset, symbol, startTime, endTime);
            map.put("every4HoursUserTradeNumber", tradeNumberTotal);

            //今日用户成交量
            startTime = localDateTime.toLocalDate().atStartOfDay(TimeZone.getTimeZone("UTC").toZoneId()).toInstant().toEpochMilli();
            BigDecimal todayTradeNumberTotal = bbOrderTradeExtApi.queryTradeNumberTotalByTime(asset, symbol, startTime, endTime);
            map.put("todayUserTradeNumber", todayTradeNumberTotal);

            //当前用户持有量

        } catch (Exception e) {
//            e.printStackTrace();
            logger.error("查询数据汇总接口报错，错误原因是：{}", e.getMessage());
        }

//        map.put("userRegisterCount", 2);

        map.put("currentUserHoldNumber", 300);
//        map.put("todayUserTradeNumber", 400);
        return map;
    }

    private Long[] getDefaultStartAndEndTime(String time) {
        if (StringUtils.isEmpty(time)) {
            LocalDateTime localDateTime = LocalDateTime.now(TimeZone.getTimeZone("UTC").toZoneId());
            LocalDateTime dateTime = localDateTime.minusHours(4L);
            Long startTime = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
            Long endTime = localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli();
            Long[] startAndEndTime = {startTime, endTime};
            return startAndEndTime;
        }
        return null;
    }

}
