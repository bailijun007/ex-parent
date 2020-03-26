package com.hp.sh.expv3.bb.extension.service.impl;

import com.gitee.hupadev.base.api.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hp.sh.expv3.bb.extension.dao.BbAccountRecordExtMapper;
import com.hp.sh.expv3.bb.extension.service.BbAccountRecordExtService;
import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordVo;
import org.apache.poi.ss.formula.functions.T;
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
public class BbAccountRecordExtServiceImpl implements BbAccountRecordExtService {

    @Autowired
    private BbAccountRecordExtMapper bbAccountRecordExtMapper;


    @Override
    public PageResult<BbAccountRecordVo> queryHistory(Long userId, String asset, Integer pageNo, Integer pageSize) {
        PageResult<BbAccountRecordVo> pageResult=new PageResult<>();
        PageHelper.startPage(pageNo,pageSize);
        Map<String,Object> map=new HashMap<>();
        map.put("userId",userId);
        map.put("asset",asset);
        List<BbAccountRecordVo> list = bbAccountRecordExtMapper.queryList(map);
         PageInfo<BbAccountRecordVo> info = new PageInfo<BbAccountRecordVo>(list);
        pageResult.setList(list);
        pageResult.setPageNo(info.getPageNum());
        pageResult.setPageCount(info.getPages());
        pageResult.setRowTotal(info.getTotal());
         return pageResult;
    }

    @Override
    public List<BbAccountRecordVo> queryByIds(List<Long> refIds) {
        return bbAccountRecordExtMapper.queryByIds(refIds);

    }
}
