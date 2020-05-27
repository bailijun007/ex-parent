package com.hp.sh.expv3.bb.trade.service.impl;

import com.hp.sh.expv3.bb.extension.vo.BbTradeVo;
import com.hp.sh.expv3.bb.trade.dao.BbMatchExtMapper;
import com.hp.sh.expv3.bb.trade.service.BbTradeExtendService;
import com.hp.sh.expv3.bb.trade.util.CommonDateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/5/27
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BbTradeExtendServiceImpl implements BbTradeExtendService {
  @Autowired
   private BbMatchExtMapper pcTradeDAO;

    @Override
    public List<BbTradeVo> selectTradeListByTimeInterval(String asset, String symbol, Long startTime, Long endTime) {
        return pcTradeDAO.selectTradeListByTimeInterval(asset,symbol,startTime,endTime);
    }

    @Override
    public List<BbTradeVo> selectTradeListByUser(Long userId, String asset, String symbol, Long startTime, Long endTime) {
        return pcTradeDAO.selectTradeListByUser(userId,asset,symbol,startTime,endTime);
    }

    @Override
    public List<BbTradeVo> queryTradeList(Long userId, String asset, String symbol, Integer count,String startTime,String endTime) {
        Long tradeTimeBegin = CommonDateUtils.stringToTimestamp(startTime);
        Long tradeTimeEnd = CommonDateUtils.stringToTimestamp(endTime);
        return pcTradeDAO.queryTradeList(userId,asset,symbol,count,tradeTimeBegin,tradeTimeEnd);
    }

    @Override
    public BbTradeVo queryLastTradeByLtTime(String asset, String symbol, Long endTime) {
        LocalDate localDate = LocalDate.now();
        Long startTime = CommonDateUtils.localDateToTimestamp(localDate);
        return pcTradeDAO.queryLastTradeByLtTime(asset,symbol,startTime,endTime);
    }

    @Override
    public List<BbTradeVo> queryTradeResult(String asset, String symbol, Integer count,String startTime,String endTime) {
        Map<String, Object> map=new HashMap<>();
        map.put("asset",asset);
        map.put("symbol",symbol);
        map.put("orderBy","trade_time");
        map.put("limit",count);
        map.put("tradeTimeBegin", CommonDateUtils.stringToTimestamp(startTime));
        map.put("tradeTimeEnd",CommonDateUtils.stringToTimestamp(endTime));
        List<BbTradeVo> list = pcTradeDAO.queryList(map);
        return list;
    }


}
