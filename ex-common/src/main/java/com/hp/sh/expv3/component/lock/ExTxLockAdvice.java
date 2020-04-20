package com.hp.sh.expv3.component.lock;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.mybatis.UpdateInterceptor;
import com.hp.sh.expv3.commons.ctx.TxContext;
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

    @Autowired(required=false)
	public void setLocker(Locker locker) {
		super.setLocker(locker);
	}
    
    protected void preLock(long threadId, String lockName, long lockNo, long time, Method method, Object[] args) {
    	String clazzStr = method.getDeclaringClass().getName();
    	String methodStr = method.getName();
    	String lockInfo = " threadId="+threadId+", lockName="+lockName+", time="+time+","+clazzStr+"."+methodStr+"(),args="+Arrays.toString(args);
    	UpdateInterceptor.setVar(lockInfo);
    	
		if(txIdService!=null){
			Long txId = txIdService.getTxId();
			TxContext.setTxId(txId );
		}
		
		TxContext.setLockKey(" threadId="+threadId+", lockName="+lockName+", time="+time+","+clazzStr+"."+methodStr);
		
	}
	
    protected void postLock(long threadId, String lockName, long lockNo, long unTime, Method method, Object[] args) {
		UpdateInterceptor.setVar(null);
		TxContext.setTxId(null);
		
	}
    
}
