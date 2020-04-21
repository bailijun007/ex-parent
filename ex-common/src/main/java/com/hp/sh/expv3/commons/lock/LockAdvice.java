package com.hp.sh.expv3.commons.lock;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;

import com.gitee.hupadev.base.exceptions.CommonError;
import com.hp.sh.expv3.commons.exception.ExException;

public class LockAdvice {
	private static final Logger logger = LoggerFactory.getLogger(LockAdvice.class);
	
	private static final AtomicLong a = new AtomicLong(0L);
	
	private Locker locker;

    public LockAdvice() {
		super();
	}
    
    @Pointcut("@annotation(com.hp.sh.expv3.commons.lock.LockIt)")
    private void lockAnnoPointcut() {

    }
    
    @Pointcut("execution(public * com.hp.sh.expv3..*.*(..))")
    private void lockPackagePointcut() {

    }
    
    @Pointcut("lockPackagePointcut() && lockAnnoPointcut()")
    protected void lockPointcut() {

    }

    @Around("lockPointcut()")
    public Object exeLock(ProceedingJoinPoint joinPoint) throws Throwable {
		return this.doExeLock(joinPoint);
    }
    
    protected Object doExeLock(ProceedingJoinPoint joinPoint) throws Throwable {
		if(this.locker == null){
			return joinPoint.proceed(joinPoint.getArgs());
		}
		String locakName=null;
		long time = 0 ;
		long threadId = Thread.currentThread().getId();
		Method method = null;
		Object[] args = null;
		long lockNo = -1L; //a.getAndIncrement();
		Lock lock = null;
		boolean isLocked=false;
		try{
			args = joinPoint.getArgs();
			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			method = signature.getMethod();
			LockIt lockAnno = AnnotationUtils.findAnnotation(method, LockIt.class);
			String _configKey = lockAnno.key();
			String[] _names = signature.getParameterNames();
			locakName = getLockName(_configKey, args, _names);
			lock = this.locker.getLock(locakName);
			isLocked = lock.tryLock(10, TimeUnit.SECONDS);
			if(!isLocked){
				throw new ExException(CommonError.LOCK, locakName);
			}
			
			time = System.currentTimeMillis();
			this.preLock(threadId, locakName, lockNo, time, method, args);
			logger.debug("lock:\"{}\", {}, {}, {}, {}", locakName, lockNo, threadId, time, method);
			Object result = joinPoint.proceed(args);
			return result;
		}finally{
			if(locakName!=null){
				try{
					long unTime = System.currentTimeMillis();
					this.postLock(threadId, locakName, lockNo, unTime, method, args);
					logger.debug("unlock:\"{}\", {}, {}, {}, {}, {}", locakName, lockNo, threadId, unTime, (unTime-time), method);
					if(lock!=null && isLocked){
						lock.unlock();
					}
					if(lockNo>=Long.MAX_VALUE-10000){
						a.set(0L);
					}
				}catch(Exception e){
					logger.error("解锁失败：{}, {}", locakName, e);
					throw e;
				}
			}
		}      
    }

    protected void preLock(long threadId, String realKey, long lockNo, long time, Method method, Object[] args) {
	}
	
    protected void postLock(long threadId, String realKey, long lockNo, long unTime, Method method, Object[] args) {
	}

	private static String getLockName(String key, Object[] args, String[] names) throws Exception{
//		Pattern pattern = Pattern.compile("\\$?\\{(\\w+)\\}", Pattern.MULTILINE|Pattern.DOTALL);
		Pattern pattern = Pattern.compile("\\$?\\{(\\w+)(?:\\.(\\w+))?\\}", Pattern.MULTILINE|Pattern.DOTALL);
		Matcher matcher = pattern.matcher(key);
		StringBuilder sb = new StringBuilder();
		int start = 0;
		while(matcher.find()){
			sb.append(key.substring(start, matcher.start()));
			String var = matcher.group(1);
			int index;
			if(Pattern.matches("\\d+", var)){
				index = Integer.parseInt(var);
			}else{
				index = findIndex(var, names);
			}
			Object val = args[index];
			
			String varpp = matcher.group(2);
			if(varpp!=null && varpp.length()>0){
				val = PropertyUtils.getSimpleProperty(val, varpp);
			}
			if(val==null){
				logger.error("lock key var is null：key={},var={},pvar={}", key, var, varpp);
			}
			sb.append(val);
			start = matcher.end();
		}
		sb.append(key.substring(start));
		return sb.toString();
	}
	
	private static int findIndex(String name, String[] names){
		for(int i=0;i<names.length;i++){
			if(name.equals(names[i])){
				return i;
			}
		}
		return -1;
	}
	
	protected void setLocker(Locker locker) {
		if(this.locker!=null){
			throw new RuntimeException("locker exist.");
		}
		this.locker = locker;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(getLockName("xx-{1}-{name.empty}-oo", new Object[]{100,"test"}, new String[]{"id","name"}));
		System.out.println(getLockName("test", null, null));
	}
}
