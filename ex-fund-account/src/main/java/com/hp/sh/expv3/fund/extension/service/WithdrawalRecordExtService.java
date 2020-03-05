package com.hp.sh.expv3.fund.extension.service;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.fund.extension.dao.WithdrawalRecordExtMapper;
import com.hp.sh.expv3.fund.extension.vo.CapitalAccountVo;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalRecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 体现记录扩展服务
 *
 * @author BaiLiJun  on 2019/12/14
 */
public interface WithdrawalRecordExtService {

    public BigDecimal getFrozenCapital(Long userId, String asset,int approvalStatus,int payStatus) ;

    List<WithdrawalRecordVo> queryHistory(Long userId, String asset, Long queryId, Integer pageSize, Integer pageStatus);

    WithdrawalRecordVo queryLastHistory(Long userId, String asset);


    List<WithdrawalRecordVo> findWithdrawalRecordList(Long userId, String asset, Long startTime,Long endTime);

    PageResult<WithdrawalRecordVo> pageQueryHistory(Long userId, String asset, Integer pageNo, Integer pageSize, Long startTime, Long endTime,Integer approvalStatus);
}
