package com.hp.sh.expv3.bb.extension.service.impl;

import com.gitee.hupadev.base.api.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hp.sh.expv3.bb.extension.dao.BbOrderExtMapper;
import com.hp.sh.expv3.bb.extension.service.BbOrderExtService;
import com.hp.sh.expv3.bb.extension.vo.BbOrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author BaiLiJun  on 2020/2/14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BbOrderExtServiceImpl implements BbOrderExtService {

    @Autowired
    private BbOrderExtMapper bbOrderExtMapper;

    @Override
    public PageResult<BbOrderVo> queryAllBbOrederHistory(Long userId, String asset, Integer pageNo, Integer pageSize) {
        PageResult<BbOrderVo> pageResult=new PageResult<>();
        PageHelper.startPage(pageNo,pageSize);
        Map<String, Object> map=new HashMap<>();
        map.put("userId",userId);
        map.put("asset",asset);
         List<BbOrderVo> list = bbOrderExtMapper.queryList(map);
         PageInfo<BbOrderVo> info = new PageInfo<>(list);
        pageResult.setPageNo(info.getPageNum());
        pageResult.setPageCount(info.getPages());
        pageResult.setRowTotal(info.getTotal());
        pageResult.setList(list);
        return pageResult;
    }
}
