package com.hp.sh.expv3.bb.extension.service;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordExtVo;
import com.hp.sh.expv3.bb.extension.vo.BbAccountRecordVo;

import java.util.List;

/**
 * @author BaiLiJun  on 2020/2/14
 */
public interface BbAccountRecordExtService {

    List<BbAccountRecordVo> queryByIds(List<Long> refIds);

    List<BbAccountRecordExtVo> listBbAccountRecords(Long userId, String asset, Integer historyType, Integer tradeType, Long startDate, Long endDate, Integer pageSize);

    List<BbAccountRecordExtVo> listBbAccountRecordsByPage(Long userId, String asset, Integer historyType, Integer tradeType, Long lastId, Integer nextPage, Long startDate, Long endDate, Integer pageSize);

    PageResult<BbAccountRecordVo> queryHistory(Long userId, String asset, String startTime, String endTime, Integer pageNo, Integer pageSize);

}
