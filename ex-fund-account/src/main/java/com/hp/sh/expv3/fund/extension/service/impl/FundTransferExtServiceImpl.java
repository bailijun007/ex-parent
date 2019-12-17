package com.hp.sh.expv3.fund.extension.service.impl;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.fund.extension.constant.FundTransferExtErrorCode;
import com.hp.sh.expv3.fund.extension.dao.FundTransferExtMapper;
import com.hp.sh.expv3.fund.extension.service.FundTransferExtService;
import com.hp.sh.expv3.fund.extension.vo.FundTransferExtVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * @author BaiLiJun  on 2019/12/16
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FundTransferExtServiceImpl implements FundTransferExtService {
    @Autowired
    private FundTransferExtMapper fundTransferExtMapper;

    @Override
    public List<FundTransferExtVo> queryHistory(Long userId, String asset, Long queryId, Integer pageSize, Integer pageStatus) {

        List<FundTransferExtVo> voList = fundTransferExtMapper.queryHistory(userId, asset, queryId, pageSize, pageStatus);
        if (CollectionUtils.isEmpty(voList)) {
            throw new ExException(FundTransferExtErrorCode.DATA_EMPTY);
        }
        for (FundTransferExtVo transferExtVo : voList) {
            Optional<FundTransferExtVo> vo = Optional.ofNullable(transferExtVo);
            transferExtVo.setCtime(vo.map(t -> t.getCreated().getTime()).orElse(null));
            int status = vo.filter(t -> t.getStatus() == 8).map(t -> t.getStatus()).orElse(2);
            if(status==2){
                transferExtVo.setStatus(status);
            }else {
                transferExtVo.setStatus(1);
            }
        }

        return voList;
    }
}