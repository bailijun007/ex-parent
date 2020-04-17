package com.hp.sh.expv3.component.lock;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.commons.ctx.TxIdContext;
import com.hp.sh.expv3.commons.lock.LockAdvice;
import com.hp.sh.expv3.commons.lock.Locker;


@Order(Ordered.LOWEST_PRECEDENCE)
@Aspect
@Component
public class ExTxLockAdvice extends LockAdvice {
    
	@Autowired(required=false)
	private TxIdService txIdService;
	
    public ExTxLockAdvice() {
		super();
	}
    
    public Object doExeLock(ProceedingJoinPoint joinPoint) throws Throwable {
    	try{
    		if(txIdService!=null){
    			Long txId = txIdService.getTxId();
				TxIdContext.setTxId(txId );
    		}
    		return super.doExeLock(joinPoint);
    	}finally{
    		TxIdContext.reset();
		}   
    }

    @Autowired(required=false)
	public void setLocker(Locker locker) {
		super.setLocker(locker);
	}
}
