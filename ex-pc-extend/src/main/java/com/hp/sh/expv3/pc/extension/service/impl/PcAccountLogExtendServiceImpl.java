package com.hp.sh.expv3.pc.extension.service.impl;

import com.hp.sh.expv3.pc.extension.dao.PcAccountLogDAO;
import com.hp.sh.expv3.pc.extension.service.PcAccountLogExtendService;
import com.hp.sh.expv3.pc.extension.util.DateUtil;
import com.hp.sh.expv3.pc.extension.vo.PcAccountLogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/25
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PcAccountLogExtendServiceImpl implements PcAccountLogExtendService {
    @Autowired
    private PcAccountLogDAO pcAccountLogDAO;


    @Override
    public List<PcAccountLogVo> getPcAccountLogList(Long userId, String asset, Integer tradeType, Integer historyType, String startDate, String endDate, String symbol) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("type", tradeType);
        map.put("symbol", symbol);
        try {
            if (historyType == 1) {
                String beginDate = DateUtil.findLatelyDate(-2);
                Date begin = dateFormat.parse(beginDate);
                map.put("timeBegin", begin.getTime());
            } else if (historyType == 2) {
                Date begin = dateFormat.parse(startDate);
                Date end = dateFormat.parse(endDate);
                map.put("timeBegin", begin.getTime());
                map.put("timeEnd", end.getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        List<PcAccountLogVo> list = pcAccountLogDAO.queryList(map);
        return list;
    }
}
