package com.hp.sh.expv3.pc.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.id.IdGenerator;
import com.hp.sh.expv3.config.db.WorkerConfigBuilder;

/**
 * 选择收费员
 * @author wangjg
 *
 */
@Component
public class TxIdService {
	
    @Autowired
    private IdGenerator idGenerator;
	
	public Long getTxId(){
		return idGenerator.nextId(WorkerConfigBuilder.TX);
	}

}
