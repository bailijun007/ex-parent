package com.hp.sh.expv3.fund.extension.service.impl;

import com.gitee.hupadev.base.api.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.extension.dao.WithdrawalRecordExtMapper;
import com.hp.sh.expv3.fund.extension.error.FundAccountExtError;
import com.hp.sh.expv3.fund.extension.service.WithdrawalRecordExtService;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalRecordVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 体现记录扩展服务
 *
 * @author BaiLiJun  on 2019/12/14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WithdrawalRecordExtServerImpl implements WithdrawalRecordExtService {
    @Autowired
    private WithdrawalRecordExtMapper withdrawalRecordExtMapper;

    @Override
    public BigDecimal getFrozenCapital(Long userId, String asset) {
        return withdrawalRecordExtMapper.getFrozenCapital(userId, asset);
    }

    @Override
    public List<WithdrawalRecordVo> queryHistory(Long userId, String asset, Long queryId, Integer pageSize, Integer pageStatus) {
        List<WithdrawalRecordVo> list = withdrawalRecordExtMapper.queryHistory(userId, asset, queryId, pageSize, pageStatus);

        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        for (WithdrawalRecordVo historyVo : list) {
            Optional<WithdrawalRecordVo> vo = Optional.ofNullable(historyVo);
            historyVo.setCtime(vo.map(d -> d.getCreated()).orElse(null));
            historyVo.setWithdrawTime(vo.map(d -> d.getCreated()).orElse(null));
        }

        return list;
    }

    @Override
    public WithdrawalRecordVo queryLastHistory(Long userId, String asset) {
        WithdrawalRecordVo vo = withdrawalRecordExtMapper.queryLastHistory(userId, asset);

        return vo;
    }

    @Override
    public List<WithdrawalRecordVo> findWithdrawalRecordList(Long userId, String asset, Long startTime, Long endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("payStatus", 2);
        map.put("createdBegin", startTime);
        map.put("createdEnd", endTime);
        List<WithdrawalRecordVo> recordVos = withdrawalRecordExtMapper.queryHistoryByTime(map);
        return recordVos;
    }

    @Override
    public PageResult<WithdrawalRecordVo> pageQueryHistory(Long userId, String asset, Integer pageNo, Integer pageSize) {
        PageResult<WithdrawalRecordVo> pageResult = new PageResult<>();
        PageHelper.startPage(pageNo, pageSize);
        List<WithdrawalRecordVo> list = withdrawalRecordExtMapper.queryByUserIdAndAsset(userId, asset);
        PageInfo<WithdrawalRecordVo> info = new PageInfo<>(list);
        pageResult.setList(list);
        pageResult.setPageNo(info.getPageNum());
        pageResult.setPageCount(info.getPages());
        pageResult.setRowTotal(info.getTotal());
        return pageResult;
    }

}
