package com.hp.sh.expv3.controller;

import com.hp.sh.expv3.service.INewAssetDefaultKlineDataService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.TimeZone;

/**
 * 新币种默认k线数据
 * 默认规则：给定一个新币种价格，
 * 自动生成该新币种2个月的kline数据（默认以两个月前该币种呈现3%-5%递增趋势）
 *
 * @author BaiLiJun  on 2020/6/4
 */
@RestController
public class NewAssetDefaultKlineDataController {
    private static final Logger logger = LoggerFactory.getLogger(NewAssetDefaultKlineDataController.class);

    @Autowired
    private INewAssetDefaultKlineDataService defaultKlineDataService;

    /**
     * @param lastPrice 价格（单位：USDT）
     *  @param time 时间戳
     */
    public void getDefaultKlineData(BigDecimal lastPrice,Long time,String asset,String symbol) {
        logger.info("进入新币种默认k线数据接口，收到参数为:lastPrice={},time={},asset={},symbol={}", lastPrice,time,asset,symbol);

        //如果没有给定成交价，则默认以 1/7 USDT 作为最新成交价
        if (lastPrice == null) {
            lastPrice = new BigDecimal("1").divide(new BigDecimal("7"), 4, RoundingMode.DOWN);
        }
        if(time==null){
            Instant instant = LocalDateTime.now(TimeZone.getTimeZone("UTC").toZoneId()).toInstant(ZoneOffset.UTC);
            time=instant.toEpochMilli();
        }

        if(StringUtils.isEmpty(asset)){
            asset="USDT";
        }
        if(StringUtils.isEmpty(symbol)){
            symbol="BYM_USDT";
        }

        defaultKlineDataService.getDefaultKlineData(lastPrice,time, asset, symbol);
    }



}
