package com.hp.sh.expv3.bb.trade.service.impl;

import com.hp.sh.expv3.bb.trade.dao.BbMatchExtMapper;
import com.hp.sh.expv3.bb.trade.pojo.BbMatchExtVo;
import com.hp.sh.expv3.bb.trade.service.BbMatchExtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/31
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BbMatchExtServiceImpl implements BbMatchExtService {

    @Autowired
    private BbMatchExtMapper bbMatchExtMapper;


    @Override
    public void batchSave(List<BbMatchExtVo> trades){
        bbMatchExtMapper.batchSave(trades);
    }

}
