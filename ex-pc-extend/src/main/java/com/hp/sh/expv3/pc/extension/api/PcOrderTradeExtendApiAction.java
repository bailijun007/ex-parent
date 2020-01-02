package com.hp.sh.expv3.pc.extension.api;

import com.hp.sh.expv3.commons.exception.ExException;
import com.hp.sh.expv3.dev.CrossDB;
import com.hp.sh.expv3.pc.extension.error.PcCommonErrorCode;
import com.hp.sh.expv3.pc.extension.service.PcOrderTradeExtendService;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeDetailVo;
import com.hp.sh.expv3.pc.extension.vo.PcOrderTradeVo;
import com.hp.sh.expv3.pc.extension.vo.UserOrderVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author BaiLiJun  on 2019/12/24
 */
@RestController
public class PcOrderTradeExtendApiAction implements PcOrderTradeExtendApi {
    @Autowired
    private PcOrderTradeExtendService pcOrderTradeService;




    /**
     * 查询成交记录
     * @param asset 资产
     * @param symbol    交易对
     * @param gtTradeId  请求大于trade_id的数据
     * @param ltTradeId 请求小于trade_id的数据
     * @param count 返回条数
     * @return
     */
    @Override
    public List<PcOrderTradeDetailVo> queryTradeRecord(String asset, String symbol, Long gtTradeId, Long ltTradeId, Integer count) {
        checkParam(asset, symbol, count);
        //如果同时传了gtTradeId和ltTradeId 则以gtOrderId为查询条件，同时不传，则查全部
        if (gtTradeId != null && ltTradeId != null) {
            ltTradeId = null;
        }
        List<PcOrderTradeDetailVo> result = new ArrayList<>();
        List<String> assetList = Arrays.asList(asset.split(",")).stream().map(s -> s.trim()).collect(Collectors.toList());
        List<String> symbolList = Arrays.asList(symbol.split(",")).stream().map(s -> s.trim()).collect(Collectors.toList());
        List<PcOrderTradeVo> voList = pcOrderTradeService.queryTradeRecords(assetList, symbolList, gtTradeId, ltTradeId, count);
        //封装结果集
        this.toResult(result, voList);

        return result;
    }

    /**
     * 查询最新成交记录
     * @param asset 资产
     * @param symbol  交易对
     * @param count 返回条数，最大100条
     * @return
     */
    @Override
    public List<PcOrderTradeDetailVo> queryLastTradeRecord(String asset, String symbol, Integer count) {
        checkParam(asset, symbol, count);

        List<PcOrderTradeDetailVo> result = new ArrayList<>();
        List<PcOrderTradeVo> voList = pcOrderTradeService.queryLastTradeRecord(asset, symbol, count);
        //封装结果集
        this.toResult(result, voList);

        return result;
    }

    private void checkParam(String asset, String symbol, Integer count) {
        if (StringUtils.isEmpty(asset) || StringUtils.isEmpty(symbol) || count == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        if(count>100){
            throw new ExException(PcCommonErrorCode.MORE_THAN_MAX_ROW);
        }

    }


    /**
     * 查小于某个时间点的最大的一条记录
     *
     * @param asset    资产
     * @param symbol   合约交易品种
     * @param statTime 成交时间
     * @return
     */
    @CrossDB
    @Override
    public PcOrderTradeDetailVo selectLessTimeTrade(String asset, String symbol, Long statTime) {
        PcOrderTradeVo vo = pcOrderTradeService.selectLessTimeTrade(asset, symbol, statTime);
        PcOrderTradeDetailVo detailVo = new PcOrderTradeDetailVo();
        if (vo != null) {
            BeanUtils.copyProperties(vo, detailVo);
            detailVo.setAsset(vo.getAsset());
            detailVo.setSymbol(vo.getSymbol());
            detailVo.setQty(vo.getVolume());
            detailVo.setAmt(vo.getPrice().multiply(vo.getVolume()));
        }
        return detailVo;
    }

    /**
     * 查某个用户的所有成交记录
     *
     * @param asset  资产
     * @param symbol 合约交易品种
     * @param userId 用户id
     * @return
     */
    @Override
    public List<PcOrderTradeDetailVo> selectAllTradeListByUser(String asset, String symbol, Long userId) {
        if (userId == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        List<PcOrderTradeDetailVo> result = new ArrayList<>();
        List<PcOrderTradeVo> voList = pcOrderTradeService.selectAllTradeListByUser(asset, symbol, userId);
        this.toResult(result,voList);
        return result;
    }

    @CrossDB
    @Override
    public List<PcOrderTradeDetailVo> selectTradeListByTimeInterval(String asset, String symbol, Long statTime, Long endTime) {
        List<PcOrderTradeDetailVo> result = new ArrayList<>();
        List<PcOrderTradeVo> voList = pcOrderTradeService.selectTradeListByTimeInterval(asset, symbol, statTime,endTime,null);
        this.toResult(result,voList);
        return result;
    }

    @CrossDB
    @Override
    public List<PcOrderTradeDetailVo> selectTradeListByUser(String asset, String symbol, Long userId, Long statTime, Long endTime) {
        if (userId == null) {
            throw new ExException(PcCommonErrorCode.PARAM_EMPTY);
        }
        List<PcOrderTradeDetailVo> result = new ArrayList<>();
        List<PcOrderTradeVo> voList = pcOrderTradeService.selectTradeListByTimeInterval(asset, symbol, statTime,endTime,userId);
        this.toResult(result,voList);
        return result;
    }


    //封装结果集
    private void toResult(List<PcOrderTradeDetailVo> result, List<PcOrderTradeVo> voList) {
        if (!CollectionUtils.isEmpty(voList)) {
            for (PcOrderTradeVo pcOrderTradeVo : voList) {
                PcOrderTradeDetailVo detailVo = new PcOrderTradeDetailVo();
                BeanUtils.copyProperties(pcOrderTradeVo, detailVo);
                detailVo.setAsset(pcOrderTradeVo.getAsset());
                detailVo.setSymbol(pcOrderTradeVo.getSymbol());
                detailVo.setQty(pcOrderTradeVo.getVolume());
                detailVo.setAmt(pcOrderTradeVo.getPrice().multiply(pcOrderTradeVo.getVolume()));
                result.add(detailVo);
            }
        }
    }

}
