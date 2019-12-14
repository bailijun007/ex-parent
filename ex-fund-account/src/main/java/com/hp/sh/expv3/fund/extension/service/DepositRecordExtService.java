package com.hp.sh.expv3.fund.extension.service;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.extension.constant.CapitalAccountErrorCode;
import com.hp.sh.expv3.fund.extension.dao.DepositRecordExtMapper;
import com.hp.sh.expv3.fund.extension.dao.FundAccountExtendMapper;
import com.hp.sh.expv3.fund.extension.vo.DepositRecordHistoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * 充值记录扩展服务
 *
 * @author BaiLiJun  on 2019/12/14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DepositRecordExtService {
    @Autowired
    private FundAccountExtendMapper fundAccountExtendMapper;

    @Autowired
    private DepositRecordExtMapper depositRecordExtMapper;

    public List<DepositRecordHistoryVo> queryHistory(Long userId, String asset, Long queryId, Integer pageSize) {

        List<DepositRecordHistoryVo> list=depositRecordExtMapper.queryHistory(userId,asset,queryId,pageSize);
        if (CollectionUtils.isEmpty(list)){
            new ExException(CapitalAccountErrorCode.DATA_EMPTY);
        }
        for (DepositRecordHistoryVo historyVo : list) {
            historyVo.setMtime(historyVo.getModified().getTime());
            historyVo.setDepositTime(historyVo.getPayTime().getTime());
        }

        return list;
    }
}
