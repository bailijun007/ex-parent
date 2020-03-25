package com.hp.sh.expv3.bb.extension.service.impl;

import com.hp.sh.expv3.bb.extension.dao.BbAccountLogExtMapper;
import com.hp.sh.expv3.bb.extension.service.BbAccountLogExtService;
import com.hp.sh.expv3.bb.extension.vo.BbAccountLogExtVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @author BaiLiJun  on 2020/3/24
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BbAccountLogExtServiceImpl implements BbAccountLogExtService {

    @Autowired
    private BbAccountLogExtMapper bbAccountLogExtMapper;

    @Override
    public List<BbAccountLogExtVo> listBbAccountLogs(Long userId, String asset, String symbol, Integer tradeType, Long startDate, Long endDate, Integer nextPage, Integer lastOrderId, Integer pageSize) {

        return null;
    }
}
