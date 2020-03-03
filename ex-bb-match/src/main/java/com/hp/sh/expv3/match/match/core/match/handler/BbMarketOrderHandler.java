/**
 * @author zw
 * @date 2019/8/5
 */
package com.hp.sh.expv3.match.match.core.match.handler;

import com.hp.sh.expv3.enums.IdTypeEnum;
import com.hp.sh.expv3.match.bo.BbOrder4MatchBo;
import com.hp.sh.expv3.match.bo.BbTradeBo;
import com.hp.sh.expv3.match.component.id.def.IdService;
import com.hp.sh.expv3.match.constant.CommonConst;
import com.hp.sh.expv3.match.match.core.match.thread.BbMatchHandlerContext;
import com.hp.sh.expv3.match.util.BbUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.PriorityQueue;

@Service
public class BbMarketOrderHandler extends BbOrderHandler {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IdService idService;

    @Override
    void matchLimit(BbMatchHandlerContext context, PriorityQueue<BbOrder4MatchBo> makerLimitQueue, BbOrder4MatchBo takerOrder) {

        boolean isTakerBid = (CommonConst.BID == takerOrder.getBidFlag());
        long now = System.currentTimeMillis();
        // taker 剩余未成交量
        BigDecimal takerUnfilledAmount = takerOrder.getNumber().subtract(takerOrder.getFilledNumber());

        while (!makerLimitQueue.isEmpty() && takerUnfilledAmount.compareTo(BigDecimal.ZERO) > 0) {
            // 对手单队列不为空，且taker尚未全部匹配
            BbOrder4MatchBo makerOrder = makerLimitQueue.peek(); // 最优价订单

            // 市价订单判断价格是否匹配，如果价格有值的话
            if (Optional.ofNullable(takerOrder.getPrice()).orElse(BigDecimal.ZERO).compareTo(BigDecimal.ZERO) > 0) {
                if (isTakerBid && makerOrder.getPrice().compareTo(takerOrder.getPrice()) > 0) { // 本单欲买,对手卖价更高,不匹配
                    break;
                } else if (!isTakerBid && makerOrder.getPrice().compareTo(takerOrder.getPrice()) < 0) { // 本单欲卖,对手买价更低,不匹配
                    break;
                }
            }

            // 市价订单按照申报在先定成交价
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

            long tradeId = idService.getId(IdTypeEnum.BB_TRADE);
            BbTradeBo trade = buildTrade(context.getAsset(), context.getSymbol(), tradeId, context.getMatchResult().getMatchTxId(), takerOrder, makerOrder, makerPrice, currentFillAmount, takerOrder.getOrderTime());

            appendMatchResult(context, trade);

            takerUnfilledAmount = takerUnfilledAmount.subtract(currentFillAmount);

            if (makerCompleted) {
                makerLimitQueue.poll();
                completeOrder(context, makerOrder);
                bookUpdate(context, makerOrder.getOrderId(), makerOrder.getBidFlag(), makerOrder.getPrice(), BigDecimal.ZERO);
                continue;
            } else {
                BigDecimal makerBookNumber = BbUtil.calcBookNumber(makerOrder.getNumber(), makerOrder.getFilledNumber(), makerOrder.getDisplayNumber());
                bookUpdate(context, makerOrder.getOrderId(), makerOrder.getBidFlag(), makerOrder.getPrice(), makerBookNumber);
                break;
            }
        }
    }

}