/**
 * @author zw
 * @date 2019/8/6
 */
package com.hp.sh.expv3.match.match.core.matched.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class BbMatchedInitTask extends BbMatchedBaseTask {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onSucess() {
    }

    @Override
    public boolean onError(Exception ex) {
        return false;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("BbMedWok-" + getAssetSymbol());
    }

}