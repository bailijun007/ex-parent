package com.hp.sh.expv3.dev;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
public @interface Question {

	//ask
	String value() default "";
	
	//ask
	String ask() default "";
	
	//answer
	String answer() default "";

}
