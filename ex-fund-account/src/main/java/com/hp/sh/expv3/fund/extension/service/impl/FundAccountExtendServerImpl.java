package com.hp.sh.expv3.fund.extension.service.impl;

import com.gitee.hupadev.base.api.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hp.sh.expv3.fund.extension.dao.FundAccountExtendMapper;
import com.hp.sh.expv3.fund.extension.service.FundAccountExtendService;
import com.hp.sh.expv3.fund.extension.vo.CapitalAccountVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2019/12/13
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class FundAccountExtendServerImpl implements FundAccountExtendService {
    @Autowired
    private FundAccountExtendMapper fundAccountExtendMapper;


    @Override
    public CapitalAccountVo getCapitalAccount(Long userId, String asset) {
        return fundAccountExtendMapper.getCapitalAccount(userId,asset);
    }

    @Override
    public PageResult<CapitalAccountVo> pageQueryAccountList(Long userId, String asset, Integer pageNo, Integer pageSize) {
        PageResult<CapitalAccountVo> pageResult=new PageResult<>();
        Map<String, Object> map=new HashMap<>();
        map.put("userId",userId);
        map.put("asset",asset);
        PageHelper.startPage(pageNo,pageSize);
        List<CapitalAccountVo> voList = fundAccountExtendMapper.queryList(map);
        PageInfo<CapitalAccountVo> info = new PageInfo<>(voList);
        pageResult.setList(voList);
        pageResult.setPageNo(info.getPageNum());
        pageResult.setPageCount(info.getPages());
        pageResult.setRowTotal(info.getTotal());
        return pageResult;
    }
}
