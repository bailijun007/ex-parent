package com.hp.sh.expv3.pc.atemp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Deprecated
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
public @interface Question {
	
	String value() default "";

}
