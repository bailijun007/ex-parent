/**
 * @author zw
 * @date 2019/8/5
 */
package com.hp.sh.expv3.match.match.core.match.task;

import com.hp.sh.expv3.match.bo.BbOrder4MatchBo;
import com.hp.sh.expv3.match.config.setting.BbmatchRedisKeySetting;
import com.hp.sh.expv3.match.constant.BbmatchConst;
import com.hp.sh.expv3.match.match.core.match.task.def.BbMatchTaskService;
import com.hp.sh.expv3.match.match.core.match.thread.BbMatchHandlerContext;
import com.hp.sh.expv3.match.thread.def.IThreadManager;
import com.hp.sh.expv3.match.thread.def.IThreadWorker;
import com.hp.sh.expv3.match.util.RedisKeyUtil;
import com.hp.sh.expv3.match.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Scope("prototype")
public class BbOrderRebaseTask extends BbOrderBaseTask implements ApplicationContextAware {

    final Logger logger = LoggerFactory.getLogger(getClass());
    private IThreadWorker matchedThreadWorker;

    @Autowired
    private BbMatchTaskService bbMatchTaskService;

    public IThreadWorker getMatchedThreadWorker() {
        return matchedThreadWorker;
    }

    public void setMatchedThreadWorker(IThreadWorker matchedThreadWorker) {
        this.matchedThreadWorker = matchedThreadWorker;
    }

    private List<BbOrder4MatchBo> orders;

    @Autowired
    @Qualifier("threadManagerBbMatchImpl")
    private IThreadManager threadManagerMatchImpl;

    @Override
    public void onSucess() {
    }

    @Override
    public boolean onError(Exception ex) {
        return false;
    }

    @Autowired
    private BbmatchRedisKeySetting bbmatchRedisKeySetting;

    @Autowired
    @Qualifier(BbmatchConst.MODULE_NAME + "RedisUtil")
    private RedisUtil bbRedisUtil;

    @Override
    public void run() {
        BbMatchHandlerContext context = BbMatchHandlerContext.getLocalContext();
        context.limitAskQueue.clear();
        context.limitBidQueue.clear();
        context.allOpenOrders.clear();
        context.setLastPrice(null);
        context.setSentMqOffset(-1L);
        context.clear();
        String snapshotRedisKey = RedisKeyUtil.buildBbOrderSnapshotRedisKey(bbmatchRedisKeySetting.getBbOrderSnapshotRedisKeyPattern(), this.getAsset(), this.getSymbol());
        bbRedisUtil.del(snapshotRedisKey);
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}