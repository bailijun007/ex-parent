package com.hp.sh.expv3.pc.component.lock;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Aspect
@Component
public class LockAdvice {

    public LockAdvice() {
		super();
	}

	@Around("@annotation(com.hp.sh.expv3.pc.component.lock.LockIt)")
    public Object exeLock(ProceedingJoinPoint joinPoint) throws Throwable {
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
		// TODO Auto-generated method stub
		
	}

	private void unlock(String realKey) {
		// TODO Auto-generated method stub
		
	}

	private static String getRealKey(String key, Object[] args, String[] names){
		Pattern pattern = Pattern.compile("\\$?\\{(\\w+)\\}", Pattern.MULTILINE|Pattern.DOTALL);
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
	
	public static void main(String[] args) {
		System.out.println(getRealKey("xx-{1}-{id}", new Object[]{100,200}, new String[]{"id","name"}));
	}
}
