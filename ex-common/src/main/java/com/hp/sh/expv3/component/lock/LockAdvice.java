package com.hp.sh.expv3.component.lock;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Aspect
@Component
public class LockAdvice {
	
	@Autowired(required=false)
	private DistributedLocker distributedLocker;

    public LockAdvice() {
		super();
	}
    
    @Pointcut("@annotation(com.hp.sh.expv3.component.lock.LockIt)")
    public void lockAnnoPointcut() {

    }
    
    @Pointcut("execution(public * com.hp.sh.expv3.pc.*.*(..))")
    public void lockPackagePointcut() {

    }
    
    @Pointcut("lockAnnoPointcut() && lockPackagePointcut()")
    public void lockPointcut() {

    }

//    @Around("lockPointcut()")
	@Around("@annotation(com.hp.sh.expv3.component.lock.LockIt)")
    public Object exeLock(ProceedingJoinPoint joinPoint) throws Throwable {
		if(distributedLocker == null){
			return joinPoint.proceed(joinPoint.getArgs());
		}
		String realKey=null;
		try{
			Object[] args = joinPoint.getArgs();
			MethodSignature signature = (MethodSignature) joinPoint.getSignature();
			Method method = signature.getMethod();
			LockIt lockIt = AnnotationUtils.findAnnotation(method, LockIt.class);
			String configKey = lockIt.key();
			String[] names = signature.getParameterNames();
			realKey = getRealKey(configKey, args, names);
			this.lock(realKey);
			Object result = joinPoint.proceed(args);
			return result;
		}finally{
			if(realKey!=null){
				this.unlock(realKey);
			}
			System.out.println("unlock it");
		}      
    }
	
	private void lock(String realKey) {
		this.distributedLocker.lock(realKey, 30);
	}

	private void unlock(String realKey) {
		this.distributedLocker.unlock(realKey);
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
			
			String pvar = matcher.group(2);
			if(pvar!=null && pvar.length()>0){
				val = PropertyUtils.getSimpleProperty(val, pvar);
			}
			sb.append(val);
			start = matcher.end();
		}
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
		System.out.println(getRealKey("xx-{1}-{name.empty}", new Object[]{100,"test"}, new String[]{"id","name"}));
	}
}
