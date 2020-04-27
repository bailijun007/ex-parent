package com.hp.sh.expv3.fund.extension.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.hp.sh.expv3.fund.transfer.constant.FundTransferStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.gitee.hupadev.base.api.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hp.sh.expv3.fund.extension.dao.FundTransferExtMapper;
import com.hp.sh.expv3.fund.extension.service.FundTransferExtService;
import com.hp.sh.expv3.fund.extension.vo.FundTransferExtVo;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FundTransferExtServiceImpl implements FundTransferExtService {
    @Autowired
    private FundTransferExtMapper fundTransferExtMapper;

    @Override
    public PageResult<FundTransferExtVo> pageQueryHistory(Long userId, String asset, Long queryId, Integer pageSize, Integer pageStatus) {
        PageResult<FundTransferExtVo> pageResult = new PageResult<>();
        List<FundTransferExtVo> voList = fundTransferExtMapper.queryHistory(userId, asset, queryId, pageSize, pageStatus);
        if (CollectionUtils.isEmpty(voList)) {
            return null;
        }
        for (FundTransferExtVo transferExtVo : voList) {
            Optional<FundTransferExtVo> vo = Optional.ofNullable(transferExtVo);
            transferExtVo.setCtime(vo.map(t -> t.getCreated()).orElse(null));
            if (transferExtVo.getStatus() == FundTransferStatus.STATUS_SUCCESS) {
                transferExtVo.setStatus(1);
            } else if (transferExtVo.getStatus() == FundTransferStatus.STATUS_FAIL) {
                transferExtVo.setStatus(2);
            }else {
                transferExtVo.setStatus(3);
            }
        }
        Long count = fundTransferExtMapper.queryCount(userId, asset, queryId, pageStatus);

        pageResult.setRowTotal(count);
        pageResult.setList(voList);
        return pageResult;
    }

    @Override
    public PageResult<FundTransferExtVo> queryAllUserHistory(Long userId, String asset, Integer status,Integer pageNo, Integer pageSize) {
        PageResult<FundTransferExtVo> pageResult = new PageResult<>();
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        map.put("status", status);
        PageHelper.startPage(pageNo, pageSize);
        List<FundTransferExtVo> voList = fundTransferExtMapper.queryList(map);
        if (!CollectionUtils.isEmpty(voList)) {
            for (FundTransferExtVo transferExtVo : voList) {
                Optional<FundTransferExtVo> vo = Optional.ofNullable(transferExtVo);
                transferExtVo.setCtime(vo.map(t -> t.getCreated()).orElse(null));
                //状态(15=>成功，16=>失败)
                transferExtVo.setStatus(transferExtVo.getStatus() == FundTransferStatus.STATUS_SUCCESS ? 1 : 2);
            }
        }

        PageInfo<FundTransferExtVo> info = new PageInfo<>(voList);
        pageResult.setList(voList);
        pageResult.setPageNo(info.getPageNum());
        pageResult.setPageCount(info.getPages());
        pageResult.setRowTotal(info.getTotal());
        return pageResult;
    }
}
