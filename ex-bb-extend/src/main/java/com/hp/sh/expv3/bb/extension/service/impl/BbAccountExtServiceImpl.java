package com.hp.sh.expv3.bb.extension.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.hp.sh.expv3.bb.extension.dao.BbAccountExtMapper;
import com.hp.sh.expv3.bb.extension.service.BbAccountExtService;
import com.hp.sh.expv3.bb.extension.vo.BbAccountExtVo;
import com.hp.sh.expv3.bb.extension.vo.BbAccountVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author BaiLiJun  on 2020/2/12
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BbAccountExtServiceImpl implements BbAccountExtService {

    @Autowired
    private BbAccountExtMapper bbAccountExtMapper;


    @Override
    public void createBBAccount(Long userId, String asset) {
        BbAccountVo bbAccountVo = new BbAccountVo();
        bbAccountVo.setUserId(userId);
        bbAccountVo.setAsset(asset);
        bbAccountVo.setBalance(BigDecimal.ZERO);
        bbAccountVo.setVersion(0L);
        long now = Instant.now().toEpochMilli();
        bbAccountVo.setModified(now);
        bbAccountVo.setCreated(now);
        bbAccountExtMapper.save(bbAccountVo);


    }

    @Override
    public Boolean bbAccountExist(Long userId, String asset) {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        BbAccountVo bbAccountVo = bbAccountExtMapper.queryOne(map);
        if (bbAccountVo != null) {
            return true;
        }
        return false;
    }

    @Override
    public BbAccountExtVo getBBAccount(Long userId, String asset) {
        BbAccountExtVo vo = null;
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("asset", asset);
        BbAccountVo bbAccountVo = bbAccountExtMapper.queryOne(map);
        if (bbAccountVo != null) {
            vo = new BbAccountExtVo();
            BeanUtils.copyProperties(bbAccountVo, vo);
            vo.setAvailable(bbAccountVo.getBalance());
        }
        return vo;
    }
}
