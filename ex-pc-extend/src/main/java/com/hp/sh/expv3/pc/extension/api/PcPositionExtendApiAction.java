package com.hp.sh.expv3.pc.extension.api;

import com.hp.sh.expv3.pc.extension.service.PcAccountExtendService;
import com.hp.sh.expv3.pc.extension.service.PcOrderExtendService;
import com.hp.sh.expv3.pc.extension.service.PcOrderTradeService;
import com.hp.sh.expv3.pc.extension.service.PcPositionExtendService;
import com.hp.sh.expv3.pc.extension.vo.CurrentPositionVo;
import com.hp.sh.expv3.pc.extension.vo.PcOrderVo;
import com.hp.sh.expv3.pc.extension.vo.PcPositionVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 永续合约_仓位 扩展接口
 *
 * @author BaiLiJun  on 2019/12/23
 */
@RestController
public class PcPositionExtendApiAction implements PcPositionExtendApi {
    @Autowired
    private PcAccountExtendService pcAccountExtendService;

    @Autowired
    private PcOrderExtendService pcOrderExtendService;

    @Autowired
    private PcPositionExtendService pcPositionExtendService;

    @Autowired
    private PcOrderTradeService pcOrderTradeService;

    /**
     * "id":"1",                            //仓位id
     * "asset":"BTC"                        //资产 asset
     * "symbol"："BTC_USD",                  //交易对 symbol
     * "margin_mode":"1",	             //1:全仓,2:逐仓  margin_mode
     * "avail_qty": "1",                    //可平数量 ? = this.volume - (select * from pc_order where close_pos_id = this.id and user_id = ? and close_flag = 1 and active_flag = 1 ).sum(volume)
     * "entry_price": "0.2985",             //平均开仓价 mean_price
     * "leverage": "5",                     //杠杆 leverage
     * "liquidation_price": "0.0136",       //预估强平价 liq_price
     * "pos_margin": "6.8129",              //仓位保证金 pos_margin
     * "pos_margin_ratio":"0.05"           //保证金率 表示5% (${pos_margin} - ${未实现盈亏} ) / ( faceValue * ${this.volume} / ${当前标记价格})
     * "maintMarginRatio":"0.18"            //维持保证金率 表示18% hold_ratio
     * "qty": "1",                          //持仓量   volume
     * "pos_pnl_ratio":"0.01"               //收益率    表示1% ,${realised_pnl} / init_margin
     * "realised_pnl": "-0.0239",           //已实现盈亏 , (select * from pc_order_trade where pos_id = ? and user_id = ? ).sum(pnl)
     * "pnl":"1.02"                         //未实现盈亏 ? 让老王给你个公式，或现成的类
     * "bid_flag": "1",                     //1多,0空   long_flag
     * "auto_increase_flag": "1",           //1.打开自动追加保证金,0.关闭   auto_add_flag
     * "ctime": "1564369557000"             //开仓时间   created
     * 和老王确认下，这个是提供RPC|http接口还是提供实现类？这里会和某些玩法挂钩
     *
     * @param userId
     * @param asset
     * @param symbol
     * @return
     */
    @Override
    public List<CurrentPositionVo> findCurrentPosition(Long userId, String asset, String symbol) {
        List<CurrentPositionVo> result = new ArrayList<>();
        List<PcPositionVo> list = pcPositionExtendService.findCurrentPosition(userId, asset, symbol);
        if (!CollectionUtils.isEmpty(list)) {
            for (PcPositionVo positionVo : list) {
                //已实现盈亏
                BigDecimal realisedPnl = pcOrderTradeService.getRealisedPnlByPosIdAndUserId(positionVo.getId(), positionVo.getUserId());
                List<PcOrderVo> pcOrderVos = pcOrderExtendService.orderList(positionVo.getId(), positionVo.getUserId());
                BigDecimal volume = pcOrderVos.stream().map(PcOrderVo::getVolume).reduce(BigDecimal.ZERO, BigDecimal::add);
                CurrentPositionVo currentPositionVo = new CurrentPositionVo();
                BeanUtils.copyProperties(positionVo,currentPositionVo);
                currentPositionVo.setRealisedPnl(realisedPnl);
                //可平数量=this.volume -volume
                currentPositionVo.setAvailQty(positionVo.getVolume().subtract(volume));
                currentPositionVo.setMarginMode(positionVo.getMarginMode());
                currentPositionVo.setEntryPrice(positionVo.getMeanPrice());
                currentPositionVo.setLiquidationPrice(positionVo.getLiqPrice());
                currentPositionVo.setMaintMarginRatio(positionVo.getHoldRatio());
                currentPositionVo.setQty(positionVo.getVolume());
                currentPositionVo.setBidFlag(positionVo.getLongFlag());
                currentPositionVo.setAutoIncreaseFlag(positionVo.getAutoAddFlag());
                currentPositionVo.setPosPnlRatio(realisedPnl.divide(positionVo.getInitMargin()));
                Date created = positionVo.getCreated();
               if(created!=null){
                   currentPositionVo.setCtime(created.getTime());
               }
                result.add(currentPositionVo);
            }
        }



        return result;
    }
}
