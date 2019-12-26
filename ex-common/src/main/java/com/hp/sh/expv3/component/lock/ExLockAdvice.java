package com.hp.sh.expv3.component.lock;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.hp.sh.expv3.commons.lock.LockAdvice;

@Order(Ordered.LOWEST_PRECEDENCE)
@Aspect
@Component
public class ExLockAdvice extends LockAdvice {
    
    public ExLockAdvice() {
		super();
	}
    
}
