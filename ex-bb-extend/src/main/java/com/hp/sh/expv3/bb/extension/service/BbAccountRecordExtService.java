package com.hp.sh.expv3.bb.extension.service;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordVo;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/2/14
 */
public interface BbAccountRecordExtService {
    PageResult<BbAccountRecordVo> queryHistory(Long userId, String asset, Integer pageNo, Integer pageSize);

    List<BbAccountRecordVo> queryByIds(List<Long> refIds);
}
