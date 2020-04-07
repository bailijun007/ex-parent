package com.hp.sh.expv3.bb.grab3rdData.service.impl;

import com.hp.sh.expv3.bb.grab3rdData.dao.PcMatchExtMapper;
import com.hp.sh.expv3.bb.grab3rdData.pojo.PcMatchExtVo;
import com.hp.sh.expv3.bb.grab3rdData.service.PcMatchExtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/3/31
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PcMatchExtServiceImpl implements PcMatchExtService {

    @Autowired
    private PcMatchExtMapper bbMatchExtMapper;


    @Override
    public void batchSave(List<PcMatchExtVo> trades, String table){

        bbMatchExtMapper.batchSave(trades,table);

    }

}
