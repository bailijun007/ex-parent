package com.hp.sh.expv3.bb.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.id.IdGenerator;
import com.hp.sh.expv3.component.lock.TxIdService;
import com.hp.sh.expv3.config.db.WorkerConfigBuilder;

/**
 * TxId
 * @author wangjg
 *
 */
@Component
public class BBTxIdService implements TxIdService {
	
    @Autowired
    private IdGenerator idGenerator;
	
	@Override
	public Long getTxId(){
		return idGenerator.nextId(WorkerConfigBuilder.TX);
	}

}
