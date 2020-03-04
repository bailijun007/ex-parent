package com.hp.sh.expv3.fund.extension.service.impl;

import com.gitee.hupadev.base.api.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hp.sh.expv3.fund.extension.dao.DepositRecordExtMapper;
import com.hp.sh.expv3.fund.extension.dao.FundAccountExtendMapper;
import com.hp.sh.expv3.fund.extension.service.DepositRecordExtService;
import com.hp.sh.expv3.fund.extension.vo.DepositRecordHistoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 充值记录扩展服务
 *
 * @author BaiLiJun  on 2019/12/14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DepositRecordExtServiceImpl implements DepositRecordExtService {
    @Autowired
    private FundAccountExtendMapper fundAccountExtendMapper;

    @Autowired
    private DepositRecordExtMapper depositRecordExtMapper;

    @Override
    public List<DepositRecordHistoryVo> queryHistory(Long userId, String asset, Long queryId, Integer pageSize, Integer pageStatus) {
        if (pageStatus == null) {
            pageStatus = 1;
        }
        List<DepositRecordHistoryVo> list = depositRecordExtMapper.queryHistory(userId, asset, queryId, pageSize, pageStatus);
        if (!CollectionUtils.isEmpty(list)) {
            for (DepositRecordHistoryVo historyVo : list) {
                Optional<DepositRecordHistoryVo> vo = Optional.ofNullable(historyVo);
                historyVo.setMtime(vo.map(d -> d.getModified()).orElse(null));
                historyVo.setDepositTime(vo.map(d -> d.getCtime()).orElse(null));
            }
        }

        return list;
    }

    @Override
    public PageResult<DepositRecordHistoryVo> pageQueryDepositRecordHistory(Long userId, String asset, Integer pageNo, Integer pageSize) {
        PageResult<DepositRecordHistoryVo> pageResult=new PageResult<>();
        PageHelper.startPage(pageNo,pageSize);
        List<DepositRecordHistoryVo> list = depositRecordExtMapper.queryByUserIdAndAsset(userId,asset);
       if(!CollectionUtils.isEmpty(list)){
           for (DepositRecordHistoryVo historyVo : list) {
               Optional<DepositRecordHistoryVo> vo = Optional.ofNullable(historyVo);
               historyVo.setMtime(vo.map(DepositRecordHistoryVo::getModified).orElse(null));
           }
       }
        PageInfo<DepositRecordHistoryVo> info = new PageInfo<>(list);
        pageResult.setList(list);
        pageResult.setPageNo(info.getPageNum());
        pageResult.setPageCount(info.getPages());
        pageResult.setRowTotal(info.getTotal());
        return pageResult;
    }
}
