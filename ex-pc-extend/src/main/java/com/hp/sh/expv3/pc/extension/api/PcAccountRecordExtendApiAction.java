package com.hp.sh.expv3.pc.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.pc.extension.service.PcAccountRecordExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcAccountRecordExtVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author BaiLiJun  on 2019/12/24
 */
@RestController
public class PcAccountRecordExtendApiAction implements PcAccountRecordExtendApi {
    @Autowired
    private PcAccountRecordExtendService pcAccountRecordExtendService;

    @Override
    public PageResult<PcAccountRecordExtVo> findContractAccountList(Long userId, String asset, Integer tradeType, Integer historyType, String startDate, String endDate, Integer pageNo, Integer pageSize) {
        return null;
    }
}
