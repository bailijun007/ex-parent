/**
 * @author 10086
 * @date 2019/10/25
 */
package com.hp.sh.expv3.match.match.core.match.thread.impl;

import com.hp.sh.expv3.match.component.notify.PcOrderMqNotify;
import com.hp.sh.expv3.match.match.core.order.OrderInitializer;
import com.hp.sh.expv3.match.thread.def.IThreadManager;
import com.hp.sh.expv3.match.util.PcUtil;
import com.hp.sh.expv3.match.util.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PcMatchAppendTriggerThread {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrderInitializer orderInitializer;

    @Scheduled(initialDelay = 60000, fixedDelay = 60000)
    private void process() {
        trigger();
    }

    public void trigger() {
        orderInitializer.start(false);
    }

}