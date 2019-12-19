/**
 * @author zw
 * @date 2019/8/5
 */
package com.hp.sh.expv3.match.match.core.match.task;

import com.hp.sh.expv3.match.bo.PcOrder4MatchBo;
import com.hp.sh.expv3.match.config.setting.PcmatchRedisKeySetting;
import com.hp.sh.expv3.match.constant.PcmatchConst;
import com.hp.sh.expv3.match.match.core.match.task.def.PcMatchTaskService;
import com.hp.sh.expv3.match.match.core.match.thread.PcMatchHandlerContext;
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
public class PcOrderRebaseTask extends PcOrderBaseTask implements ApplicationContextAware {

    final Logger logger = LoggerFactory.getLogger(getClass());
    private IThreadWorker matchedThreadWorker;

    @Autowired
    private PcMatchTaskService pcMatchTaskService;

    public IThreadWorker getMatchedThreadWorker() {
        return matchedThreadWorker;
    }

    public void setMatchedThreadWorker(IThreadWorker matchedThreadWorker) {
        this.matchedThreadWorker = matchedThreadWorker;
    }

    private List<PcOrder4MatchBo> orders;

    @Autowired
    @Qualifier("threadManagerPcMatchImpl")
    private IThreadManager threadManagerMatchImpl;

    @Override
    public void onSucess() {
    }

    @Override
    public boolean onError(Exception ex) {
        return false;
    }

    @Autowired
    private PcmatchRedisKeySetting pcmatchRedisKeySetting;

    @Autowired
    @Qualifier(PcmatchConst.MODULE_NAME + "RedisUtil")
    private RedisUtil pcRedisUtil;

    @Override
    public void run() {
        PcMatchHandlerContext context = PcMatchHandlerContext.getLocalContext();
        context.limitAskQueue.clear();
        context.limitBidQueue.clear();
        context.allOpenOrders.clear();
        context.setLastPrice(null);
        context.setSentMqOffset(-1L);
        context.clear();
        String snapshotRedisKey = RedisKeyUtil.buildPcOrderSnapshotRedisKey(pcmatchRedisKeySetting.getPcOrderSnapshotRedisKeyPattern(), this.getAsset(), this.getSymbol());
        pcRedisUtil.del(snapshotRedisKey);
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}