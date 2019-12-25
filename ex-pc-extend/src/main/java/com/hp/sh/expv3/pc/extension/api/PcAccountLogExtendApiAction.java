package com.hp.sh.expv3.pc.extension.api;

import com.gitee.hupadev.base.api.PageResult;
import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.pc.extension.constant.PcCommonErrorCode;
import com.hp.sh.expv3.pc.extension.service.PcAccountLogExtendService;
import com.hp.sh.expv3.pc.extension.service.PcAccountRecordExtendService;
import com.hp.sh.expv3.pc.extension.service.PcLiqRecordService;
import com.hp.sh.expv3.pc.extension.vo.PcAccountLogVo;
import com.hp.sh.expv3.pc.extension.vo.PcAccountRecordLogVo;
import com.hp.sh.expv3.pc.extension.vo.PcLiqRecordVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2019/12/25
 */
@RestController
public class PcAccountLogExtendApiAction implements PcAccountLogExtendApi {

    @Autowired
    private PcAccountRecordExtendService pcAccountRecordExtendService;

    @Autowired
    private PcAccountLogExtendService pcAccountLogExtendService;

    @Autowired
    private PcLiqRecordService pcLiqRecordService;

    @Override
    public PageResult<PcAccountRecordLogVo> findContractAccountList(Long userId, String asset, Integer tradeType, Integer historyType, String startDate, String endDate, Integer pageNo, Integer pageSize, String symbol) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || tradeType == null || null == userId || historyType == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }

        if (tradeType == 0) {
            tradeType = null;
        }
        PageResult<PcAccountRecordLogVo> result = new PageResult<PcAccountRecordLogVo>();
        List<PcAccountRecordLogVo> list = new ArrayList<>();
        List<PcAccountLogVo> pcAccountLogList = pcAccountLogExtendService.getPcAccountLogList(userId, asset, tradeType, historyType, startDate, endDate, symbol);
        if (!CollectionUtils.isEmpty(pcAccountLogList)) {
            for (PcAccountLogVo pcAccountLogVo : pcAccountLogList) {
                PcAccountRecordLogVo recordLogVo = new PcAccountRecordLogVo();
                BeanUtils.copyProperties(pcAccountLogVo, recordLogVo);

                if (tradeType == 11 || tradeType == 12) {
                    PcLiqRecordVo pcLiqRecordVo=pcLiqRecordService.getPcLiqRecord(pcAccountLogVo.getRefId(),pcAccountLogVo.getAsset(),pcAccountLogVo.getSymbol(),pcAccountLogVo.getUserId(),pcAccountLogVo.getTime());
                }


                list.add(recordLogVo);
            }



            List<PcAccountRecordLogVo> voList = list.stream().skip(pageSize * (pageNo - 1)).limit(pageSize).collect(Collectors.toList());
            result.setList(voList);
        }
        int rowTotal = pcAccountLogList.size();
        result.setPageNo(pageNo);
        result.setRowTotal(Long.parseLong(rowTotal + ""));
        result.setPageCount(rowTotal % pageSize == 0 ? rowTotal / pageSize : rowTotal / pageSize + 1);
        return result;
    }


}
