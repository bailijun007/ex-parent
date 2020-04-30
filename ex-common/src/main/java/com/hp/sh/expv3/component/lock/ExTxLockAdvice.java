package com.hp.sh.expv3.component.lock;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.gitee.hupadev.commons.mybatis.UpdateInterceptor;
import com.hp.sh.expv3.commons.ctx.TxContext;
import com.hp.sh.expv3.commons.lock.LockAdvice;
import com.hp.sh.expv3.commons.lock.Locker;


@Order(Ordered.HIGHEST_PRECEDENCE)
@Aspect
@Component
public class ExTxLockAdvice extends LockAdvice {
    
	@Autowired(required=false)
	private TxIdService txIdService;
	
	@Value("${lock.use.anno:true}")
	private Boolean useLockAnno;
	
    public ExTxLockAdvice() {
		super();
	}

    @Autowired(required=false)
	public void setLocker(Locker locker) {
    	if(Boolean.TRUE.equals(useLockAnno)){
    		super.setLocker(locker);
    	}
	}
    
    protected Object doExeLock(ProceedingJoinPoint joinPoint) throws Throwable {
		if(txIdService!=null){
			Long txId = txIdService.getTxId();
			TxContext.setTxId(txId );
		}
		try{
			return super.doExeLock(joinPoint);
		}finally{
			TxContext.setTxId(null);
		}
    }
    
    protected void preLock(long threadId, String lockName, long lockNo, long time, Method method, Object[] args) {
		
//    	String clazzStr = method.getDeclaringClass().getName();
//    	String methodStr = method.getName();
//    	String lockInfo = " threadId="+threadId+", lockName="+lockName+",lockNo="+lockNo+", time="+time+","+clazzStr+"."+methodStr+"(),args="+Arrays.toString(args);
//    	UpdateInterceptor.setCtxVar(lockInfo);
//		
//		TxContext.setLockKey(" threadId="+threadId+", lockName="+lockName+",lockNo="+lockNo+", time="+time+","+clazzStr+"."+methodStr);
		
	}
	
    protected void postLock(long threadId, String lockName, long lockNo, long unTime, Method method, Object[] args) {
		UpdateInterceptor.setCtxVar(null);
	}
    
}
