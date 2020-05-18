package com.hp.sh.expv3.bb.extension.api;

import com.hp.sh.expv3.bb.extension.error.BbExtCommonErrorCode;
import com.hp.sh.expv3.bb.extension.service.IQueryKlineDataByThirdDataService;
import com.hp.sh.expv3.commons.exception.ExException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiLiJun  on 2020/5/6
 */
@RestController
public class QueryKlineDataByThirdDataController implements QueryKlineDataByThirdDataApi {

    @Autowired
    private IQueryKlineDataByThirdDataService queryKlineDataByThirdDataService;

    @Override
    public void queryKlineDataByThirdData(@RequestParam("tableName") String tableName, @RequestParam("klineType") Integer klineType,
                                          @RequestParam(name = "asset") String asset, @RequestParam("pair") String pair, @RequestParam("interval") String interval, @RequestParam("openTimeBegin") Long openTimeBegin, @RequestParam("openTimeEnd") Long openTimeEnd) {
        if (StringUtils.isEmpty(tableName) || klineType == null || StringUtils.isEmpty(asset) || StringUtils.isEmpty(pair) || StringUtils.isEmpty(interval) || null == openTimeBegin || null == openTimeEnd) {
            throw new ExException(BbExtCommonErrorCode.PARAM_EMPTY);
        }
        queryKlineDataByThirdDataService.queryKlineDataByThirdData(tableName, klineType, asset, pair, interval, openTimeBegin, openTimeEnd);
    }

}
