/**
 * @author 10086
 * @date 2019/10/24
 */
package com.hp.sh.expv3.match.match.core.match.task.service;

import com.hp.sh.expv3.match.bo.PcOrder4MatchBo;
import com.hp.sh.expv3.match.msg.BookMsgDto.BookEntry;
import com.hp.sh.expv3.match.util.PcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class PcOrderBookEventService {

    final Logger logger = LoggerFactory.getLogger(getClass());

    public List<BookEntry> buildBookEntry4Cancel(Long accountId, Long orderId) {
        List<BookEntry> ens = new ArrayList<>(1);
        BookEntry bookEntry = new BookEntry(orderId, BigDecimal.ZERO);
        ens.add(bookEntry);
        return ens;
    }

    public List<BookEntry> buildBookEntry4CancelByLiq(Collection<PcOrder4MatchBo> orders) {
        List<BookEntry> ens = new ArrayList<>();
        for (PcOrder4MatchBo order : orders) {
            BookEntry bookEntry = new BookEntry(order.getOrderId(), order.getPrice(), BigDecimal.ZERO, order.getBidFlag());
            ens.add(bookEntry);
        }
        return ens;
    }

    public List<BookEntry> buildBookEntry(PriorityQueue<PcOrder4MatchBo>... queues) {
        List<BookEntry> ens = new ArrayList<>();
        Map<Long, PcOrder4MatchBo> entries = new HashMap<>();
        for (PriorityQueue<PcOrder4MatchBo> queue : queues) {
            for (PcOrder4MatchBo order : queue) {
                if (entries.containsKey(order.getOrderId())) {
                    logger.error("order duplicated,asset:{},symbol:{},accountId:{},orderId:{}.", order.getAsset(), order.getSymbol(), order.getAccountId(), order.getOrderId());
                    throw new RuntimeException();
                }
                entries.put(order.getOrderId(), order);
                BigDecimal bookNumber = PcUtil.calcBookNumber(order.getNumber(), order.getFilledNumber(), order.getDisplayNumber());
                if (bookNumber.compareTo(BigDecimal.ZERO) > 0) {
                    BookEntry entry = new BookEntry(order.getOrderId(), order.getPrice(), bookNumber, order.getBidFlag());
                    ens.add(entry);
                } else {
                    logger.error("display amt<0,asset:{},symbol:{},accountId:{},orderId:{}", order.getAsset(), order.getSymbol(), order.getAccountId(), order.getOrderId());
                    throw new RuntimeException();
                }
            }
        }
        return ens;
    }

}
