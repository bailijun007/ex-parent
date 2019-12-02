/**
 * @author zw
 * @date 2019/8/5
 */
package com.hp.sh.expv3.match.match.core.match.handler;

import com.hp.sh.expv3.match.bo.PcOrder4MatchBo;
import com.hp.sh.expv3.match.bo.PcTradeBo;
import com.hp.sh.expv3.match.component.id.def.IdService;
import com.hp.sh.expv3.match.constant.CommonConst;
import com.hp.sh.expv3.match.match.core.match.thread.PcMatchHandlerContext;
import com.hp.sh.expv3.match.util.IdTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.PriorityQueue;

@Service
@Scope("prototype")
public class PcLimitOrderHandler extends PcOrderHandler {
    final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    void matchLimit(PcMatchHandlerContext context, PriorityQueue<PcOrder4MatchBo> makerLimitQueue, PcOrder4MatchBo takerOrder) {

        boolean isBid = (CommonConst.BID == takerOrder.getBidFlag());
        long now = System.currentTimeMillis();
        // taker 剩余未成交量
        BigDecimal takerUnfilledAmount = takerOrder.getNumber().subtract(takerOrder.getFilledNumber());

        while (!makerLimitQueue.isEmpty() && takerUnfilledAmount.compareTo(BigDecimal.ZERO) > 0) {
            // 对手单队列不为空，且taker尚未全部匹配
            PcOrder4MatchBo makerOrder = makerLimitQueue.peek(); // 最优价订单

            // 限价订单判断价格是否匹配
            if (isBid && makerOrder.getPrice().compareTo(takerOrder.getPrice()) > 0) { // 本单欲买,对手卖价更高,不匹配
                break;
            } else if (!isBid && makerOrder.getPrice().compareTo(takerOrder.getPrice()) < 0) { // 本单欲卖,对手买价更低,不匹配
                break;
            }
            // 价格交叉
            // 限价订单按照申报在先定成交价
            BigDecimal makerPrice = makerOrder.getPrice();

            BigDecimal makerUnfilledAmount = makerOrder.getNumber().subtract(makerOrder.getFilledNumber());
            boolean makerCompleted = takerUnfilledAmount.compareTo(makerUnfilledAmount) >= 0;

            BigDecimal currentFillAmount = (makerCompleted) ?
                    // taker >= maker
                    BigDecimal.ZERO.add(makerUnfilledAmount)
                    : // taker < maker
                    BigDecimal.ZERO.add(takerUnfilledAmount);

            takerOrder.setFilledNumber(takerOrder.getFilledNumber().add(currentFillAmount));
            makerOrder.setFilledNumber(makerOrder.getFilledNumber().add(currentFillAmount));

            long tradeId = idService.getId(IdTypeEnum.TRADE);
            PcTradeBo trade = buildTrade(context.getAsset(), context.getSymbol(), tradeId, context.getMatchTxId(), takerOrder, makerOrder, makerPrice, currentFillAmount, takerOrder.getOrderTime());

            appendMatchResult(context, trade);
            context.setLastPrice(makerPrice);

            takerUnfilledAmount = takerUnfilledAmount.subtract(currentFillAmount);
            makerUnfilledAmount = makerUnfilledAmount.subtract(currentFillAmount);

            if (makerCompleted) {
                makerLimitQueue.poll();
                completeOrder(context, makerOrder);
                bookUpdate(context, makerOrder.getId(), makerOrder.getBidFlag(), makerOrder.getPrice(), BigDecimal.ZERO);
                continue;
            } else {
                BigDecimal makerDisplayAmt = makerUnfilledAmount;
                bookUpdate(context, makerOrder.getId(), makerOrder.getBidFlag(), makerOrder.getPrice(), makerDisplayAmt);
                break;
            }
        }
    }

    @Autowired
    private IdService idService;

}