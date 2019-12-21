package com.hp.sh.expv3.pc.mq.liq;

import org.springframework.stereotype.Component;

import com.hp.sh.expv3.pc.constant.MqTags;
import com.hp.sh.expv3.pc.mq.BaseMqSender;
import com.hp.sh.expv3.pc.mq.liq.msg.LiqLockMsg;

/**
 * 强平消息
 * @author wangjg
 *
 */
@Component
public class LiqMqSender extends BaseMqSender{
	
	public LiqMqSender() {
	}
	
	public void sendLiqLockMsg(LiqLockMsg lockMsg){
        super.sendOrderMsg(lockMsg, MqTags.TAGS_PC_POS_LIQ_LOCKED, ""+lockMsg.keys());
	}
    
}
