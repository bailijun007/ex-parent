package com.hp.sh.expv3.fund.extension.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.hp.sh.expv3.fund.extension.constants.WithdrawalStatus;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalRecordByAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.gitee.hupadev.base.api.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hp.sh.expv3.fund.constant.ApprovalStatus;
import com.hp.sh.expv3.fund.constant.PaymentStatus;
import com.hp.sh.expv3.fund.constant.SynchStatus;
import com.hp.sh.expv3.fund.extension.dao.WithdrawalRecordExtMapper;
import com.hp.sh.expv3.fund.extension.service.WithdrawalRecordExtService;
import com.hp.sh.expv3.fund.extension.vo.WithdrawalRecordVo;

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
        return withdrawalRecordExtMapper.getFrozenCapital(userId, asset, SynchStatus.SYNCH, PaymentStatus.PENDING);
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
            //1.审核中,2.审核通过,3.失败
            if(historyVo.getStatus()== ApprovalStatus.APPROVED &&historyVo.getPayStatus()== PaymentStatus.SUCCESS){
                historyVo.setStatus(WithdrawalStatus.SUCCESS);
            }else if(historyVo.getStatus()== ApprovalStatus.APPROVED &&historyVo.getPayStatus()== PaymentStatus.FAIL){
              //审核通过，支付失败 状态也是失败
                historyVo.setStatus(WithdrawalStatus.FAIL);
            }else{
                //审核中
                historyVo.setStatus(WithdrawalStatus.IN_APPROVAL);
            }
        }

        return list;
    }

    @Override
    public WithdrawalRecordVo queryLastHistory(Long userId, String asset) {
        WithdrawalRecordVo vo = withdrawalRecordExtMapper.queryLastHistory(userId, asset);
        if (vo != null) {
            Optional<WithdrawalRecordVo> recordVo = Optional.ofNullable(vo);
            vo.setCtime(recordVo.map(WithdrawalRecordVo::getCreated).orElse(null));
            vo.setWithdrawTime(recordVo.map(WithdrawalRecordVo::getCreated).orElse(null));
        }

        return vo;
    }

    @Override
    public List<WithdrawalRecordVo> findWithdrawalRecordList(Long userId, String asset, Long startTime, Long endTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("payStatus", PaymentStatus.FAIL);
        map.put("createdBegin", startTime);
        map.put("createdEnd", endTime);
        List<WithdrawalRecordVo> recordVos = withdrawalRecordExtMapper.queryHistoryByTime(map);
        if (!CollectionUtils.isEmpty(recordVos)) {
            for (WithdrawalRecordVo vo : recordVos) {
                Optional<WithdrawalRecordVo> recordVo = Optional.ofNullable(vo);
                vo.setCtime(recordVo.map(WithdrawalRecordVo::getCreated).orElse(null));
                vo.setWithdrawTime(recordVo.map(WithdrawalRecordVo::getCreated).orElse(null));
            }
        }
        return recordVos;
    }

    @Override
    public PageResult<WithdrawalRecordVo> pageQueryHistory(Long userId, String asset, Integer pageNo, Integer pageSize, Long startTime, Long endTime, Integer approvalStatus) {
        PageResult<WithdrawalRecordVo> pageResult = new PageResult<>();
        PageHelper.startPage(pageNo, pageSize);
//        List<WithdrawalRecordVo> list = withdrawalRecordExtMapper.queryByUserIdAndAsset(userId, asset);
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("approvalStatus", approvalStatus);
        map.put("modifiedBegin", startTime);
        map.put("modifiedEnd", endTime);
        List<WithdrawalRecordVo> list = withdrawalRecordExtMapper.listWithdrawalRecords(map);

        if (!CollectionUtils.isEmpty(list)) {
            for (WithdrawalRecordVo vo : list) {
                Optional<WithdrawalRecordVo> recordVo = Optional.ofNullable(vo);
                vo.setWithdrawTime(recordVo.map(WithdrawalRecordVo::getCreated).orElse(null));
            }
        }

        PageInfo<WithdrawalRecordVo> info = new PageInfo<>(list);
        pageResult.setList(list);
        pageResult.setPageNo(info.getPageNum());
        pageResult.setPageCount(info.getPages());
        pageResult.setRowTotal(info.getTotal());
        return pageResult;
    }

    @Override
    public PageResult<WithdrawalRecordByAdmin> queryHistoryByAdmin(Long userId, String asset, Integer status,Integer payStatus, Integer pageNo, Integer pageSize) {
        PageHelper.startPage(pageNo,pageSize);
        PageResult<WithdrawalRecordByAdmin> pageResult=new PageResult<>();
        List<WithdrawalRecordByAdmin> list = withdrawalRecordExtMapper.queryHistoryByAdmin(userId,asset ,status,payStatus);
         PageInfo<WithdrawalRecordByAdmin> info = new PageInfo<>(list);
        pageResult.setList(list);
        pageResult.setRowTotal(info.getTotal());
        pageResult.setPageNo(info.getPageNum());
        pageResult.setPageCount(info.getPages());
        return pageResult;
    }

}
