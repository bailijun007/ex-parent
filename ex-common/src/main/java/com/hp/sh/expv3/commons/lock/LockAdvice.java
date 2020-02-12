package com.hp.sh.expv3.commons.lock;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;

public class LockAdvice {
	private static final Logger logger = LoggerFactory.getLogger(LockAdvice.class);
	
	@Autowired(required=false)
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
    
    public Object doExeLock(ProceedingJoinPoint joinPoint) throws Throwable {
		if(this.locker == null){
			return joinPoint.proceed(joinPoint.getArgs());
		}
		String realKey=null;
		long time = 0 ;
		try{
			Object[] args = joinPoint.getArgs();
			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			Method method = signature.getMethod();
			LockIt lockIt = AnnotationUtils.findAnnotation(method, LockIt.class);
			String configKey = lockIt.key();
			String[] names = signature.getParameterNames();
			realKey = getRealKey(configKey, args, names);
			time = System.currentTimeMillis();
			logger.debug("lock:{},{},{}", realKey, method, time);
			this.lock(realKey);
			Object result = joinPoint.proceed(args);
			return result;
		}finally{
			if(realKey!=null){
				try{
					this.unlock(realKey);
					time = System.currentTimeMillis()-time;
					logger.debug("unlock：{},{},{},{}", realKey, time, (time/1000), joinPoint);
				}catch(Exception e){
					logger.error("解锁失败：{}", realKey, e);
					throw e;
				}
			}
		}      
    }
	
	private void lock(String realKey) {
		this.locker.lock(realKey, 60);
	}

	private void unlock(String realKey) {
		this.locker.unlock(realKey);
	}

	private static String getRealKey(String key, Object[] args, String[] names) throws Exception{
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
	
	public static void main(String[] args) throws Exception {
		System.out.println(getRealKey("xx-{1}-{name.empty}-oo", new Object[]{100,"test"}, new String[]{"id","name"}));
		System.out.println(getRealKey("test", null, null));
	}
}
